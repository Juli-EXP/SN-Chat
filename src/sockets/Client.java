package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        dataOutputStream.writeByte(1);
        dataOutputStream.writeUTF(msg);
        dataOutputStream.flush();
    }

    public void sendFile(String msg) throws IOException {
        dataOutputStream.writeByte(2);
        dataOutputStream.writeUTF(msg);
        dataOutputStream.flush();
    }

    public String receiveMessage() throws IOException {
        /*
        byte messageType = dataInputStream.readByte();

        if(messageType == 1){
            System.out.println("Message");
        }else if(messageType == 2){
            System.out.print("File");
        }else{
            System.out.println("Unknown messagetype");
        }
        */
        return dataInputStream.readUTF();
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
    }
}
