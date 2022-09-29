package no.ntnu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handler for one client connection
 */
public class ClientHandler implements Runnable {
    // Character used to mark end of a command
    private static final Character TERMINATING_CHARACTER = '.';
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final String clientId;
    // The task assigned to this client
    private String assignedTask;

    /**
     * Create a client handler
     *
     * @param clientSocket Socket for this particular client
     */
    public ClientHandler(Socket clientSocket) throws IOException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        this.clientId = clientSocket.getInetAddress().getHostName() + "[:" + clientSocket.getPort() + "]";
    }

    /**
     * Run a conversation loop until the client disconnects
     */
    public void run() {
        System.out.println("Handling client " + getClientId() + " in thread " + Thread.currentThread().getId());

        boolean clientAlive = true;
        while (clientAlive) {
            String clientCommand = waitForNextClientCommand();
            if (clientCommand != null) {
                if (Logic.isClientRequestingATask(clientCommand)) {
                    String task = Logic.getRandomTask();
                    if (sendResponse(assignedTask)) {
                        assignedTask = task;
                    }
                } else {
                    if (Logic.hasClientAnsweredCorrectly(assignedTask, clientCommand)) {
                        sendResponse(Logic.OK);
                    } else {
                        sendResponse(Logic.ERROR);
                    }
                }
            } else {
                System.out.println("Client socket closed, shutting down client handler...");
                clientAlive = false;
            }
        }
    }

    /**
     * Send a response to the client
     *
     * @param response The response to send
     * @return True on success, false on error
     */
    private boolean sendResponse(String response) {
        boolean success = false;

        byte[] dataToSend = response.getBytes();
        try {
            outputStream.write(dataToSend, 0, dataToSend.length);
            outputStream.flush();
            success = true;
        } catch (IOException e) {
            System.out.println("Error while sending response `" + response + "`: " + e.getMessage());
        }
        return success;
    }

    /**
     * Wait for the next command from the client. Read the incoming bytes until the next terminating symbol
     *
     * @return The received command, null on socket error
     */
    private String waitForNextClientCommand() {
        StringBuilder buffer = new StringBuilder();
        Character receivedChar = receiveNextCharFromClient();
        while (receivedChar != TERMINATING_CHARACTER && receivedChar != null) {
            buffer.append(receivedChar);
            receivedChar = receiveNextCharFromClient();
        }
        String command;
        if (receivedChar != null) {
            command = buffer.toString();
        } else {
            System.out.println("Socket error while reading client input");
            command = null;
        }
        return command;
    }

    /**
     * Receive a single character from the client (wait for it)
     *
     * @return The received character or null on error
     */
    private Character receiveNextCharFromClient() {
        Character nextChar = null;
        try {
            int nextByte = inputStream.read();
            nextChar = (char) nextByte;
        } catch (IOException e) {
            System.out.println("Error while reading next character: " + e.getMessage());
        }
        return nextChar;
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
