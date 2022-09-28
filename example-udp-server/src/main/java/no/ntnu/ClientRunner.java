package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Entrypoint for UDP client.
 * Reads user input from the console, starts UDP client with the user's input
 */
public class ClientRunner {
    public static void main(String[] args) {
        try {
            System.out.print("Enter a message: ");
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String messageToSend = userInput.readLine();

            UdpClient client = new UdpClient();
            client.run(messageToSend);
        } catch (IOException e) {
            System.out.println("Failed while reading user input, aborting...");
        }
    }
}
