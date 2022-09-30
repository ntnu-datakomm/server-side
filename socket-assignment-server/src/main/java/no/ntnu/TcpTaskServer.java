package no.ntnu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * UDP server, running according to the task protocol
 */
public class TcpTaskServer {
    // TCP port number used by the server
    private static final int TCP_PORT_NUMBER = 1234;
    private ServerSocket socket;

    /**
     * Starts the server. Then it runs in a never-ending loop.
     */
    public void runIndefinitely() {
        if (!openListeningSocket()) {
            System.out.println("Could not open a listening TCP socket, aborting...");
            return;
        }
        System.out.println("TCP Task server started, waiting for client connections...");
        while (true) {
            Socket clientSocket = waitForNextClientConnection();
            if (clientSocket != null) {
                handleClientInNewThread(clientSocket);
            }
        }
    }

    /**
     * Open a TCP server socket
     * @return True on success, false on error.
     */
    private boolean openListeningSocket() {
        boolean success = false;
        try {
            socket = new ServerSocket(TCP_PORT_NUMBER);
            success = true;
        } catch (IOException e) {
            System.out.println("Could not open a listening TCP socket: " + e.getMessage());
        }
        return success;
    }

    /**
     * Wait for the next client to connect
     * @return The socket for the newly connected client; or null on error.
     */
    private Socket waitForNextClientConnection() {
        try {
            return socket.accept();
        } catch (IOException e) {
            System.out.println("Error while accepting the next client connection: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handle this particular client in a new thread
     * @param clientSocket Socket for the newly connected client
     */
    private void handleClientInNewThread(Socket clientSocket) {
        try {
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        } catch (IOException e) {
            System.out.println("Error while initializing the client connection: " + e.getMessage());
        }
    }
}