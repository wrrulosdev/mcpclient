package dev.wrrulosdev.mcpclient.client.settings;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsManager {

    private static final String MC_FOLDER = ".minecraft";
    private static final String CONFIG_FILE = "mcpclient_settings.json";
    private final transient Path configPath;
    private final transient Gson gson;
    private AutoCommandsSettings autoCommands;
    private LuckPermsEditorSettings luckPermsEditor;
    private PasswordTrackerSettings passwordTracker;

    /**
     * Constructs a new SettingsManager, determining the config path based on OS
     * and initializing defaults and JSON engine.
     */
    public SettingsManager() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            this.configPath = Paths.get(appdata, MC_FOLDER, CONFIG_FILE);
        } else {
            String home = System.getProperty("user.home");
            this.configPath = Paths.get(home, MC_FOLDER, CONFIG_FILE);
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.autoCommands = new AutoCommandsSettings();
        this.luckPermsEditor = new LuckPermsEditorSettings();
        this.passwordTracker = new PasswordTrackerSettings();
    }

    /**
     * Loads settings from disk into this manager, or keeps defaults if none exist.
     * @throws IOException if reading the config file fails
     */
    public void load() throws IOException {
        if (Files.exists(configPath)) {
            String json = Files.readString(configPath);
            SettingsManager loaded = gson.fromJson(json, SettingsManager.class);
            this.autoCommands = loaded.autoCommands;
            this.luckPermsEditor = loaded.luckPermsEditor;
            this.passwordTracker = loaded.passwordTracker;
        }
    }

    /**
     * Saves the current settings to disk, creating directories if needed.
     * Omits empty "ranks" array in luckPermsEditor if no ranks are set.
     * @throws IOException if writing the config file fails
     */
    public void save() throws IOException {
        Path parent = configPath.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        JsonElement tree = gson.toJsonTree(this);
        JsonObject root = tree.getAsJsonObject();

        // Remove "ranks" from luckPermsEditor
        if (root.has("luckPermsEditor")) {
            JsonObject lpObj = root.getAsJsonObject("luckPermsEditor");
            if (lpObj.has("ranks")) {
                lpObj.remove("ranks");
            }
        }

        String json = gson.toJson(root);
        Files.writeString(configPath, json);
    }


    /**
     * @return the AutoCommandsSettings instance
     */
    public AutoCommandsSettings getAutoCommandsSettings() {
        return autoCommands;
    }

    /**
     * @return the LuckPermsEditorSettings instance
     */
    public LuckPermsEditorSettings getLuckPermsEditorSettings() {
        return luckPermsEditor;
    }

    /**
     * @return the PasswordTrackerSettings instance
     */
    public PasswordTrackerSettings getPasswordTrackerSettings() {
        return passwordTracker;
    }
}
