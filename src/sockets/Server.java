package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public Socket acceptConnection() throws IOException {
        return serverSocket.accept();
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }
}
