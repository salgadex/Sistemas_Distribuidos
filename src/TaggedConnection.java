import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ReentrantLock wlock;
    private ReentrantLock rlock;

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.wlock = new ReentrantLock();
        this.rlock = new ReentrantLock();
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }
   public static class Frame {
        public int lag;   // Lag is the tag, an integer
        public final byte[] data;

        public Frame(int lag, byte[] data) {
            this.lag = lag;
            this.data = data;
        }
    }

    public void send(Frame frame) throws IOException {
        this.wlock.lock();
        try {
            out.writeInt(frame.lag);
            out.writeInt(frame.data.length);
            out.write(frame.data);
            out.flush();
        } finally {
            this.wlock.unlock();
        }
    }

    public void send(int lag, byte[] data) throws IOException {
        this.send(new Frame(lag, data));
    }

    public Frame receive() throws IOException {
        this.rlock.lock();
        try {
            int lag = in.readInt();
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);
            return new Frame(lag, data);
        } finally {
            this.rlock.unlock();
        }
    }

    public void close() throws IOException {
        this.rlock.lock();
        this.wlock.lock();
        try {
            socket.close();
        } finally {
            this.rlock.unlock();
            this.wlock.unlock();
        }
    }
}
