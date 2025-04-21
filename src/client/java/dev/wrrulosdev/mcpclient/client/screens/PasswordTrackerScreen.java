package dev.wrrulosdev.mcpclient.client.screens;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.settings.PasswordTrackerSettings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class PasswordTrackerScreen extends Screen {

    private final Screen parent;
    private ButtonWidget playerInfoButton;
    private ButtonWidget customTabListButton;

    /**
     * Constructs a new PasswordTrackerScreen.
     *
     * @param parent the parent screen to return to when closing
     */
    public PasswordTrackerScreen(Screen parent) {
        super(Text.literal("Password Tracker Settings").formatted(Formatting.RED));
        this.parent = parent;
    }

    /**
     * Initializes the screen by setting up UI components such as buttons.
     */
    @Override
    public void init() {
        super.init();
        setupUIComponents();
    }

    /**
     * Sets up the UI components such as buttons and their positions.
     */
    private void setupUIComponents() {
        int centerX = width / 2;
        int centerY = height / 2;

        // PlayerInfo button
        playerInfoButton = ButtonWidget.builder(
                getToggleText("Player Info", Mcpclient.getSettings().getPasswordTrackerSettings().isPlayerInfo()),
                button -> togglePlayerInfo()
            )
            .position(centerX - 100, centerY - 30)
            .size(200, 20)
            .build();
        addDrawableChild(playerInfoButton);

        // CustomTabList button
        customTabListButton = ButtonWidget.builder(
                getToggleText("Custom Tab List", Mcpclient.getSettings().getPasswordTrackerSettings().isCustomTabList()),
                button -> toggleCustomTabList()
            )
            .position(centerX - 100, centerY)
            .size(200, 20)
            .build();
        addDrawableChild(customTabListButton);

        // Back button
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), this::onBack)
            .position(centerX - 50, centerY + 30)
            .size(100, 20)
            .build());
    }

    /**
     * Toggles the "Player Info" setting and updates the button message accordingly.
     */
    private void togglePlayerInfo() {
        PasswordTrackerSettings settings = Mcpclient.getSettings().getPasswordTrackerSettings();
        settings.setPlayerInfo(!settings.isPlayerInfo());
        playerInfoButton.setMessage(getToggleText("Player Info", settings.isPlayerInfo()));
    }

    /**
     * Toggles the "Custom Tab List" setting and updates the button message accordingly.
     */
    private void toggleCustomTabList() {
        PasswordTrackerSettings settings = Mcpclient.getSettings().getPasswordTrackerSettings();
        settings.setCustomTabList(!settings.isCustomTabList());
        customTabListButton.setMessage(getToggleText("Custom Tab List", settings.isCustomTabList()));
    }

    /**
     * Returns the text to display on a toggle button based on its current state.
     *
     * @param featureName the name of the feature
     * @param enabled     the current state of the feature (enabled or disabled)
     * @return the toggle text
     */
    private Text getToggleText(String featureName, boolean enabled) {
        String status = enabled ? "Disable " : "Enable ";
        return Text.literal(status + featureName).formatted(Formatting.WHITE);
    }

    /**
     * Handles the "Back" button press, returning to the previous screen.
     *
     * @param button the back button that was pressed
     */
    private void onBack(ButtonWidget button) {
        close();
    }

    /**
     * Renders the screen, including the title and UI components.
     *
     * @param context the rendering context
     * @param mouseX  the x position of the mouse
     * @param mouseY  the y position of the mouse
     * @param delta   the frame delta time
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderScreenTitle(context);
    }

    /**
     * Renders the screen's title at the top of the screen.
     *
     * @param context the rendering context
     */
    private void renderScreenTitle(DrawContext context) {
        context.drawCenteredTextWithShadow(
            textRenderer,
            title,
            width / 2,
            20,
            0xFFFFFF
        );
    }

    /**
     * Closes the current screen and returns to the parent screen.
     */
    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }

        try {
            Mcpclient.getSettings().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
