import java.io.*;
import java.net.*;

public class Client {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;

    // Construtor para colocar o endereço IP e a porta
    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            String line = "";

            while (true) {
                try {
                    System.out.print("Enter a number (or press Enter to finish): ");
                    line = input.readLine();

                    // Verifica se a linha está vazia para finalizar o envio
                    if (line.isEmpty()) {
                        out.writeUTF("EOF");  // Envia "EOF" para indicar o fim dos números
                        String finalResponse = in.readUTF();  // Lê a média
                        System.out.println(finalResponse);
                        break;
                    }

                    out.writeUTF(line); // Envia o número para o servidor
                    String response = in.readUTF(); // Recebe a soma acumulada do servidor
                    System.out.println(response);
                } catch (IOException i) {
                    System.out.println(i);
                    break;
                }
            }

            // Fecha as conexões
            input.close();
            out.close();
            in.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 5000);
    }
}
