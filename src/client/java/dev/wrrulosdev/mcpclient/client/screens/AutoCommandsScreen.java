package dev.wrrulosdev.mcpclient.client.screens;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class AutoCommandsScreen extends Screen {

    private final Screen parent;
    private int currentCommandIndex = 0;
    private TextWidget commandLabel;
    private TextFieldWidget commandInputField;

    /**
     * Constructs the AutoCommands screen with a reference to the parent screen.
     *
     * @param parent The screen to return to when this screen is closed.
     */
    public AutoCommandsScreen(Screen parent) {
        super(Text.literal("AutoCommands Settings").setStyle(Style.EMPTY.withColor(Formatting.RED)));
        this.parent = parent;
    }

    /**
     * Initializes the UI components on the screen.
     */
    @Override
    public void init() {
        super.init();
        setupUIComponents();
        addNavigationButton();
    }

    /**
     * Sets up the command label, input field, and navigation buttons.
     */
    private void setupUIComponents() {
        int centerX = width / 2;
        int baseY = (height - 60) / 2;

        commandLabel = new TextWidget(
            centerX - 75,
            baseY - 25,
            150, 20,
            createLabelText(),
            textRenderer
        );
        commandLabel.alignCenter();
        addDrawableChild(commandLabel);

        addDrawableChild(ButtonWidget.builder(Text.literal("←"), button -> navigate(-1))
            .position(centerX - 105, baseY)
            .size(20, 20)
            .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("→"), button -> navigate(1))
            .position(centerX + 85, baseY)
            .size(20, 20)
            .build());

        commandInputField = new TextFieldWidget(
            textRenderer,
            centerX - 75,
            baseY,
            150, 20,
            Text.empty()
        );
        commandInputField.setMaxLength(100);
        commandInputField.setText(getCurrentCommand());
        commandInputField.setChangedListener(this::onInputChanged);
        addDrawableChild(commandInputField);
    }

    /**
     * Navigates to a different command slot.
     *
     * @param direction The direction to move (-1 for previous, 1 for next).
     */
    private void navigate(int direction) {
        currentCommandIndex = (currentCommandIndex + direction + 6) % 6;
        updateCommandDisplay();
    }

    /**
     * Updates the label and input field to reflect the current command slot.
     */
    private void updateCommandDisplay() {
        commandLabel.setMessage(createLabelText());
        commandInputField.setText(getCurrentCommand());
    }

    /**
     * Creates the label text for the current command slot.
     *
     * @return A styled {@link Text} object showing the current command index.
     */
    private Text createLabelText() {
        return Text.literal("AutoCommand " + (currentCommandIndex + 1))
            .copy()
            .setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));
    }

    /**
     * Retrieves the current command string from the settings.
     *
     * @return The command assigned to the current index.
     */
    private String getCurrentCommand() {
        return Mcpclient.getSettings().getAutoCommandsSettings().getCommandByIndex(currentCommandIndex);
    }

    /**
     * Called when the input text changes. Updates the command setting.
     *
     * @param text The new command text entered by the user.
     */
    private void onInputChanged(String text) {
        Mcpclient.getSettings().getAutoCommandsSettings().setCommandByIndex(currentCommandIndex, text);
    }

    /**
     * Adds the "Back" button to return to the parent screen.
     */
    private void addNavigationButton() {
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), this::onBack)
            .width(100)
            .position((width - 100) / 2, height / 2)
            .build());
    }

    /**
     * Callback for the back button.
     *
     * @param button The button instance clicked.
     */
    private void onBack(ButtonWidget button) {
        try {
            Mcpclient.getSettings().save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mcpclient.getMinecraftClient().setScreen(parent);
    }

    /**
     * Renders the screen and its components.
     *
     * @param context The draw context.
     * @param mouseX  The mouse X position.
     * @param mouseY  The mouse Y position.
     * @param delta   The tick delta.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderScreenTitle(context);
    }

    /**
     * Renders the screen title centered at the top.
     *
     * @param context The draw context.
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
     * Closes the screen and returns to the parent.
     */
    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}