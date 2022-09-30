package no.ntnu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles all socket operations
 */
public class SocketHandler {
    private static final int BAD_RECEIVED_CHAR = 65535;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private static final char TERMINATING_SYMBOL = '/';

    /**
     * Create a socket handler
     *
     * @param socket The socket for this connection
     * @throws IOException Can throw an exception if something goes wrong with the streams (should not happen, but still)
     */
    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    /**
     * Send a message to the socket
     *
     * @param message The message to send, add the terminating symbol in the end
     * @return True on success, false on error
     */
    public boolean send(String message) {
        boolean success = false;
        message = message + TERMINATING_SYMBOL;
        byte[] dataToSend = message.getBytes();
        try {
            outputStream.write(dataToSend, 0, dataToSend.length);
            outputStream.flush();
            success = true;
        } catch (IOException e) {
            System.out.println("Error while sending response `" + message + "`: " + e.getMessage());
        }
        return success;
    }

    /**
     * Wait for the next command from the socket. Read the incoming bytes until the next terminating symbol
     *
     * @return The received command, null on socket error
     */
    public String waitForNextCommand() {
        StringBuilder buffer = new StringBuilder();
        Character receivedChar = receiveOneCharacter();
        while (receivedChar != null && ((int) receivedChar != BAD_RECEIVED_CHAR) && receivedChar != TERMINATING_SYMBOL) {
            buffer.append(receivedChar);
            receivedChar = receiveOneCharacter();
        }
        String command;
        if (receivedChar != null && (int) receivedChar != BAD_RECEIVED_CHAR) {
            command = buffer.toString();
        } else {
            System.out.println("Socket error while reading client input");
            command = null;
        }
        return command;
    }

    /**
     * Receive a single character from the socket (wait for it)
     *
     * @return The received character or null on error
     */
    private Character receiveOneCharacter() {
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
     * Close the socket
     *
     * @return True on success, false on error
     */
    public boolean close() {
        boolean success = true;
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error while closing socket: " + e.getMessage());
        }
        return success;
    }
}
