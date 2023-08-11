import java.io.*;
import java.net.*;
import java.util.*;

class Client extends Thread {
    Socket socket;
    boolean running;
    PrintWriter writer;

    Client(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.running = true;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.print("You> ");
            String data = scanner.nextLine();
            Message message;
            try {
                message = new Message("PM", "USER1", "USER1", data);
            } catch (Exception e) {
                continue;
            }
            writer.write(message.toString());
            writer.flush();
        }
        scanner.close();
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client(new Socket(InetAddress.getLocalHost(), 8000));
        client.start();
        new ListenMessages(client).start();
    }
}

class ListenMessages extends Thread {
    Client client;
    boolean listening;
    InputStreamReader reader;

    ListenMessages(Client client) throws IOException {
        this.client = client;
        this.reader = new InputStreamReader(client.socket.getInputStream());
    }

    public void run() {

        try {
            while (client.running) {
                if (reader.ready()) {
                    char buffer[] = new char[1024];
                    reader.read(buffer, 0, 1024);
                    String message = (new String(buffer)).trim();
                    if (message.equals(""))
                        continue;
                    System.out.println(message);
                    sleep(100);
                }
            }
        } catch (IOException e) {
            System.out.println("Lost connection from the server");
        } catch (InterruptedException e) {
            client.running = false;
        }
    }
}