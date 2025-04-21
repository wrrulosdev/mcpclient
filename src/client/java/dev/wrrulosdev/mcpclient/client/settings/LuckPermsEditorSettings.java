package dev.wrrulosdev.mcpclient.client.settings;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LuckPermsEditorSettings {

    private boolean enabled = false;
    private String mode = PREFIX_MODE;
    private String newValue = "";
    private final List<String> ranks = new ArrayList<>();
    public static final String PREFIX_MODE = "PREFIX";
    public static final String SUFFIX_MODE = "SUFFIX";
    public static final String DISPLAYNAME_MODE = "RANDOM DISPLAYNAME";
    public static final String CLEAR_MODE = "CLEAR";
    public static final String DELETE_MODE = "DELETE";
    public static final List<String> MODES = Arrays.asList(PREFIX_MODE, SUFFIX_MODE, DISPLAYNAME_MODE, CLEAR_MODE, DELETE_MODE);

    /**
     * Checks if the settings are enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled state of the settings.
     *
     * @param enabled true to enable the settings, false to disable.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the current mode of operation.
     *
     * @return the current mode.
     */
    public String getMode() {
        return mode;
    }

    /**
     * Cycles through the available modes in the MODES list.
     * If the current mode is the last one, it will wrap around to the first.
     */
    public void cycleMode() {
        int currentIndex = MODES.indexOf(mode);
        int nextIndex = (currentIndex + 1) % MODES.size();
        mode = MODES.get(nextIndex);
    }

    /**
     * Returns the current value of the 'newValue' variable.
     * This function is a simple getter to access the value of 'newValue'.
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Sets a new value for the 'newValue' variable.
     * This function is a setter that allows modifying the value of 'newValue'.
     *
     * @param newValue The new value to be assigned to 'newValue'.
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * Gets the list of ranks associated with the settings.
     *
     * @return a list of ranks.
     */
    public List<String> getRanks() {
        return ranks;
    }

    /**
     * Adds a new rank to the list if it is not null, empty, or already present.
     *
     * @param rank the rank to add.
     */
    public void addRank(String rank) {
        if (rank != null && !rank.isEmpty() && !ranks.contains(rank)) {
            ranks.add(rank);
        }
    }

    /**
     * Clears all the ranks from the list.
     */
    public void clearRanks() {
        ranks.clear();
    }

    /**
     * Generates the appropriate LuckPerms command based on the current mode and provided rank.
     *
     * @param rank the rank for which the command will be generated.
     * @return the LuckPerms command as a string.
     * @throws IllegalArgumentException if the mode is unknown.
     */
    public String getLPCommand(String rank) {
        String genericBaseCommand = "/lp group " + rank;
        String randomValue = RandomStringUtils.random(4, 40, 123, true, true, null, new Random());

        return switch (mode) {
            case PREFIX_MODE -> genericBaseCommand + " meta setprefix " + newValue;
            case SUFFIX_MODE -> genericBaseCommand + " meta setsuffix " + newValue;
            case DISPLAYNAME_MODE -> genericBaseCommand + " setdisplayname " + randomValue;
            case CLEAR_MODE -> genericBaseCommand + " clear";
            case DELETE_MODE -> "/lp deletegroup " + rank;
            default -> throw new IllegalArgumentException("Unknown mode: " + mode);
        };
    }

    public String getGroupCommand() {
        return "/lp listgroups";
    }

    /**
     * Extracts the group name from a formatted line of text.
     * The line is expected to contain a specific format with the group name after a " - ".
     *
     * @param line the line of text from which the group name will be extracted.
     * @return the extracted group name, or null if the format is incorrect.
     */
    public static String extractGroupName(String line) {
        String[] parts = line.split(" - ");

        if (parts.length >= 2) {
            String rawName = parts[1].split(" ")[1];
            return rawName.trim().toLowerCase();
        }

        return null;
    }
}
