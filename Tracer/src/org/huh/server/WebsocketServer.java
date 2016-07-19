package org.huh.server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

/**
 * Created by rnentjes on 19-7-16.
 */
public class WebsocketServer extends NanoWSD {

    public WebsocketServer(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new VisualizerWebsocket(handshake);
    }


    @Override
    protected Response serveHttp(IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found xxx");
    }

}
