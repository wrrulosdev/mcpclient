package dev.wrrulosdev.mcpclient.client.plugins;

import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;

import java.util.ArrayList;
import java.util.List;

public class PluginTabStorage {

    private static final List<String> storedPluginNames = new ArrayList<>();
    private Boolean listening = false;

    /**
     * Adds a plugin name to the storage list if it isn't already stored.
     * This ensures that no duplicate plugin names are added to the storage.
     *
     * @param name The plugin name to be stored.
     */
    public void addPluginName(String name) {
        if (storedPluginNames.contains(name)) return;
        System.out.println("[LOG] Adding plugin name to storage: " + name);
        storedPluginNames.add(name);
    }

    /**
     * Sends all stored plugin names to the player and clears the storage list.
     * This method should be called when the collection of plugin names is complete.
     */
    public void sendStoredPluginNames() {
        if (storedPluginNames.isEmpty()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cNo plugins found. The server has blocked tab autocompletion");
            Msg.sendFormattedMessage("&cNo plugins found", true);
            return;
        }

        String plugins = String.join(" ", storedPluginNames);
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&fPlugins found: &a" + plugins);
        storedPluginNames.clear();
    }

    /**
     * Retrieves the current listening status.
     *
     * @return The current listening status as a Boolean.
     */
    public Boolean getListening() {
        return listening;
    }

    /**
     * Sets the listening status.
     *
     * @param listening The new listening status to be set.
     */
    public void setListening(Boolean listening) {
        this.listening = listening;
    }
}
