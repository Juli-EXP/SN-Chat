package chat;

import sockets.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        showInstructions();

        Thread reader = new Thread(new ClientReader());
        reader.start();
        Thread writer = new Thread(new ClientWriter());
        writer.start();
    }

    private static void stopThread() throws IOException {
        client.close();
        stop = true;
    }

    private static void showInstructions() {
        System.out.println("*****************INSTRUCTIONS*****************");
        System.out.println("Write \"/leave\" to leave the chat");
        System.out.println("Write \"/file\" to send a file");
        System.out.println("You have to type in the directory of the file");
        System.out.println("**********************************************");
    }


    private static class ClientReader implements Runnable {

        @Override
        public void run() {
            String msg;
            while (true) {
                try {
                    if (stop) {
                        return;
                    }

                    msg = client.receiveMessage();

                    switch (msg) {
                        case "/file":
                            receiveFile();
                            break;
                        default:
                            System.out.println(msg);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void receiveFile() throws IOException {
            String filename;

            Files.createDirectories(Paths.get("download"));

            if (Files.notExists(Paths.get("download"))) {
                filename = client.receiveFile();
            } else {
                filename = client.receiveFile("download/");
            }

            System.out.println("The file \"" + filename + "\" was downloaded");
        }
    }

    private static class ClientWriter implements Runnable {

        @Override
        public void run() {
            System.out.println("You can now write messages to the server");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String msg;

            while (true) {
                try {
                    msg = br.readLine();

                    switch (msg) {
                        case "/leave":
                            System.out.println("Stopping...");
                            client.sendMessage(msg);
                            stopThread();   //kill other thread
                            return;
                        case "/file":
                            sendFile();
                            break;
                        case "/instruction":
                            showInstructions();
                            break;
                        default:
                            client.sendMessage(msg);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendFile() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the file path:");
            String filename = br.readLine();

            client.sendMessage("/file");

            if (Files.notExists(Paths.get(filename))) {
                System.err.println("File not found");
                return;
            }

            client.sendFile(filename);
        }
    }
}