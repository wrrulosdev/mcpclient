package dev.wrrulosdev.mcpclient.client.passwords;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.passwords.request.PlayerPasswords;
import dev.wrrulosdev.mcpclient.client.utils.validators.MinecraftValidator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordRender {

    private static final int PADDING = 4;
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int USERNAME_LABEL_COLOR = 0xFFFFFF;
    private static final int USERNAME_VALUE_COLOR = 0xD8B7DD;
    private static final int NO_PASSWORDS_COLOR = 0xFF7F7F;
    private static final int HAS_PASSWORDS_COLOR = 0x90EE90;
    private static final Map<String, String> usernameMap = new HashMap<>();

    /**
     * Checks if the local player is looking at the specified target player.
     *
     * @param target The target player entity to check.
     * @return True if the local player is looking at the target player, otherwise false.
     */
    public static boolean isLookingAt(AbstractClientPlayerEntity target) {
        Entity targetedEntity = Mcpclient.getMinecraftClient().targetedEntity;
        return targetedEntity != null && targetedEntity.equals(target);
    }

    /**
     * Renders the password overlay on the screen. This includes displaying information
     * about the player the local player is looking at, including their username and password status.
     *
     * @param drawContext The draw context used to render on the screen.
     */
    public static void onHudRender(DrawContext drawContext) {
        if (!Mcpclient.getSettings().getPasswordTrackerSettings().isPlayerInfo()) return;
        MinecraftClient client = Mcpclient.getMinecraftClient();
        ClientWorld world = client.world;
        PlayerEntity localPlayer = client.player;
        if (world == null || localPlayer == null) return;

        world.getPlayers().forEach(target -> {
            if (target == localPlayer || !isLookingAt(target)) return;
            String username = target.getName().getString();
            if (!(MinecraftValidator.isValidMinecraftUsername(username))) return;
            usernameMap.putIfAbsent(username, username);

            PlayerPasswords playerPasswords = Mcpclient.getPlayerTracker().getPasswordsForPlayer(username);
            List<List<TextSegment>> lines = getLists(username, playerPasswords);
            int maxWidth = calculateMaxWidth(client, lines);
            int lineHeight = client.textRenderer.fontHeight;
            int backgroundHeight = (lineHeight * lines.size()) + PADDING * 2;
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int x = (screenWidth - maxWidth) / 2;
            int y = (screenHeight / 2) + 12;
            drawContext.fill(x, y, x + maxWidth, y + backgroundHeight, BACKGROUND_COLOR);

            int textY = y + PADDING;

            for (List<TextSegment> line : lines) {
                int textX = x + PADDING;
                for (TextSegment segment : line) {
                    drawContext.drawText(
                        client.textRenderer,
                        segment.text(),
                        textX,
                        textY,
                        segment.color(),
                        true
                    );
                    textX += client.textRenderer.getWidth(segment.text());
                }
                textY += lineHeight;
            }
        });
    }

    /**
     * Creates a list of text segments to display for a player's username and password status.
     *
     * @param username The player's username.
     * @param playerPasswords The player's password information.
     * @return A list of text segments for rendering.
     */
    private static @NotNull List<List<TextSegment>> getLists(String username, PlayerPasswords playerPasswords) {
        List<List<TextSegment>> lines = new ArrayList<>();
        List<TextSegment> usernameLine = new ArrayList<>();

        usernameLine.add(new TextSegment("Username: ", USERNAME_LABEL_COLOR));
        usernameLine.add(new TextSegment(username, USERNAME_VALUE_COLOR));
        lines.add(usernameLine);

        List<TextSegment> passwordLine = new ArrayList<>();
        String statusText = getString(playerPasswords);
        int statusColor = playerPasswords.passwords().passwords().isEmpty() ?
            NO_PASSWORDS_COLOR : HAS_PASSWORDS_COLOR;

        passwordLine.add(new TextSegment(statusText, statusColor));
        lines.add(passwordLine);
        return lines;
    }

    /**
     * Determines the status text for the player's password information.
     *
     * @param playerPasswords The player's password information.
     * @return A string representing the password status.
     */
    private static @NotNull String getString(PlayerPasswords playerPasswords) {
        String statusText;
        boolean isPasswordsEmpty = playerPasswords.passwords().passwords().isEmpty();
        boolean isObfuscatedEmpty = playerPasswords.passwords().obfuscatedPasswords().isEmpty();

        if (isPasswordsEmpty && isObfuscatedEmpty) {
            statusText = "No passwords found";
        } else if (isPasswordsEmpty) {
            statusText = "Obfuscated passwords found";
        } else if (isObfuscatedEmpty) {
            statusText = "Available passwords";
        } else {
            statusText = "Available passwords (both)";
        }
        return statusText;
    }

    /**
     * Calculates the maximum width of the background box that holds the text.
     *
     * @param client The Minecraft client instance used to retrieve the text renderer.
     * @param lines A list of text lines to be displayed.
     * @return The maximum width of the text lines.
     */
    private static int calculateMaxWidth(MinecraftClient client, List<List<TextSegment>> lines) {
        int maxWidth = 0;
        for (List<TextSegment> line : lines) {
            int lineWidth = line.stream()
                .mapToInt(segment -> client.textRenderer.getWidth(segment.text()))
                .sum();
            maxWidth = Math.max(maxWidth, lineWidth);
        }
        return maxWidth + PADDING * 2;
    }

    /**
     * Helper record to represent a text segment with a specific color.
     *
     * @param text The text to be displayed.
     * @param color The color of the text.
     */
    private record TextSegment(String text, int color) {}
}
