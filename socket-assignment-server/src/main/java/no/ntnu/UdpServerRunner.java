package no.ntnu;

/**
 * Starts the UDP task-server
 */
public class UdpServerRunner {
    public static void main(String[] args) {
        UdpTaskServer server = new UdpTaskServer();
        server.runIndefinitely();
    }
}