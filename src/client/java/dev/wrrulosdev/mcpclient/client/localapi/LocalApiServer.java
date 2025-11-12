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
    
    /**
     * Sends a JSON response based on an ApiResponse object.
     *
     * @param exchange   the HttpExchange representing the HTTP request/response
     * @param statusCode the HTTP status code to return (e.g., 200, 400, 500)
     * @param response   the ApiResponse object containing status, title, and optional data
     * @throws IOException if writing to the response body fails
     */
    public static void sendJsonResponse(HttpExchange exchange, int statusCode, ApiResponse response) throws IOException {
        String jsonResponse = new Gson().toJson(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

   /**
     * Extracts a specific parameter value from a URL query string.
     *
     * @param query the raw query string from the URI (e.g. "address=host:port")
     * @param paramName the name of the parameter to extract
     * @return the decoded parameter value, or null if not found
     */
    public static String extractQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty() || paramName == null) return null;
        
        for (String param : query.split("&")) {
            if (param.startsWith(paramName + "=") && param.length() > paramName.length() + 1) {
                String encodedValue = param.substring(paramName.length() + 1);

                try {
                    return URLDecoder.decode(encodedValue, "UTF-8");
                } catch (Exception e) {
                    return encodedValue;
                }
            }
        }
        return null;
    }
}
