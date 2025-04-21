package dev.wrrulosdev.mcpclient.client.passwords.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.constants.UrlConstants;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PasswordFinder {

    private final String username;
    private final List<PlayerPasswords> passwordsList = new ArrayList<>();
    private static final String API_URL = UrlConstants.SEARCH_ENDPOINT;

    public PasswordFinder(String usernames) {
        this.username = usernames;
    }

    /**
     * Retrieves the passwords for the given username by making an HTTP request to the API.
     * @return A PlayerPasswords object containing the username passwords and obfuscated passwords.
     */
    public List<PlayerPasswords> getUsernamesPasswords() {
        try {
            HttpResponse<String> response = this.sendRequest();
            System.out.println(response.body());

            if (response.statusCode() != 200) {
                System.err.println("Error en la respuesta: Código " + response.statusCode());
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());

            if (!rootNode.has("results")) {
                System.err.println("Formato JSON inválido");
                return null;
            }

            JsonNode resultsNode = rootNode.get("results");

            resultsNode.fieldNames().forEachRemaining(fieldName -> {
                String passwordValue = resultsNode.get(fieldName).asText();
                String[] passwords = passwordValue.split(" ");
                List<String> userPasswords = new ArrayList<>(List.of());
                List<String> userObfuscatedPasswords = new ArrayList<>(List.of());

                for (String pw : passwords) {
                    if (pw.trim().isEmpty()) continue;

                    if (pw.length() >= 32) {
                        userObfuscatedPasswords.add(pw);
                    } else {
                        userPasswords.add(pw);
                    }
                }

                Passwords usernamePasswords = new Passwords(userPasswords, userObfuscatedPasswords);
                PlayerPasswords playerPasswords = new PlayerPasswords(fieldName, usernamePasswords);
                passwordsList.add(playerPasswords);
            });

            if (!Mcpclient.isMCPToolAPIEnabled()) {
                Mcpclient.setMCPToolAPIEnabled(true);
            }

            return passwordsList;

        } catch (IOException e) {
            //System.err.println("I/O Error: " + e.getMessage());
            Mcpclient.setMCPToolAPIEnabled(false);
        } catch (InterruptedException e) {
            System.err.println("Conexión interrumpida");
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.err.println("Error en el mapeo JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unkown: " + e.getMessage());
        }
        return null;
    }

    /**
     * Sends an HTTP request to the password search API for the specified username.
     * @return The HTTP response containing the results of the password search.
     * @throws IOException If an I/O error occurs while sending the request.
     * @throws InterruptedException If the request is interrupted.
     */
    private HttpResponse<String> sendRequest() throws IOException, InterruptedException {
        String encodedUsernames = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String url = API_URL.replace("%u", encodedUsernames);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}