package chat;

import sockets.*;

import java.io.IOException;
import java.util.ArrayList;

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

                    if (msg.equals("/leave")) {
                        removeClient();
                        return;
                    }else if(msg.equals("file")){
                        fileHandler();
                    }

                    for (Client c : clients) {
                        if (c != client) {
                            c.sendMessage(username + ": " + msg);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("The connection to the client was lost");
                    clients.remove(client);
                    return;
                }
            }
        }

        private void removeClient() throws IOException {
            clients.remove(client);

            for (Client c : clients) {
                c.sendMessage(username + " left the chat");
            }
        }

        private void fileHandler() throws IOException{
            client.sendMessage("Please send your file now");
        }

    }
}
