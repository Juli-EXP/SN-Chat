package sockets;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class implements a client socket with methodes
 * to send and receive Strings as well as files.
 *
 * @author Julian Lamprecht
 */

public class Client {
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    /**
     * Creates a socket and initializes the DataInputStream as well as
     * the DataOutputStream.
     *
     * @param ipAdress  the IP address of the Server.
     * @param port      the port of the Server.
     * @throws IOException if an I/O error occurs when creating the socket,
     *                     if an I/O error occurs when creating the input stream,
     *                     if an I/O error occurs when creating the output stream.
     */
    public Client(String ipAdress, int port) throws IOException {
        socket = new Socket(ipAdress, port);

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Uses a socket and initializes the DataInputStream
     * as well as the DataOutputStream.
     *
     * @param socket The socket you want to use
     * @throws IOException if an I/O error occurs when creating the socket,
     *                     if an I/O error occurs when creating the input stream,
     *                     if an I/O error occurs when creating the output stream.
     */
    public Client(Socket socket) throws IOException {
        this.socket = socket;

        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     *
     * @return
     */
    public String getIpAddress(){
        if(socket.getInetAddress() == null){
            return "";
        }else{
            return socket.getInetAddress().toString();
        }
    }

    /**
     *
     * @return
     */
    public int getPort(){
        return socket.getPort();
    }

    /**
     * Sends a String to the Server using modified UTF-8.
     *
     * @param msg the string you want to send.
     * @throws IOException if an I/O error occurs.
     */
    public void sendMessage(String msg) throws IOException {
        dataOutputStream.writeUTF(msg);
    }

    /**
     * Receives a String from the Server using modified UTF-8.
     *
     * @return a Unicode string.
     * @throws IOException if the stream has been closed.
     *                     or an I/O error occurs.
     */
    public String receiveMessage() throws IOException {
        return dataInputStream.readUTF();
    }

    /**
     * Sends the file from the given path. It sends the filename, the filesize,
     * and the bytes of the file.
     *
     * @param path the path of the file.
     * @throws IOException if an I/O error occurs.
     */
    public void sendFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));

        dataOutputStream.writeUTF(Paths.get(path).getFileName().toString());
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes, 0, bytes.length);
        dataOutputStream.flush();
    }

    /**
     * Receives a file and saves it in directory of the executed program.
     *
     * @return the name of the file.
     * @throws IOException if the stream has been closed.
     *                     or an I/O error occurs.
     */
    public String receiveFile() throws IOException {
        return receiveFile("");
    }

    /**
     * Receives a file and saves it in the given directory.
     *
     * @param directory the directory where you want to save your file.
     * @return the name of the file.
     * @throws IOException if the stream has been closed.
     *                     or an I/O error occurs.
     */
    public String receiveFile(String directory) throws IOException {
        String filename = dataInputStream.readUTF();
        int filesize = dataInputStream.readInt();
        byte[] bytes = new byte[1024];

        FileOutputStream fileOutputStream = new FileOutputStream(directory + filename);

        int count;  //counts the amount of read bytes

        //checks if there are still bytes to read; it read either the whole file or the remaining bytes
        while (filesize > 0 && (count = dataInputStream.read(bytes, 0, Math.min(bytes.length, filesize))) != -1) {
            fileOutputStream.write(bytes, 0, count);
            filesize -= count;
        }

        fileOutputStream.close();

        return filename;
    }

    /**
     * Closes this input and output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}