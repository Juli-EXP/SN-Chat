package java.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class implements a server with important methodes.
 *
 * @author Julian Lamprecht
 */

public class Server {
    private final ServerSocket serverSocket;

    /**
     * Creates a server socket.
     *
     * @param port the port you want to use.
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Accepts a connectin from a client.
     *
     * @return a socket to wich you can send and receive messages.
     * @throws IOException if an I/O error occurs when waiting for a connection.
     */
    public Socket acceptConnection() throws IOException {
        return serverSocket.accept();
    }

    /**
     * Closes the server socket
     *
     * @throws IOException if an I/O error occurs when closing the socket.
     */
    public void closeServer() throws IOException {
        serverSocket.close();
    }
}
