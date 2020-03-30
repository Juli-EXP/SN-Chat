package chat;

import sockets.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyClient {
    private static Client client;
    private static boolean stop = false;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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

        Thread reader = new Thread(new ClientReader());
        reader.start();
        Thread writer = new Thread(new ClientWriter());
        writer.start();
    }

    public static void stopThread(){
        stop = true;
    }

    private static class ClientReader implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    if (stop) {
                        return;
                    }

                    System.out.println(client.receiveMessage());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientWriter implements Runnable {

        @Override
        public void run() {
            System.out.println("You can now write messages to the server");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String msg;

            while (true){
                try{
                    msg = br.readLine();

                    if(msg.equals("/leave")){
                        client.sendMessage(msg);
                        System.out.println("Stopping...");
                        stopThread();
                        return;
                    }

                    if(msg.equals("/file")){
                        System.out.println();
                    }

                    client.sendMessage(msg);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}


