package org.huh.server;

import java.io.IOException;

/**
 * Created by rnentjes on 19-7-16.
 */
public class Codevisualizer {

    /**
     * Method to start visualizer http/ws server
     *
     * @throws IOException
     */
    public static void startVisualizer() throws IOException {
        WebsocketServer server = new WebsocketServer(12345);

        server.start();
    }

    public static void main(String[] args) throws IOException {
        Codevisualizer.startVisualizer();

        System.out.println("Server started, hit Enter to stop.\n");
        try {
            System.in.read();
        } catch (IOException ignored) {
        }
        System.out.println("Stopping server.\n");
    }
}
