import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket sSock = new ServerSocket(12345);
        int sum, n;

        while (true) {
            Socket clSock = sSock.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clSock.getInputStream()));
            PrintWriter out = new PrintWriter(clSock.getOutputStream());

            sum = 0;
            n = 0;
            String line = "";

            while ((line = in.readLine()) != null) {

                try {
                    sum += Integer.parseInt(line);
                    n++;
                } catch (NumberFormatException e) {

                }
                out.println(sum);
                out.flush();
            }

            if (n > 0) {
                out.println(sum / n);
                out.flush();
            } else {
                out.println(0);
                out.flush();
            }

            clSock.shutdownOutput();
            clSock.shutdownInput();
            clSock.close();

        }
    }
}

//Thread thread = new Thread(new Runn(clSock));