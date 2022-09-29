package no.ntnu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * UDP server, running according to the task protocol
 */
public class UdpTaskServer {
    // The UDP port on which the server will be listening
    private static final int UDP_PORT_NUMBER = 1234;

    // Maximum size of client datagram, in bytes
    private static final int MAX_DATAGRAM_SIZE = 64;

    private DatagramSocket serverSocket;

    // All the clients who have had communication with the server. The keys are AddressAndPort objects in string format
    private final Map<String, Client> clients = new HashMap<>();

    /**
     * Starts the server. Then it runs in a never-ending loop.
     */
    public void runIndefinitely() {
        if (!openListeningSocket()) {
            System.out.println("Could not open a listening socket, aborting...");
            return;
        }
        while (true) {
            Client client = waitForNextClientMessage();
            if (client != null) {
                if (Logic.isClientRequestingATask(client.getLastReceivedMessage())) {
                    String task = Logic.getRandomTask();
                    if (sendResponse(client, task)) {
                        client.setAssignedTask(task);
                    }
                } else {
                    String task = client.getAssignedTask();
                    if (Logic.hasClientAnsweredCorrectly(task, client.getLastReceivedMessage())) {
                        sendResponse(client, Logic.OK);
                    } else {
                        sendResponse(client, Logic.ERROR);
                    }
                }
            }
        }
    }

    /**
     * Try to open a listening server socket
     *
     * @return True when socket successfully opened, false on error
     */
    private boolean openListeningSocket() {
        boolean success = false;
        try {
            serverSocket = new DatagramSocket(UDP_PORT_NUMBER);
            System.out.println("Started UDP server on port " + UDP_PORT_NUMBER);
            success = true;
        } catch (SocketException e) {
            System.out.println("Failed while trying to open socket: " + e.getMessage());
        }
        return success;
    }

    /**
     * Waits for the next incoming datagram from a client. Also check if this is a previously known or a new client.
     *
     * @return The client who sent the datagram, or null on error
     */
    private Client waitForNextClientMessage() {
        Client client = null;
        try {
            final byte[] dataBuffer = new byte[MAX_DATAGRAM_SIZE];
            final DatagramPacket datagram = new DatagramPacket(dataBuffer, dataBuffer.length);
            serverSocket.receive(datagram);
            String message;
            if (datagram.getLength() > 0) {
                message = new String(datagram.getData(), 0, datagram.getLength());
                AddressAndPort clientId = new AddressAndPort(datagram.getAddress(), datagram.getPort());
                client = findExistingClient(clientId);
                if (client == null) {
                    client = new Client(clientId, message);
                    saveClient(client);
                } else {
                    client.setLastReceivedMessage(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while receiving client packet: " + e.getMessage());
        }
        return client;
    }

    /**
     * Find a client with given address and port which has had communication with the server before
     *
     * @param clientId The unique ID of a client to check
     * @return The client or null if no client with such address has ever communicated with the server
     */
    private Client findExistingClient(AddressAndPort clientId) {
        return clients.get(clientId.toString());
    }

    /**
     * Save a client so that we can later look it up
     * @param client A client who just sent its first message to the server
     */
    private void saveClient(Client client) {
        clients.put(client.getId().toString(), client);
    }

    /**
     * Send a new UDP datagram containing the given message to the client
     *
     * @param client  The UDP client (where the datagram will be sent)
     * @param message The message to include in the datagram
     * @return True on success, false on error
     */
    private boolean sendResponse(Client client, String message) {
        boolean success = false;

        byte[] bytesToSend = message.getBytes();
        AddressAndPort clientId = client.getId();
        DatagramPacket response = new DatagramPacket(bytesToSend, bytesToSend.length,
                clientId.getAddress(), clientId.getPort());
        try {
            serverSocket.send(response);
            success = true;
        } catch (IOException e) {
            System.out.println("Error while sending a message to the client " + clientId.getAddress().getHostAddress()
                    + ": " + e.getMessage());
        }

        return success;
    }
}
