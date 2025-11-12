package dev.wrrulosdev.mcpclient.client.utils.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import dev.wrrulosdev.mcpclient.client.localapi.ApiResponse;

public class HttpUtils {

    /**
     * Extracts the value of a specified query parameter from an HttpExchange's request URI.
     *
     * @param exchange  the HttpExchange containing the HTTP request
     * @param paramName the name of the query parameter to extract
     * @return the decoded parameter value, or null if not present or an error occurs
     */
    public static String extractQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        
        try {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith(paramName + "=")) {
                    return URLDecoder.decode(param.substring(paramName.length() + 1), "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends a plain-text HTTP response with CORS headers.
     *
     * @param exchange   the HttpExchange to send the response to
     * @param statusCode the HTTP status code of the response
     * @param response   the response body as a string
     * @throws IOException if an I/O error occurs when writing the response
     */
    public static void sendTextResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.length());
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
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
