package no.ntnu;

import java.io.IOException;
import java.net.*;

/**
 * An example UDP server. The example from the lecture slides, a bit cleaned.
 * Waits for an incoming UDP datagram with a text message. Transforms that message to uppercase and
 * sends it back to the client. Then waits for the next client datagram (from the same or another client), etc.
 */
public class UdpServer {
    public final static int SERVER_PORT = 9876;

    /**
     * Starts the UDP server, never returns (goes into a never-ending loop)
     */
    public void run() {
        System.out.println("Starting UDP server on port " + SERVER_PORT);
        byte[] receiveData = new byte[1024];
        DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Socket error, shutting down the server: " + e.getMessage());
            return;
        }
        while (true) {
            try {
                // Receive a datagram from a client (any client)
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("Received UDP packet from " + clientAddress.getHostAddress() + ", port " + port);

                // Send back datagram with the same message (capitalized) to the same client
                String capitalizedSentence = sentence.toUpperCase();
                byte[] sendData = capitalizedSentence.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(sendData, sendData.length, clientAddress, port);
                serverSocket.send(responsePacket);
            } catch (IOException e) {
                System.out.println("Socket error for the client: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
