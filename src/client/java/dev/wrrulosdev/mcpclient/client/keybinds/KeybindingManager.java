package dev.wrrulosdev.mcpclient.client.keybinds;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.screens.MenuScreen;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindingManager {

    private final KeyBinding openSettingsKey;
    private final KeyBinding[] autoCommandKeys;

    /** Default key codes for auto-commands (F1-F6) */
    private static final int[] DEFAULT_AUTOCOMMAND_KEYS = {
        GLFW.GLFW_KEY_F6,
        GLFW.GLFW_KEY_F7,
        GLFW.GLFW_KEY_F8,
        GLFW.GLFW_KEY_F9,
        GLFW.GLFW_KEY_F10,
        GLFW.GLFW_KEY_F12
    };

    /**
     * Initializes all key bindings:
     * - 1 settings key
     * - 6 auto-command keys
     */
    public KeybindingManager() {
        this.openSettingsKey = createKeyBinding(
            "Open Settings",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "MCPClient | Settings"
        );

        this.autoCommandKeys = new KeyBinding[6];
        initializeAutoCommandKeys();
    }

    /** Creates and registers all auto-command key bindings */
    private void initializeAutoCommandKeys() {
        for(int i = 0; i < autoCommandKeys.length; i++) {
            autoCommandKeys[i] = createKeyBinding(
                "AutoCommand " + (i + 1),
                DEFAULT_AUTOCOMMAND_KEYS[i],
                "MCPClient | AutoCommands"
            );
        }
    }

    /**
     * Creates and registers a key binding
     *
     * @param translationKey Language file key for display text
     * @param keyCode GLFW key code
     * @param category Control settings category
     * @return Configured KeyBinding instance
     */
    private KeyBinding createKeyBinding(String translationKey, int keyCode, String category) {
        KeyBinding binding = new KeyBinding(
            translationKey,
            InputUtil.Type.KEYSYM,
            keyCode,
            category
        );
        KeyBindingHelper.registerKeyBinding(binding);
        return binding;
    }

    /**
     * Main tick handler for keybinding processing. Called every client tick.
     *
     * @param client Active Minecraft client instance
     */
    public void tick(MinecraftClient client) {
        if (client == null) return;

        checkSettingsKey(client);
        checkAutoCommandKeys(client);
    }

    /** Handles settings key press */
    private void checkSettingsKey(MinecraftClient client) {
        if (openSettingsKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new MenuScreen(null));
            }
        }
    }

    /** Checks and handles all auto-command key presses */
    private void checkAutoCommandKeys(MinecraftClient client) {
        for(int i = 0; i < autoCommandKeys.length; i++) {
            if (autoCommandKeys[i].wasPressed()) {
                handleAutoCommand(client, i);
            }
        }
    }

    /**
     * Executes an auto-command
     *
     * @param client Minecraft client instance
     * @param commandIndex Which command to execute (0-5)
     */
    private void handleAutoCommand(MinecraftClient client, int commandIndex) {
        String command = Mcpclient.getSettings().getAutoCommandsSettings().getCommandByIndex(commandIndex);

        if (command == null || command.trim().isEmpty()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "Empty command slot: " + (commandIndex + 1));
            return;
        }

        executeCommand(client, command);
    }

    /**
     * Executes a command as the player
     * @param command The full command to execute (including "/")
     */
    private void executeCommand(MinecraftClient client, String command) {
        if (client.player == null) return;

        if (command.startsWith("/")) {
            client.player.networkHandler.sendCommand(command);
            return;
        }

        client.player.networkHandler.sendChatMessage(command);
    }
}