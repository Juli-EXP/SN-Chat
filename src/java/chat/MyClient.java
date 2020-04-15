package chat;

import sockets.Client;

import java.io.*;
import java.nio.file.*;
import java.net.ConnectException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MyClient {
    private static Client client;
    private static ArrayList<String> clients;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        clients = fileToArrayList("config");
        boolean exit = true;
        String option;

        while (exit) {
            showOptions();

            try {
                option = br.readLine();

                switch (new Integer(option)) {
                    case 1:
                        startChat();
                        br.readLine();
                        break;
                    case 2:
                        showServers();
                        br.readLine();
                        break;
                    case 3:
                        addServer();
                        br.readLine();
                        break;
                    case 4:
                        removeServer();
                        br.readLine();
                        break;
                    case 5:
                        exit = false;
                        break;
                    default:
                        System.err.println("Wrong input");
                        br.readLine();
                        break;
                }

                clearConsole();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.err.println("Wrong input");
            }
        }

        System.out.println("Goodbye");
    }

    private static void startChat() {
        int select = selectServer();

        if (clients.isEmpty()) {
            System.out.println("Please add a Server");
            return;
        } else if (select >= clients.size() || select < 0) {
            System.out.println("Wrong input");
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] serverInfo = clients.get(select).split(",");
        String username;

        try {
            client = new Client(serverInfo[0], new Integer(serverInfo[1]));

            System.out.println("Enter your username:");
            username = br.readLine();
            client.sendMessage(username);
        } catch (ConnectException e) {
            System.err.println("Could not connect to the server");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        showInstructions();

        Thread reader = new Thread(new ClientReader());
        Thread writer = new Thread(new ClientWriter());
        reader.start();
        writer.start();

        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addServer() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String serverInfo;
        String ip;
        int port;

        try {
            System.out.println("Enter the ip address of the Server:");
            ip = br.readLine();

            System.out.println("Enter the port of the Server:");
            port = new Integer(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (NumberFormatException e) {
            System.err.println("Wrong input");
            return;
        }

        serverInfo = ip + "," + port;

        if (clients.contains(serverInfo)) {
            System.out.println("Server was already added");
        } else {
            System.out.println("Server was added");
            clients.add(serverInfo);
            arrayListToFile(clients);
        }
    }

    private static void removeServer() {
        int select = selectServer();

        if (clients.isEmpty()) {
            System.out.println("Please add a Server");
        } else if (select < clients.size() && select >= 0) {
            System.out.println("Server was removed");
            clients.remove(select);
            arrayListToFile(clients);
        } else {
            System.out.println("Wrong input");
        }
    }

    private static int selectServer() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int select;

        showServers();

        if (clients.isEmpty()){
            return -1;
        }

        System.out.println("Selcet a Server:");

        try {
            select = new Integer(br.readLine()) - 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e) {
            System.err.println("Wrong input");
            return -1;
        }

        return select;
    }

    private static void showServers() {
        if (clients.isEmpty()) {
            System.out.println("You have no saved servers");
            return;
        }

        int i = 1;

        for (String s : clients) {
            System.out.println(i + ": " + s);
            ++i;
        }
    }

    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopThread() throws IOException {
        client.close();
    }

    private static void arrayListToFile(ArrayList<String> arrayList) {
        String filename = "config";

        try {
            if(Files.notExists(Paths.get(filename))){
                Files.createFile(Paths.get(filename));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedWriter bw =
                    Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)){

            for (String s : arrayList) {
                bw.write(s);
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> fileToArrayList(String path) {
        ArrayList<String> arrayList = new ArrayList<>();

        if(Files.notExists(Paths.get(path))){
            return arrayList;
        }

        try (BufferedReader br =
                     Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)){

            String s;
            while((s = br.readLine()) != null){
                arrayList.add(s);
            }

        }catch (IOException e){
            e.printStackTrace();
        }


        return arrayList;
    }

    private static void showOptions() {
        System.out.println("*****************Options*****************");
        System.out.println("(1) Start chatting");
        System.out.println("(2) Show servers");
        System.out.println("(3) Add server");
        System.out.println("(4) Remove server");
        System.out.println("(5) Exit");
        System.out.println("*****************************************");
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
                    msg = client.receiveMessage();

                    switch (msg) {
                        case "/file":
                            receiveFile();
                            break;
                        default:
                            System.out.println(msg);
                            break;
                    }

                } catch (SocketException e) {
                    System.err.println("Socket was closed");
                    return;
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

            if (Files.notExists(Paths.get(filename))) {
                System.err.println("File not found");
                return;
            }

            client.sendMessage("/file");

            client.sendFile(filename);
        }
    }
}