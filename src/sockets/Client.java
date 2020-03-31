package sockets;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        byte[] bytes = Files.readAllBytes(Paths.get(path));

        dataOutputStream.writeUTF(Paths.get(path).getFileName().toString());

        dataOutputStream.write(bytes, 0, bytes.length);
        dataOutputStream.flush();
    }

    public String receiveFile() throws IOException{
        return receiveFile("");
    }

    public String receiveFile(String directory) throws IOException {

        String filename;
        int filesize;
        byte[] bytes = new byte[1024 * 1024 * 50];    //Max size = 50 MB

        filename = dataInputStream.readUTF();
        filesize = dataInputStream.read(bytes, 0, bytes.length);



        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(directory + filename));
        bufferedOutputStream.write(bytes, 0, filesize);
        bufferedOutputStream.close();

        return filename;
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
    }
}
