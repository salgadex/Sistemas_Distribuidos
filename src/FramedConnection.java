import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {

    private Socket socket;
    private ReentrantLock wlock;
    private ReentrantLock rlock;
    private DataInputStream in;
    private DataOutputStream out;

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.wlock = new ReentrantLock();
        this.rlock = new ReentrantLock();
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void send(byte[] data) throws IOException {
        this.wlock.lock();
        try{
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        }
        finally{
            this.wlock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        this.rlock.lock();
        try{
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return data;
        }
        finally{
            this.rlock.unlock();
        }
    }

    public void close() throws IOException {
        this.rlock.lock();
        this.wlock.lock();
        socket.close();
        this.rlock.unlock();
        this.wlock.unlock();
    }
}
