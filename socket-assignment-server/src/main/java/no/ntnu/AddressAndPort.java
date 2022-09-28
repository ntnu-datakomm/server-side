package no.ntnu;

import java.net.InetAddress;

/**
 * Stores Internet address (IP address) and port number
 */
public class AddressAndPort {
    private final InetAddress address;
    private final int port;

    public AddressAndPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "AddressAndPort{" +
                "address=" + address +
                ", port=" + port +
                '}';
    }
}
