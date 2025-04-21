package dev.wrrulosdev.mcpclient.client.settings;

public class AutoCommandsSettings {

    // Array holding the auto commands. The size of the array can be adjusted as needed.
    private final String[] autoCommands = {"", "", "", "", "", ""};

    /**
     * Gets the auto command at the specified index.
     *
     * @param index the index of the auto command to retrieve
     * @return the auto command at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid (less than 0 or greater than the array length)
     */
    public String getCommandByIndex(int index) {
        return autoCommands[index];
    }

    /**
     * Sets the auto command at the specified index.
     *
     * @param index the index where the auto command should be set
     * @param value the new auto command to set
     * @throws IndexOutOfBoundsException if the index is invalid (less than 0 or greater than the array length)
     */
    public void setCommandByIndex(int index, String value) {
        autoCommands[index] = value;
    }
}
