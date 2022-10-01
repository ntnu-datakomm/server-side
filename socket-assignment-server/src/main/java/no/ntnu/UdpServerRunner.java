package no.ntnu;

import no.ntnu.udp.UdpTaskServer;

/**
 * Starts the UDP task-server
 */
public class UdpServerRunner {
    public static void main(String[] args) {
        UdpTaskServer server = new UdpTaskServer();
        server.runIndefinitely();
    }
}