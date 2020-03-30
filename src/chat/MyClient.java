package chat;

import sockets.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyClient {
    private static Client client;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        String ip;
        String username;

        try {
            System.out.println("Enter the ip address of the Server");
            ip = br.readLine();

            client = new Client(ip, 42069);

            System.out.println("Enter your Username: ");
            username = br.readLine();
            client.sendMessage(username);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientReader implements Runnable {

        @Override
        public void run() {

        }
    }

    private static class ClientWriter implements Runnable {

        @Override
        public void run() {

        }
    }
}


