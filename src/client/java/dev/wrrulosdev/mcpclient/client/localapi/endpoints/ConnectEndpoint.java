package dev.wrrulosdev.mcpclient.client.localapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import dev.wrrulosdev.mcpclient.client.localapi.ApiResponse;
import dev.wrrulosdev.mcpclient.client.localapi.LocalApiServer;
import dev.wrrulosdev.mcpclient.client.utils.network.OnlineServers;
import dev.wrrulosdev.mcpclient.client.utils.screens.ConfirmationSystem;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

public class ConnectEndpoint implements HttpHandler {

    /**
     * Handles incoming HTTP requests for the /connect endpoint.
     * Expects a PUT request with a query parameter "address" specifying the server address.
     * If the request is valid, prompts the Minecraft client to confirm and connect to the server.
     *
     * @param exchange the HTTP exchange containing request and response objects
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

        connectToServerWithConfirmation(address);

        ApiResponse response = ApiResponse.success("connecting");
        LocalApiServer.sendJsonResponse(exchange, 200, response);
    }

    /**
     * Runs on the Minecraft client thread to show a confirmation dialog before connecting to a server.
     *
     * @param address the server address to connect to
     */
    private void connectToServerWithConfirmation(String address) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            ConfirmationSystem.showServerConnectionConfirmation(
                client,
                address,
                () -> {
                    OnlineServers.connect(address, client);
                }
            );
        });
    }
}
