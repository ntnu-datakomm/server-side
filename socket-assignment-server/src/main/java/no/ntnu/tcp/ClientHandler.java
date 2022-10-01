package no.ntnu.tcp;

import no.ntnu.Logic;

import java.io.IOException;
import java.net.Socket;

/**
 * Handler for one TCP client connection
 */
public class ClientHandler implements Runnable {
    private final String clientId;
    private final SocketHandler socketHandler;
    // The task assigned to this client
    private String assignedTask;

    /**
     * Create a client handler
     *
     * @param clientSocket Socket for this particular client
     */
    public ClientHandler(Socket clientSocket) throws IOException {
        this.socketHandler = new SocketHandler(clientSocket);
        this.clientId = clientSocket.getInetAddress().getHostName() + "[:" + clientSocket.getPort() + "]";
    }

    /**
     * Run a conversation loop until the client disconnects
     */
    public void run() {
        System.out.println("Handling client " + getClientId() + " in thread " + Thread.currentThread().getId());

        boolean clientAlive = true;
        while (clientAlive) {
            String clientCommand = socketHandler.waitForNextCommand();
            if (clientCommand != null) {
                if (Logic.isTaskRequest(clientCommand)) {
                    String task = Logic.getRandomTask();
                    System.out.println("Client " + clientId + " asking for a new task, here it is: " + task);
                    if (socketHandler.send(task)) {
                        assignedTask = task;
                    }
                } else {
                    if (Logic.hasClientAnsweredCorrectly(assignedTask, clientCommand)) {
                        socketHandler.send(Logic.OK);
                    } else {
                        socketHandler.send(Logic.ERROR);
                    }
                }
            } else {
                System.out.println("Client socket closed, shutting down client handler...");
                clientAlive = false;
            }
        }
    }

    /**
     * Get a string that describes client identification (address and port)
     *
     * @return Client ID string
     */
    private String getClientId() {
        return clientId;
    }
}
