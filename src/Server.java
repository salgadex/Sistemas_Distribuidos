import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

class ContactManager {
    private HashMap<String, Contact> contacts = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    // @TODO
    public void update(Contact c) {
        lock.lock();
        try{
            contacts.put(c.name(), c);
        }
        finally {
            lock.unlock();
        }
    }



    // @TODO
    public ContactList getContacts() {
        ContactList contactList = new ContactList();

        for (Contact contact : contacts.values()) {
            contactList.add(contact);
        }

        return contactList;
    }
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactManager manager;

    public ServerWorker(Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    // @TODO
    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            boolean open = true;
            while (open) {
                try {
                    Contact contact = Contact.deserialize(in);
                    if(contact != null) {
                        System.out.println(contact.toString());
                        manager.update(contact);
                        System.out.println("Contato adicionado: " + contact);
                    }
                    else
                    {
                        open = false;
                    }

                    socket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();
        // example pre-population
        manager.update(new Contact("John", 20, 253123321, null, Arrays.asList("john@mail.com")));
        manager.update(new Contact("Alice", 30, 253987654, "CompanyInc.", Arrays.asList("alice.personal@mail.com", "alice.business@mail.com")));
        manager.update(new Contact("Bob", 40, 253123456, "Comp.Ld", Arrays.asList("bob@mail.com", "bob.work@mail.com")));

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }
}
