package no.ntnu.udp;

import no.ntnu.Logic;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * UDP server, running according to the task protocol
 */
public class UdpTaskServer {
    // The UDP port on which the server will be listening
    private static final int UDP_PORT_NUMBER = 1234;

    private UdpSocketHandler serverSocketHandler;

    // All the clients who have had communication with the server. The keys are AddressAndPort objects in string format
    private final Map<String, Client> clients = new HashMap<>();

    /**
     * Starts the server. Then it runs in a never-ending loop.
     */
    public void runIndefinitely() {
        serverSocketHandler = UdpSocketHandler.createServerSocket(UDP_PORT_NUMBER);
        if (serverSocketHandler == null) {
            System.out.println("Could not open a listening socket, aborting...");
            return;
        }
        System.out.println("UDP task server started");
        while (true) {
            Client client = waitForNextClientMessage();
            if (client != null) {
                System.out.println("Message from " + client.getAddress() + ": " + client.getLastReceivedMessage());
                if (Logic.isTaskRequest(client.getLastReceivedMessage())) {
                    String task = Logic.getRandomTask();
                    System.out.println("Task for the client: " + task);
                    if (sendResponse(client, task)) {
                        client.setAssignedTask(task);
                    }
                } else {
                    String task = client.getAssignedTask();
                    System.out.print("Client's answer: " + client.getLastReceivedMessage());
                    if (Logic.hasClientAnsweredCorrectly(task, client.getLastReceivedMessage())) {
                        System.out.println(" is CORRECT!");
                        sendResponse(client, Logic.OK);
                    } else {
                        sendResponse(client, Logic.ERROR);
                        System.out.println(" is FAIL!");
                    }
                }
            }
        }
    }

    /**
     * Waits for the next incoming datagram from a client. Also check if this is a previously known or a new client.
     *
     * @return The client who sent the datagram, or null on error
     */
    private Client waitForNextClientMessage() {
        Client client = null;
        String message = serverSocketHandler.receive();
        if (message != null) {
            InetSocketAddress clientId = serverSocketHandler.getRemoteAddress();
            client = findExistingClient(clientId);
            if (client == null) {
                client = new Client(clientId, message);
                saveClient(client);
            } else {
                client.setLastReceivedMessage(message);
            }
        }
        return client;
    }

    /**
     * Find a client with given address and port which has had communication with the server before
     *
     * @param clientId The unique ID of a client to check
     * @return The client or null if no client with such address has ever communicated with the server
     */
    private Client findExistingClient(InetSocketAddress clientId) {
        return clients.get(clientId.toString());
    }

    /**
     * Save a client so that we can later look it up
     *
     * @param client A client who just sent its first message to the server
     */
    private void saveClient(Client client) {
        clients.put(client.getAddress().toString(), client);
    }

    /**
     * Send a new UDP datagram containing the given message to the client
     *
     * @param client  The UDP client (where the datagram will be sent)
     * @param message The message to include in the datagram
     * @return True on success, false on error
     */
    private boolean sendResponse(Client client, String message) {
        return serverSocketHandler.sendTo(client.getAddress(), message);
    }
}
