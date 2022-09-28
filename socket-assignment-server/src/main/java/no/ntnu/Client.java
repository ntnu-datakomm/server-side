package no.ntnu;

import java.net.InetAddress;

/**
 * Stores information about a specific client, including the address and last message received from this client
 */
public class Client {
    // The unique identifier (address+port) of this client
    private final AddressAndPort id;

    private String lastReceivedMessage;

    // The task assigned to this client
    private String assignedTask;


    /**
     * Create a new client
     *
     * @param id                  Remote Internet address and UDP port of the client (uniquely identify the client)
     * @param lastReceivedMessage The last message recieved from this client
     */
    public Client(AddressAndPort id, String lastReceivedMessage) {
        this.id = id;
        this.lastReceivedMessage = lastReceivedMessage;
    }

    /**
     * Get the IP address and UDP port which uniquely identify this client
     *
     * @return The unique identifier of this client
     */
    public AddressAndPort getId() {
        return id;
    }

    /**
     * Get the last message received from the client
     *
     * @return The last received message, null if nothing was received
     */
    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    /**
     * Remember the task assigned for this client
     *
     * @param task The assigned task
     */
    public void setAssignedTask(String task) {
        this.assignedTask = task;
    }

    /**
     * Get the task assigned to this client
     *
     * @return The assigned task
     */
    public String getAssignedTask() {
        return assignedTask;
    }

    public void setLastReceivedMessage(String message) {
        this.lastReceivedMessage = message;
    }

}
