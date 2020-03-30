package chat;

import sockets.Client;

import java.io.IOException;

public class MyClient {
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 42069);
            client.sendMessage("hund");
            client.sendFile("hunde");


            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
