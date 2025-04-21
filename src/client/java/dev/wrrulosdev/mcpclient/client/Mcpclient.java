package dev.wrrulosdev.mcpclient.client;

import dev.wrrulosdev.mcpclient.client.commands.CommandManager;
import dev.wrrulosdev.mcpclient.client.hudrender.HudRenderer;
import dev.wrrulosdev.mcpclient.client.keybinds.KeybindingManager;
import dev.wrrulosdev.mcpclient.client.passwords.PasswordRender;
import dev.wrrulosdev.mcpclient.client.passwords.PlayerTracker;
import dev.wrrulosdev.mcpclient.client.payloads.PayloadRegistry;
import dev.wrrulosdev.mcpclient.client.plugins.PluginChannelStorage;
import dev.wrrulosdev.mcpclient.client.plugins.PluginTabStorage;
import dev.wrrulosdev.mcpclient.client.settings.SettingsManager;
import dev.wrrulosdev.mcpclient.client.spoofing.SessionController;
import dev.wrrulosdev.mcpclient.client.spoofing.SpoofColors;
import dev.wrrulosdev.mcpclient.client.utils.resources.GifTextureManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

/**
 * Main class for the MCPClient mod. Handles initialization and lifecycle events
 * such as client tick events, HUD rendering, and client commands. Initializes
 * necessary managers and storage components.
 */
public class Mcpclient implements ClientModInitializer {

    private static SessionController sessionController;
    private static SpoofColors spoofColors;
    private static MinecraftClient minecraftClient;
    private KeybindingManager keybindingManager;
    private static PlayerTracker playerTracker;
    private static SettingsManager settingsManager;
    private static PluginChannelStorage pluginChannelStorage;
    private static PluginTabStorage pluginTabStorage;
    private static GifTextureManager gifTextureManager;
    private static boolean MCPToolAPIEnabled = false;

    /**
     * Initializes the mod by setting up all required components and event handlers.
     * Registers client tick events, HUD rendering, commands, and other necessary systems.
     */
    @Override
    public void onInitializeClient() {
        start();

        try {
            Mcpclient.getSettings().load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String originalUUID = sessionController.getOriginalUuid();
        sessionController.manageUuid(originalUUID, true);
        sessionController.manageUuid(originalUUID, false);
        pluginChannelStorage.loadVulnerablePluginMessages();

        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            keybindingManager.tick(client);
        });
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            PasswordRender.onHudRender(drawContext);
        });
        HudRenderCallback.EVENT.register(new HudRenderer());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandManager.registerCommands(dispatcher);
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            gifTextureManager = new GifTextureManager("textures/gui/background.gif", 60);
        });
        PayloadRegistry.registerAllPayloads();
    }

    /**
     * Initializes all components of the mod, including managers and storage systems.
     */
    private void start() {
        minecraftClient = MinecraftClient.getInstance();
        playerTracker = new PlayerTracker();
        settingsManager = new SettingsManager();
        keybindingManager = new KeybindingManager();
        sessionController = new SessionController();
        spoofColors = new SpoofColors();
        pluginChannelStorage = new PluginChannelStorage();
        pluginTabStorage = new PluginTabStorage();
    }

    /**
     * Handles client tick events. This method is used to check and save passwords
     * associated with the player.
     *
     * @param client The Minecraft client instance that is being ticked.
     */
    private void onClientTick(MinecraftClient client) {
        playerTracker.checkAndSavePasswords();
    }

    /**
     * Retrieves the Minecraft client instance.
     *
     * @return The Minecraft client instance.
     */
    public static MinecraftClient getMinecraftClient() {
        return minecraftClient;
    }

    /**
     * Retrieves the PlayerTracker instance used to track player-related data.
     *
     * @return The PlayerTracker instance.
     */
    public static PlayerTracker getPlayerTracker() {
        return playerTracker;
    }

    /**
     * Retrieves the SessionController instance used to manage session-related actions.
     *
     * @return The SessionController instance.
     */
    public static SessionController getSessionController() {
        return sessionController;
    }

    /**
     * Retrieves the SpoofColors instance used to handle spoofing colors in the client.
     *
     * @return The SpoofColors instance.
     */
    public static SpoofColors getSpoofColors() {
        return spoofColors;
    }

    /**
     * Retrieves the Settings instance used to manage the configuration and settings of the client.
     *
     * @return The Settings instance.
     */
    public static SettingsManager getSettings() {
        return settingsManager;
    }

    /**
     * Retrieves the PluginChannelStorage instance used to store and manage plugin channel data.
     *
     * @return The PluginChannelStorage instance.
     */
    public static PluginChannelStorage getPluginChannelStorage() {
        return pluginChannelStorage;
    }

    /**
     * Retrieves the PluginTabStorage instance used to store and manage plugin tab data.
     *
     * @return The PluginTabStorage instance.
     */
    public static PluginTabStorage getPluginTabStorage() {
        return pluginTabStorage;
    }

    /**
     * Checks if the MCPTool API is enabled.
     *
     * @return true if the MCPTool API is enabled, false otherwise.
     */
    public static boolean isMCPToolAPIEnabled() {
        return MCPToolAPIEnabled;
    }

    /**
     * Sets the status of the MCPTool API.
     *
     * @param status true to enable the MCPTool API, false to disable.
     */
    public static void setMCPToolAPIEnabled(boolean status) {
        MCPToolAPIEnabled = status;
    }

    /**
     * Retrieves the GifTextureManager instance.
     *
     * @return The GifTextureManager instance.
     */
    public static GifTextureManager getGifTextureManager() {
        return gifTextureManager;
    }
}
