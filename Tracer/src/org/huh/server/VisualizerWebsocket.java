package org.huh.server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;

/**
 * Created by rnentjes on 19-7-16.
 */
public class VisualizerWebsocket extends NanoWSD.WebSocket {
    private SendMessageThread sendMessageThread = null;

    private static class SendMessageThread extends Thread {
        private NanoWSD.WebSocket ws;
        private boolean running = false;
        private int counter = 0;

        SendMessageThread(VisualizerWebsocket ws) {
            super("SendMessageThread - "+ws);

            this.ws = ws;
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                try {
                    ws.send("Message "+(counter++));
                    Thread.sleep(100L);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRunning() {
            running = false;
        }
    }

    public VisualizerWebsocket(NanoHTTPD.IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onOpen() {
        sendMessageThread = new SendMessageThread(this);

        sendMessageThread.start();
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        sendMessageThread.stopRunning();
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
        System.out.println("Received ws message: " + message.getTextPayload());
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {

    }

    @Override
    protected void onException(IOException exception) {
        sendMessageThread.stopRunning();
    }
}
