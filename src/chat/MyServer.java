package chat;

import sockets.*;

import java.io.IOException;

public class MyServer {
    public static void main(String[] args) {
        try {
            Server server = new Server(42069);
            Client client = new Client(server.acceptConnection());
            System.out.println("New Client");

            System.out.println(client.receiveMessage());
            System.out.println(client.receiveMessage());

            client.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
