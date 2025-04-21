package dev.wrrulosdev.mcpclient.client.screens;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.spoofing.SessionController;
import dev.wrrulosdev.mcpclient.client.spoofing.SpoofColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpoofSectionScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final int BUTTON_WIDTH = 50;
    private static final int X_POSITION = -100;
    private static final int START_Y = -110;
    private static final int Y_SPACING = 45;

    private final SessionController sessionController;
    private final Screen parent;
    private final SpoofColors spoofColors;

    private TextFieldWidget usernameField;
    private TextFieldWidget uuidField;
    private TextFieldWidget fakeIPField;
    private TextFieldWidget fakeHostnameField;

    private TextWidget usernameLabel;
    private TextWidget uuidLabel;
    private TextWidget fakeIPLabel;
    private TextWidget fakeHostnameLabel;

    /**
     * Constructs a new spoof configuration screen.
     *
     * @param parent The parent screen to return to when exiting this screen.
     */
    public SpoofSectionScreen(Screen parent) {
        super(Text.literal("Spoof section"));
        this.parent = parent;
        this.sessionController = Mcpclient.getSessionController();
        this.spoofColors = Mcpclient.getSpoofColors();
    }

    @Override
    protected void init() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int y = START_Y;

        // Username Section
        usernameField = createTextFieldSection(textRenderer, y,
            "Username", 16,
            sessionController::getUsername,
            this::updateUsernameText,
            label -> this.usernameLabel = label
        );
        y += Y_SPACING;

        // UUID Section
        uuidField = createTextFieldSection(textRenderer, y,
            "UUID", 36,
            sessionController::getCurrentUuid,
            this::updateUUIDText,
            label -> this.uuidLabel = label
        );
        createStatusButton(y,
            this::getUUIDStatus,
            this::toggleUUIDStatus
        );
        y += Y_SPACING;

        // FakeIP Section
        fakeIPField = createTextFieldSection(textRenderer, y,
            "FakeIP", 16,
            sessionController::getFakeIp,
            this::updateFakeIPText,
            label -> this.fakeIPLabel = label
        );
        createStatusButton(y,
            this::getFakeIPStatus,
            this::toggleFakeIPStatus
        );
        y += Y_SPACING;

        // FakeHostname Section
        fakeHostnameField = createTextFieldSection(textRenderer, y,
            "FakeHostname", 16,
            sessionController::getFakeHostname,
            this::updateHostnameText,
            label -> this.fakeHostnameLabel = label
        );
        createStatusButton(y,
            this::getHostnameStatus,
            this::toggleHostnameStatus
        );

        // Control buttons
        addDrawableChild(createActionButton("Back", this.height / 2 + 75, this::onBack));
        addDrawableChild(createActionButton("Save", this.height / 2 + 100, this::onSave));
    }

    /**
     * Creates a labeled text field section with validation.
     *
     * @param renderer The text renderer for GUI elements.
     * @param yOffset The vertical starting position for the field.
     * @param hint The placeholder text for the field.
     * @param maxLength The maximum allowed input length.
     * @param textSupplier A supplier for the initial field value.
     * @param updater A consumer to handle text updates and validation.
     * @param labelConsumer A consumer to store the created label reference.
     * @return The configured TextFieldWidget.
     */
    private TextFieldWidget createTextFieldSection(TextRenderer renderer, int yOffset, String hint,
                                                   int maxLength, Supplier<String> textSupplier,
                                                   Consumer<TextFieldWidget> updater,
                                                   Consumer<TextWidget> labelConsumer) {
        // Create label
        TextWidget label = new TextWidget(X_POSITION + width / 2, yOffset + height / 2, FIELD_WIDTH, 20,
            Text.empty(), renderer
        );
        labelConsumer.accept(label);
        addDrawableChild(label);

        // Create text field
        TextFieldWidget field = new TextFieldWidget(renderer,
            X_POSITION + width / 2, yOffset + height / 2 + 20,
            FIELD_WIDTH, 20, Text.literal(hint)
        );
        field.setMaxLength(maxLength);
        field.setText(textSupplier.get());
        field.setChangedListener(t -> updater.accept(field));
        addDrawableChild(field);

        updater.accept(field);
        return field;
    }

    /**
     * Creates a status toggle button with dynamic text.
     *
     * @param yOffset The vertical position offset for the button.
     * @param textSupplier A supplier for the button's text.
     * @param action The click handler for the button.
     * @return The configured ButtonWidget.
     */
    private ButtonWidget createStatusButton(int yOffset, Supplier<Text> textSupplier, Consumer<ButtonWidget> action) {
        ButtonWidget button = ButtonWidget.builder(textSupplier.get(), action::accept)
            .width(BUTTON_WIDTH)
            .position(X_POSITION + width / 2 - 55, yOffset + height / 2 + 20)
            .build();
        addDrawableChild(button);
        return button;
    }

    /**
     * Creates a full-width action button.
     *
     * @param text The display text for the button.
     * @param y The vertical position for the button.
     * @param action The click handler for the button.
     * @return The configured ButtonWidget.
     */
    private ButtonWidget createActionButton(String text, int y, Consumer<ButtonWidget> action) {
        return ButtonWidget.builder(Text.literal(text), action::accept)
            .width(FIELD_WIDTH)
            .position(X_POSITION + width / 2, y)
            .build();
    }

    // Field update handlers

    /**
     * Updates the username label based on input validation.
     *
     * @param field The username input field.
     */
    private void updateUsernameText(TextFieldWidget field) {
        updateLabel(field, usernameLabel, t -> Text.literal("Username").setStyle(spoofColors.getUsernameTextColor(t)));
    }

    /**
     * Updates the UUID label based on input validation.
     *
     * @param field The UUID input field.
     */
    private void updateUUIDText(TextFieldWidget field) {
        updateLabel(field, uuidLabel, t -> Text.literal("UUID (Bungee)").setStyle(spoofColors.getUUIDTextColor(t)));
    }

    /**
     * Updates the FakeIP label based on input validation.
     *
     * @param field The FakeIP input field.
     */
    private void updateFakeIPText(TextFieldWidget field) {
        updateLabel(field, fakeIPLabel, t -> Text.literal("FakeIP (Bungee)").setStyle(spoofColors.getFakeIPTextColor(t)));
    }

    /**
     * Updates the FakeHostname label based on input validation.
     *
     * @param field The FakeHostname input field.
     */
    private void updateHostnameText(TextFieldWidget field) {
        updateLabel(field, fakeHostnameLabel, t -> Text.literal("FakeHostname").setStyle(spoofColors.getFakeHostnameTextColor(t)));
    }

    /**
     * Updates a text label's style based on input validation.
     *
     * @param field The input field.
     * @param label The associated text label.
     * @param textGenerator A function to generate styled text based on input.
     */
    private void updateLabel(TextFieldWidget field, TextWidget label, Function<String, Text> textGenerator) {
        label.setMessage(textGenerator.apply(field.getText()));
    }

    // Status management

    /**
     * Generates status text with appropriate styling.
     *
     * @param status The current status (enabled/disabled).
     * @return The styled status text.
     */
    private Text getStatusText(boolean status) {
        return Text.literal(status ? "ON" : "OFF")
            .setStyle(spoofColors.getStatusTextColor(status));
    }

    /**
     * Gets the current UUID spoof status.
     *
     * @return The styled status text.
     */
    private Text getUUIDStatus() { return getStatusText(sessionController.isUuidSpoofEnabled()); }

    /**
     * Gets the current FakeIP spoof status.
     *
     * @return The styled status text.
     */
    private Text getFakeIPStatus() { return getStatusText(sessionController.isFakeIpEnabled()); }

    /**
     * Gets the current FakeHostname spoof status.
     *
     * @return The styled status text.
     */
    private Text getHostnameStatus() { return getStatusText(sessionController.isFakeHostnameEnabled()); }

    // Toggle handlers

    /**
     * Toggles the UUID spoof status and updates the UI.
     *
     * @param b The button that triggered the toggle.
     */
    private void toggleUUIDStatus(ButtonWidget b) {
        boolean newState = !sessionController.isUuidSpoofEnabled();
        sessionController.toggleSpoofState(
            newState,
            sessionController.isFakeIpEnabled(),
            sessionController.isFakeHostnameEnabled()
        );
        updateUUIDText(usernameField);
        updateStatusButton(b, newState, uuidField);
    }

    /**
     * Toggles the FakeIP spoof status and updates the UI.
     *
     * @param b The button that triggered the toggle.
     */
    private void toggleFakeIPStatus(ButtonWidget b) {
        boolean newState = !sessionController.isFakeIpEnabled();
        sessionController.toggleSpoofState(
            sessionController.isUuidSpoofEnabled(),
            newState,
            sessionController.isFakeHostnameEnabled()
        );
        updateFakeIPText(fakeIPField);
        updateStatusButton(b, newState, fakeIPField);
    }

    /**
     * Toggles the FakeHostname spoof status and updates the UI.
     *
     * @param b The button that triggered the toggle.
     */
    private void toggleHostnameStatus(ButtonWidget b) {
        boolean newState = !sessionController.isFakeHostnameEnabled();
        sessionController.toggleSpoofState(
            sessionController.isUuidSpoofEnabled(),
            sessionController.isFakeIpEnabled(),
            newState
        );
        updateHostnameText(fakeHostnameField);
        updateStatusButton(b, newState, fakeHostnameField);
    }

    /**
     * Updates a status button and linked field state.
     *
     * @param button The button to update.
     * @param newState The new activation state.
     * @param field The linked text field.
     */
    private void updateStatusButton(ButtonWidget button, boolean newState, TextFieldWidget field) {
        button.setMessage(getStatusText(newState));
        field.active = newState;
    }

    // Button actions

    /**
     * Handles the back button click, returning to the parent screen.
     *
     * @param b The back button.
     */
    private void onBack(ButtonWidget b) {
        MinecraftClient.getInstance().setScreen(parent);
    }

    /**
     * Handles the save button click, updating session properties and returning to the parent screen.
     *
     * @param b The save button.
     */
    private void onSave(ButtonWidget b) {
        if (hasValidationErrors()) return;

        sessionController.updateSessionProperty(
            usernameField.getText(),
            uuidField.getText()
        );

        sessionController.setFakeIp(fakeIPField.getText());
        sessionController.setFakeHostname(fakeHostnameField.getText());

        try {
            Mcpclient.getSettings().save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        onBack(b);
    }

    /**
     * Checks for validation errors in any active spoofing fields.
     *
     * @return True if any active field has validation errors.
     */
    private boolean hasValidationErrors() {
        return sessionController.hasValidationErrors();
    }
}
