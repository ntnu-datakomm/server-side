package no.ntnu;

/**
 * Runs the UDP server
 */
public class ServerRunner {
    public static void main(String[] args) {
        UdpServer server = new UdpServer();
        server.run();
    }
}