package dev.wrrulosdev.mcpclient.client.screens;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.settings.LuckPermsEditorSettings;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LuckPermsEditorScreen extends Screen {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Screen parent;
    private TextFieldWidget inputField;

    /**
     * Constructs a new LuckPermsEditorScreen.
     *
     * @param parent the parent screen to return to
     */
    public LuckPermsEditorScreen(Screen parent) {
        super(Text.literal("LuckPermsEditor Settings").setStyle(Style.EMPTY.withColor(Formatting.RED)));
        this.parent = parent;
    }

    /**
     * Initializes the UI components on the screen.
     */
    @Override
    public void init() {
        super.init();
        int buttonWidth = 150;
        int baseY = (height - 40) / 2;

        addDrawableChild(new TextWidget(centerX(100), baseY - 60, 100, 20, Text.literal("Editor mode"), textRenderer));

        ButtonWidget modeButton = createCenteredButton(
            buttonWidth,
            baseY - 30,
            getModeText(),
            this::toggleLuckPermsMode
        );
        addDrawableChild(modeButton);

        inputField = new TextFieldWidget(textRenderer, centerX(buttonWidth), baseY, buttonWidth, 20, Text.literal(""));
        inputField.setMessage(Text.literal(Mcpclient.getSettings().getLuckPermsEditorSettings().getNewValue()));
        inputField.setText(Mcpclient.getSettings().getLuckPermsEditorSettings().getNewValue());
        inputField.setMaxLength(50);
        inputField.setChangedListener(this::onInputChanged);
        inputField.active = noInputMode();
        addDrawableChild(inputField);

        addDrawableChild(createCenteredButton(buttonWidth, baseY + 40, Text.literal("Back"), this::onBack));
        addDrawableChild(createCenteredButton(buttonWidth, baseY + 65, Text.literal("Execute"), this::onExecute));
    }

    /**
     * Creates a centered button with specified width, position, text and action.
     *
     * @param width   the button width
     * @param y       the y position
     * @param text    the button text
     * @param action  the press action
     * @return the created button
     */
    private ButtonWidget createCenteredButton(int width, int y, Text text, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(text, action)
            .width(width)
            .position(centerX(width), y)
            .build();
    }

    /**
     * Calculates the centered x position for a given element width.
     *
     * @param elementWidth the width of the element
     * @return the x coordinate
     */
    private int centerX(int elementWidth) {
        return (width - elementWidth) / 2;
    }

    /**
     * Executes the LuckPerms task based on current settings.
     *
     * @param button the button pressed
     */
    private void onExecute(ButtonWidget button) {
        try {
            Mcpclient.getSettings().save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Mcpclient.getSettings().getLuckPermsEditorSettings().isEnabled()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cThere's already a luckperms task in progress. Please wait.");
            Msg.sendFormattedMessage("&cTask in progress!", true);
            close();
            return;
        }

        if ((Mcpclient.getSettings().getLuckPermsEditorSettings().getNewValue().isEmpty() || Mcpclient.getSettings().getLuckPermsEditorSettings().getNewValue().isBlank()) && noInputMode()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cFor this mode it is necessary to fill in the field with a valid value!");
            close();
            return;
        }

        Mcpclient.getSettings().getLuckPermsEditorSettings().setEnabled(true);
        if (client == null || client.player == null) return;

        executor.submit(() -> {
            client.player.networkHandler.sendChatMessage(Mcpclient.getSettings().getLuckPermsEditorSettings().getGroupCommand());
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            if (Mcpclient.getSettings().getLuckPermsEditorSettings().getRanks().isEmpty()) {
                Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cThe luckperms groups could not be retrieved");
                Msg.sendFormattedMessage("&cThe luckperms groups could not be retrieved", true);
                Mcpclient.getSettings().getLuckPermsEditorSettings().setEnabled(false);
                return;
            }

            AtomicInteger commandsExecuted = new AtomicInteger(0);

            Mcpclient.getSettings().getLuckPermsEditorSettings().getRanks().forEach(rank -> {
                if (Mcpclient.getSettings().getLuckPermsEditorSettings().getMode().equals(LuckPermsEditorSettings.DELETE_MODE) && rank.equals("default")) {
                    return;
                }

                String lpCommand = Mcpclient.getSettings().getLuckPermsEditorSettings().getLPCommand(rank);
                client.player.networkHandler.sendChatMessage(lpCommand);
                int currentCount = commandsExecuted.incrementAndGet();

                if (currentCount == 4) {
                    try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                    commandsExecuted.set(0);
                }

                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            });

            Mcpclient.getSettings().getLuckPermsEditorSettings().setEnabled(false);
            Mcpclient.getSettings().getLuckPermsEditorSettings().clearRanks();
            sendTaskCompletedMessage();
        });

        close();
    }

    /**
     * Toggles the current LuckPerms mode and updates the UI.
     *
     * @param button the button clicked
     */
    private void toggleLuckPermsMode(ButtonWidget button) {
        Mcpclient.getSettings().getLuckPermsEditorSettings().cycleMode();
        button.setMessage(getModeText());
        inputField.active = noInputMode();
    }

    /**
     * Called when the input field value changes.
     *
     * @param text the new input text
     */
    private void onInputChanged(String text) {
        Mcpclient.getSettings().getLuckPermsEditorSettings().setNewValue(text);
    }

    /**
     * Handles the action when the "Back" button is pressed.
     *
     * @param button the button pressed
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
     * Sends a success message when the LuckPerms task is completed.
     */
    private void sendTaskCompletedMessage() {
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&aThe luckperms task has been completed!");
        Msg.sendFormattedMessage("&aLuckperms task has been completed!", true);
    }

    /**
     * Returns the text representing the current mode.
     *
     * @return the mode text
     */
    private Text getModeText() {
        return Text.literal("Mode: " + Mcpclient.getSettings().getLuckPermsEditorSettings().getMode());
    }

    /**
     * Checks whether the current mode requires input.
     *
     * @return true if the input field should be disabled, false otherwise
     */
    private boolean noInputMode() {
        String mode = Mcpclient.getSettings().getLuckPermsEditorSettings().getMode();
        return (!mode.equals(LuckPermsEditorSettings.CLEAR_MODE)
            && !mode.equals(LuckPermsEditorSettings.DELETE_MODE)
            && !mode.equals(LuckPermsEditorSettings.DISPLAYNAME_MODE));
    }

    /**
     * Renders the screen content.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
    }

    /**
     * Called when the screen is closed.
     */
    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
