package dev.wrrulosdev.mcpclient.client.localapi;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dev.wrrulosdev.mcpclient.client.localapi.endpoints.AddServerEndpoint;
import dev.wrrulosdev.mcpclient.client.localapi.endpoints.ConnectEndpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LocalApiServer {

    private HttpServer server;

    /**
     * Starts the local HTTP server on 127.0.0.1:45555 and registers the /connect endpoint.
     *
     * <p>If the server fails to start, an error message is printed to stderr.</p>
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 45555), 0);
            server.createContext("/connect", new ConnectEndpoint());
            server.createContext("/addserver", new AddServerEndpoint());
            server.setExecutor(null);
            server.start();
            System.out.println("[MCPClient] Local API started on http://127.0.0.1:45555");
        } catch (IOException e) {
            System.err.println("[MCPClient] Failed to start Local API: " + e.getMessage());
        }
    }

    /**
     * Stops the local HTTP server if it is running.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
