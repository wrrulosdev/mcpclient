package dev.wrrulosdev.mcpclient.client.passwords;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.passwords.request.PasswordFinder;
import dev.wrrulosdev.mcpclient.client.passwords.request.Passwords;
import dev.wrrulosdev.mcpclient.client.passwords.request.PlayerPasswords;
import dev.wrrulosdev.mcpclient.client.utils.validators.MinecraftValidator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerTracker {

    private static final ConcurrentHashMap<String, PlayerPasswords> savedPlayers = new ConcurrentHashMap<>();
    private static final Set<String> processingUsers = ConcurrentHashMap.newKeySet();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final PlayerPasswords DEFAULT_PLAYER_PASSWORDS = new PlayerPasswords(
        "",
        new Passwords(List.of(), List.of())
    );

    /**
     * Checks the list of players in the game, and if there are any new players
     * whose passwords have not been retrieved yet, it initiates the password retrieval process.
     * It uses an executor to perform this operation asynchronously.
     */
    public void checkAndSavePasswords() {
        if (Mcpclient.getMinecraftClient().player == null) return;
        Set<String> currentPlayers = ConcurrentHashMap.newKeySet();

        Mcpclient.getMinecraftClient().player.networkHandler.getPlayerList().forEach(player -> {
            String playerName = player.getProfile().getName();
            if (!MinecraftValidator.isValidMinecraftUsername(playerName)) return;
            currentPlayers.add(playerName);
        });

        Set<String> newPlayers = ConcurrentHashMap.newKeySet();
        currentPlayers.forEach(player -> {
            if (!savedPlayers.containsKey(player) && !processingUsers.contains(player)) {
                newPlayers.add(player);
            }
        });

        if (!newPlayers.isEmpty()) {
            processingUsers.addAll(newPlayers);

            executor.submit(() -> {
                try {
                    String usernamesToSearch = String.join(",", newPlayers);
                    PasswordFinder finder = new PasswordFinder(usernamesToSearch);
                    var results = finder.getUsernamesPasswords();

                    if (results != null) {
                        results.forEach(passwords ->
                            savedPlayers.put(passwords.username(), passwords)
                        );
                    }
                } finally {
                    processingUsers.removeAll(newPlayers);
                }
            });
        }
    }

    /**
     * Retrieves the passwords for a specific player. If no passwords are found for the player,
     * it returns a default set of passwords.
     *
     * @param username The username of the player whose passwords are being requested.
     * @return PlayerPasswords The passwords associated with the player, or a default set if none are found.
     */
    public PlayerPasswords getPasswordsForPlayer(String username) {
        return savedPlayers.getOrDefault(username, DEFAULT_PLAYER_PASSWORDS);
    }
}
