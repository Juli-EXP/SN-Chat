package sockets;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private Socket socket;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Client(String ipAdress, int port) throws IOException {
        socket = new Socket(ipAdress, port);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Client(Socket socket) throws IOException {
        this.socket = socket;

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException {
        dataOutputStream.writeUTF(msg);
        dataOutputStream.flush();
    }

    public String receiveMessage() throws IOException {

        return dataInputStream.readUTF();
    }

    public void sendFile(String path) throws IOException {
        File file = new File(path);
        byte[] bytes = Files.readAllBytes(file.toPath());

        dataOutputStream.writeUTF(file.getName());

        dataOutputStream.write(bytes, 0, bytes.length);
        dataOutputStream.flush();
    }

    public void receiveFile() throws IOException {
        String filename;
        int filesize;
        byte[] bytes = new byte[1024 * 1024 * 50];    //Max size = 50 MB

        filename = dataInputStream.readUTF();
        filesize = dataInputStream.read(bytes, 0, bytes.length);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
        bufferedOutputStream.write(bytes, 0, filesize);
        bufferedOutputStream.close();
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
    }
}
