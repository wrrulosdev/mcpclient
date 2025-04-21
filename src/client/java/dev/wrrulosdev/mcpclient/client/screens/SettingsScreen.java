package dev.wrrulosdev.mcpclient.client.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SettingsScreen extends Screen {

    private final Screen parent;

    /**
     * Constructs a new settings screen.
     * @param parent The parent screen to return to when exiting this screen.
     */
    public SettingsScreen(Screen parent) {
        super(Text.literal("MCPClient Settings").setStyle(Style.EMPTY.withColor(Formatting.RED)));
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        int buttonWidth = 150;
        int buttonHeight = 20;
        int yStart = this.height / 2 - 60;
        int spacing = 25;

        // Add feature buttons
        this.addDrawableChild(createFeatureButton("Spoofing", yStart + spacing, buttonWidth, buttonHeight));
        this.addDrawableChild(createFeatureButton("LuckPerms Edit", yStart + spacing * 2, buttonWidth, buttonHeight));
        this.addDrawableChild(createFeatureButton("AutoCommand", yStart + spacing * 3, buttonWidth, buttonHeight));
        this.addDrawableChild(createFeatureButton("Password Tracker", yStart + spacing * 4, buttonWidth, buttonHeight));

        // Add back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> close())
            .position(this.width / 2 - 75, yStart + spacing * 5)
            .size(buttonWidth, buttonHeight)
            .build()
        );
    }

    /**
     * Creates a button for a specific feature.
     * @param text The display text for the button.
     * @param y The vertical position of the button.
     * @param width The width of the button.
     * @param height The height of the button.
     * @return The configured ButtonWidget.
     */
    private ButtonWidget createFeatureButton(String text, int y, int width, int height) {
        return ButtonWidget.builder(Text.literal(text), button -> handleFeatureAction(text))
            .position(this.width / 2 - width / 2, y)
            .size(width, height)
            .build();
    }

    /**
     * Handles actions for feature buttons based on the button text.
     * @param feature The name of the feature associated with the button.
     */
    private void handleFeatureAction(String feature) {
        switch(feature) {
            case "Spoofing" -> toggleSpoofing();
            case "LuckPerms Edit" -> onLuckPermsEditClick();
            case "AutoCommand" -> openAutoCommandSettings();
            case "Password Tracker" -> togglePasswordTracker();
        }
    }

    /**
     * Toggles the spoofing feature and opens the spoofing configuration screen.
     */
    private void toggleSpoofing() {
        if (client != null) client.setScreen(new SpoofSectionScreen(this));
    }

    /**
     * Opens the luckperms editor configuration screen.
     */
    private void onLuckPermsEditClick() {
        if (client != null) client.setScreen(new LuckPermsEditorScreen(this));
    }

    /**
     * Opens the AutoCommand settings screen (to be implemented).
     */
    private void openAutoCommandSettings() {
        if (client != null) client.setScreen(new AutoCommandsScreen(this));
    }

    /**
     * Toggles the password tracker feature on or off.
     */
    private void togglePasswordTracker() {
        if (client != null) client.setScreen(new PasswordTrackerScreen(this));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Draw the screen title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            20,
            0xFFFFFF
        );
    }

    /**
     * Closes the settings screen and returns to the parent screen.
     */
    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}