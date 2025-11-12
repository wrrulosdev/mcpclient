package dev.wrrulosdev.mcpclient.client.utils.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfirmationSystem {
    
    static final Text confirmationMCPToolTitleText = Text.literal("§c§lMCP§f§lTool §7Action");

    /**
     * Shows a confirmation dialog with custom title, message and actions.
     *
     * @param client the Minecraft client instance
     * @param title the title of the confirmation dialog
     * @param message the message to display
     * @param onConfirm action to execute when confirmed
     * @param onCancel action to execute when cancelled (optional)
     */
    public static void showConfirmation(MinecraftClient client, Text title, Text message, 
                                      Runnable onConfirm, Runnable onCancel) {
        Screen previousScreen = client.currentScreen;
        
        client.setScreen(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    onConfirm.run();
                } else {
                    if (onCancel != null) {
                        onCancel.run();
                    } else {
                        client.setScreen(previousScreen);
                    }
                }
            },
            title,
            message
        ));
    }
    
    /**
     * Shows a confirmation dialog with default cancel action (return to previous screen).
     *
     * @param client the Minecraft client instance
     * @param title the title of the confirmation dialog
     * @param message the message to display
     * @param onConfirm action to execute when confirmed
     */
    public static void showConfirmation(MinecraftClient client, Text title, Text message, Runnable onConfirm) {
        showConfirmation(client, title, message, onConfirm, null);
    }
    
    /**
     * Shows a server connection confirmation dialog.
     *
     * @param client the Minecraft client instance
     * @param serverAddress the server address to connect to
     * @param onConfirm action to execute when confirmed
     */
    public static void showServerConnectionConfirmation(MinecraftClient client, String serverAddress, Runnable onConfirm) {
        showConfirmation(
            client,
            ConfirmationSystem.confirmationMCPToolTitleText,
            Text.literal("§7Do you want to connect to:\n§e" + serverAddress + "§r?\n\n§7This will disconnect you from your current server."),
            onConfirm
        );
    }
}