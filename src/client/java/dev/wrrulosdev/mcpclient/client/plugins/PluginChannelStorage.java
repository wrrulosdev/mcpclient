package dev.wrrulosdev.mcpclient.client.plugins;

import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;

import java.util.ArrayList;
import java.util.List;

public class PluginChannelStorage {

    private static final List<String> storedPluginMessages = new ArrayList<>();
    public static final List<String> vulnerablePluginMessages = new ArrayList<>();

    /**
     * Adds a plugin message to the storage list if it's not already stored.
     * This ensures that each plugin message is only stored once.
     *
     * @param message The plugin message to be stored.
     */
    public void addPluginMessage(String message) {
        if (storedPluginMessages.contains(message)) return;
        System.out.println("[LOG] Adding plugin message to storage: " + message);
        storedPluginMessages.add(message);
    }

    /**
     * Sends all stored plugin messages to the player and clears the storage list.
     * This is typically called after the player has connected to the server.
     */
    public void sendStoredPluginMessages() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("[ERROR] Error while sleeping thread: " + e.getMessage());
        }

        // If no messages are stored, notify the user
        if (storedPluginMessages.isEmpty()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cNo plugin message channels registered.");
            Msg.sendFormattedMessage("&cNo plugin message channels registered", true);
            return;
        }

        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&dRegistered plugin message channels:");
        Msg.sendFormattedMessage("&aFounded plugin channels: &f" + storedPluginMessages.size(), true);

        for (String message : storedPluginMessages) {
            System.out.println("[LOG] Sending message: " + message);

            if (vulnerablePluginMessages.contains(message)) {
                Msg.sendFormattedMessage("&f• " + "&d" + message + " &cVULNERABLE");
            } else {
                Msg.sendFormattedMessage("&f• " + "&a" + message);
            }
        }

        storedPluginMessages.clear();
    }

    /**
     * Loads known vulnerable plugin messages into the list of vulnerable messages.
     * This method is called during initialization to set up vulnerable channels.
     */
    public void loadVulnerablePluginMessages() {
        vulnerablePluginMessages.add("authmevelocity:main");
        vulnerablePluginMessages.add("signedvelocity:main");
    }
}
