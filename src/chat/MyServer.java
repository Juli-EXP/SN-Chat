package chat;

import sockets.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyServer {
    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Server server = new Server(42069);
            System.out.println("Server is ready");

            while (true) {
                Thread thread = new Thread(new ClientHandler(new Client(server.acceptConnection())));
                System.out.println("A new client was added to the chat");
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler implements Runnable {
        private Client client;
        private String username = "";

        public ClientHandler(Client client) {
            this.client = client;
            clients.add(client);
        }

        @Override
        public void run() {
            String msg;

            try {
                username = client.receiveMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    msg = client.receiveMessage();
                    System.out.println("Received message: " + msg + "; from: " + username);

                    switch (msg) {
                        case "/leave":
                            clients.remove(client);
                            for (Client c : clients)
                                c.sendMessage(username + " left the chat");
                            return;
                        case "/file":
                            handleFile();
                            break;
                        default:
                            for (Client c : clients) {
                                if (c != client) {
                                    c.sendMessage(username + ": " + msg);
                                }
                            }
                            break;
                    }

                } catch (IOException e) {
                    System.out.println("The connection to the client was lost");
                    clients.remove(client);
                    return;
                }
            }
        }

        private void handleFile() throws IOException {
            String filename;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();

            Files.createDirectories(Paths.get("download"));

            if (Files.notExists(Paths.get("download"))) {
                System.err.println("Couldn't create download folder");
                client.sendMessage("An error occurred while sending the file, please try again");
                return;
            }

            filename = client.receiveFile("download/");

            Path path = Paths.get("download/" + filename);
            Files.move(path, path.resolveSibling(username + "_" + dateFormat.format(date) + "_" + filename),
                    StandardCopyOption.REPLACE_EXISTING);

            filename = username + "_" + dateFormat.format(date) + "_" + filename;

            for (Client c : clients) {
                if (c != client) {
                    c.sendMessage("/file");
                    c.sendFile("download/" + filename);
                }
            }

        }
    }
}