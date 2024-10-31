import java.net.*;
import java.io.*;

public class Server {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // Construtor com porta
    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            // Aceita a conexão do cliente
            socket = server.accept();
            System.out.println("Client accepted");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            int sum = 0;
            int count = 0;

            while (true) {
                try {
                    // Lê a entrada do cliente
                    String message = in.readUTF();

                    // Verifica se é o sinal de fim
                    if (message.equals("EOF")) {
                        double average = count > 0 ? (double) sum / count : 0;
                        out.writeUTF("Média dos números recebidos: " + average);
                        break;
                    }

                    // Caso contrário, trata como um número e atualiza a soma e contagem
                    int number = Integer.parseInt(message);
                    sum += number;
                    count++;

                    // Envia a soma acumulada ao cliente
                    out.writeUTF("Soma acumulada: " + sum);
                } catch (IOException e) {
                    System.out.println(e);
                    break;
                }
            }

            System.out.println("Closing connection");

            // Fecha as conexões
            socket.close();
            in.close();
            out.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        Server server = new Server(5000);
    }
}
