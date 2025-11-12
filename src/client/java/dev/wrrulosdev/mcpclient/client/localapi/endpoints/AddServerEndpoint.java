package dev.wrrulosdev.mcpclient.client.localapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import dev.wrrulosdev.mcpclient.client.localapi.ApiResponse;
import dev.wrrulosdev.mcpclient.client.localapi.LocalApiServer;
import dev.wrrulosdev.mcpclient.client.utils.network.OnlineServers;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

public class AddServerEndpoint implements HttpHandler {

    /**
     * Handles HTTP PUT requests to add a server to the Minecraft client server list.
     * Expects a query parameter "address" specifying the server address.
     * Responds with appropriate success or error messages.
     *
     * @param exchange the HTTP exchange containing request and response
     * @throws IOException if an I/O error occurs when handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"PUT".equals(exchange.getRequestMethod())) {
            ApiResponse response = ApiResponse.error("invalid_request_method");
            LocalApiServer.sendJsonResponse(exchange, 405, response);
            return;
        }

        String address = LocalApiServer.extractQueryParam(exchange.getRequestURI().getQuery(), "address");

        if (address == null || address.isEmpty()) {
            ApiResponse response = ApiResponse.error("parameter_missing");
            LocalApiServer.sendJsonResponse(exchange, 405, response);
            return;
        }

        OnlineServers.addServer(address, MinecraftClient.getInstance());
        ApiResponse response = ApiResponse.success("server_added");
        LocalApiServer.sendJsonResponse(exchange, 200, response);
    }
}
