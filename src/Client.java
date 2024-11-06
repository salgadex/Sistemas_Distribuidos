import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 12345);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String line = "";

        while ((line = stdIn.readLine()) != null) {
            out.println(line);
            out.flush();
            System.out.println("Sum: " + in.readLine());
        }
        socket.shutdownOutput();
        System.out.println("avg: " + in.readLine());
        socket.close();

    }
}