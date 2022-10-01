package no.ntnu.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Handles all UDP-socket operations
 */
public class UdpSocketHandler {
    private DatagramSocket socket;

    private InetSocketAddress remoteAddress;

    /**
     * Create a client-side UDP socket targeted to a given server address
     *
     * @param serverAddress The server IP address or hostname
     * @param serverPort    the UDP port on the server side
     * @return UDP socket handler or null on error
     */
    public static UdpSocketHandler createClientSocket(String serverAddress, int serverPort) {
        UdpSocketHandler handler = new UdpSocketHandler();
        try {
            handler.remoteAddress = new InetSocketAddress(serverAddress, serverPort);
            handler.socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Error while creating client UDP socket: " + e.getMessage());
            handler = null;
        }
        return handler;
    }

    // Make the constructor private, prohibit using the constructor directly. Use static creator methods instead!
    private UdpSocketHandler() {
    }

    /**
     * Create a UDP socket which the server will use to listen on a specific port
     *
     * @param port The UDP port number of the server
     * @return Socket handler or null on error
     */
    public static UdpSocketHandler createServerSocket(int port) {
        UdpSocketHandler handler = new UdpSocketHandler();
        try {
            handler.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Error while creating server UDP socket: " + e.getMessage());
            handler = null;
        }
        return handler;
    }

    /**
     * Send a datagram with provided message to the previously saved remote address
     *
     * @param message The message to include in the datagram body
     * @return True on success, false on error
     */
    public boolean send(String message) {
        return sendTo(remoteAddress, message);
    }

    byte[] responseDataBuffer = new byte[1024]; // Reserve a bit more space than one would normally need
    DatagramPacket receivePacket = new DatagramPacket(responseDataBuffer, responseDataBuffer.length);

    /**
     * Wait for the next datagram from the socket, receive it, interpret it as a string message
     *
     * @return The string message from the datagram; null on error
     */
    public String receive() {
        String message = null;
        try {
            socket.receive(receivePacket);
            message = new String(receivePacket.getData(), 0, receivePacket.getLength());
            remoteAddress = new InetSocketAddress(receivePacket.getAddress(), receivePacket.getPort());
            System.out.println("Saving remote address: " + remoteAddress);
        } catch (Exception e) {
            System.out.println("Error while receiving a UDP datagram: " + e.getMessage());
        }
        return message;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Send a datagram with provided message to the provided address
     *
     * @param address The address (including port number) to send to
     * @param message The message to include in the datagram body
     * @return True on success, false on error
     */
    public boolean sendTo(InetSocketAddress address, String message) {
        boolean success = false;

        byte[] dataToSend = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, address);
        try {
            socket.send(sendPacket);
            success = true;
        } catch (IOException e) {
            System.out.println("Error while sending a datagram: " + e.getMessage());
        }

        return success;
    }
}
