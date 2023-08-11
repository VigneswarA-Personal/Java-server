import java.io.*;
import java.net.*;
import java.util.*;

class Server extends Thread {
    ServerSocket server;
    boolean listening;

    Server(ServerSocket server) {
        this.server = server;
        this.listening = true;
    }

    public void run() {
        while (listening) {
            try {
                Socket client = server.accept();
                new ClientHandler(client).start();
                System.out.println(String.format("Received Connection from %s", client.getInetAddress()));
            } catch (IOException e) {
                System.out.println("Attempt to connection failed!");
            }
        }
    }

    public static void main(String args[]) throws Exception {
        new Server(new ServerSocket(8000)).start();
    }
}

class ClientHandler extends Thread {
    Socket client;
    InputStreamReader reader;
    PrintWriter writer;
    HashMap<String, ArrayList<Socket>> groups;
    HashMap<String, Socket> users;

    ClientHandler(Socket client) {
        try {
            this.client = client;
            this.reader = new InputStreamReader(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Client Handler has failed!");
        }
    }

    public void run() {
        while (true) {
            try {
                if (reader.ready()) {
                    char buffer[] = new char[1024];
                    reader.read(buffer, 0, 1024);
                    Message message = new Message(new String(buffer));
                    switch (message.type) {
                        case "PM":
                            new SendMessage(message, users.get(message.receiver)).start();
                            break;
                        case "GM":
                            for (Socket recepient : groups.get(message.receiver)) {
                                new SendMessage(message, recepient).start();
                            }
                            break;
                        case "PA":
                            users.put(message.sender, client);
                            break;
                        case "GA":
                            if (!groups.containsKey(message.receiver))
                                groups.put(message.sender, new ArrayList<Socket>());
                            groups.get(message.receiver).add(client);
                            break;

                    }
                }
            } catch (IOException e) {
                System.out.println(String.format("Unable to reach %s", client.getInetAddress()));
            }
        }
    }
}

class SendMessage extends Thread {
    Message message;
    Socket receiver;

    SendMessage(Message message, Socket receiver) {
        this.message = message;
        this.receiver = receiver;
    }

    public void run() {
        try {
            PrintWriter writer = new PrintWriter(receiver.getOutputStream());
            while (true) {

                writer.write(message.toString());
                writer.flush();
                receiver.close();
                break;
            }
        } catch (Exception e1) {
            System.out.println(String.format("%s could not be reached!", receiver.getInetAddress()));
        }
    }
}