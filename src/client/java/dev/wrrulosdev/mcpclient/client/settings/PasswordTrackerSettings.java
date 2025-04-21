package dev.wrrulosdev.mcpclient.client.settings;

public class PasswordTrackerSettings {

    private boolean playerInfo = false;
    private boolean customTabList = false;

    /**
     * Checks if the display of player information is enabled.
     *
     * @return true if player info is enabled, false otherwise.
     */
    public boolean isPlayerInfo() {
        return playerInfo;
    }

    /**
     * Sets the state of displaying player information.
     *
     * @param playerInfo true to enable player info display, false to disable.
     */
    public void setPlayerInfo(boolean playerInfo) {
        this.playerInfo = playerInfo;
    }

    /**
     * Checks if the custom tab list is enabled.
     *
     * @return true if the custom tab list is enabled, false otherwise.
     */
    public boolean isCustomTabList() {
        return customTabList;
    }

    /**
     * Sets the state of the custom tab list.
     *
     * @param customTabList true to enable the custom tab list, false to disable.
     */
    public void setCustomTabList(boolean customTabList) {
        this.customTabList = customTabList;
    }
}
