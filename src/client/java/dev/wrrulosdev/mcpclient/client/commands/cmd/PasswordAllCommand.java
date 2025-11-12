package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.passwords.PlayerTracker;
import dev.wrrulosdev.mcpclient.client.passwords.request.PlayerPasswords;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import dev.wrrulosdev.mcpclient.client.utils.validators.MinecraftValidator;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PasswordAllCommand implements Command {

    /**
     * Registers the passwordall command with the client command system.
     * This method associates the passwordall command with the `executePasswordAll` method.
     *
     * @return a LiteralArgumentBuilder used to register the command.
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("passwordall")
            .executes(this::executePasswordAll);
    }

    /**
     * Executes the passwordall command and checks for all users with potential password leaks.
     * This method retrieves a list of all players on the server and categorizes them based on whether
     * they have clean or obfuscated passwords, then sends the results to the user.
     *
     * @param context the command context containing information about the command execution.
     * @return an integer representing the success status of the command execution.
     */
    private int executePasswordAll(CommandContext<FabricClientCommandSource> context) {
        if (!Mcpclient.isMCPToolAPIEnabled()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cYou must have MCPTool open for this option and purchase the Pro plan.");
            return 0;
        }

        MinecraftClient client = Mcpclient.getMinecraftClient();
        PlayerTracker tracker = Mcpclient.getPlayerTracker();

        if (client.player == null) return 1;

        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&fSearching the databases for all players on the server...");
        List<String> onlyPasswordsUsers = new ArrayList<>();
        List<String> onlyObfuscatedUsers = new ArrayList<>();
        List<String> bothPasswordsUsers = new ArrayList<>();

        client.player.networkHandler.getPlayerList().forEach(player -> {
            String username = player.getProfile().getName();
            if (!MinecraftValidator.isValidMinecraftUsername(username)) return;

            PlayerPasswords passwords = tracker.getPasswordsForPlayer(username);
            boolean hasClean = !passwords.passwords().passwords().isEmpty();
            boolean hasObfuscated = !passwords.passwords().obfuscatedPasswords().isEmpty();

            if (hasClean || hasObfuscated) {
                if (hasClean && hasObfuscated) {
                    bothPasswordsUsers.add(username);
                } else if (hasClean) {
                    onlyPasswordsUsers.add(username);
                } else {
                    onlyObfuscatedUsers.add(username);
                }
            }
        });

        return buildAndSendResponse(onlyPasswordsUsers, onlyObfuscatedUsers, bothPasswordsUsers);
    }

    /**
     * Builds the response message to send to the user, categorizing the players based on password vulnerabilities.
     * This method groups players by their password status (clean, obfuscated, or both) and constructs a formatted
     * message indicating the vulnerable users found.
     *
     * @param green list of players with clean passwords.
     * @param red list of players with obfuscated passwords.
     * @param pink list of players with both clean and obfuscated passwords.
     * @return an integer representing the success status of the command execution.
     */
    private int buildAndSendResponse(List<String> green, List<String> red, List<String> pink) {
        StringBuilder result = new StringBuilder();
        List<String> allUsers = new ArrayList<>();
        addGroup(result, "&a", green, allUsers);
        addGroup(result, "&d", pink, allUsers);
        addGroup(result, "&c", red, allUsers);

        if (allUsers.isEmpty()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cNo users with leaked passwords were found");
            return 1;
        }

        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&fVulnerable users found: " + result.toString().trim());
        return 0;
    }

    /**
     * Adds a group of players to the response message, including the color formatting and the player's usernames.
     * The users are appended to the provided StringBuilder and added to the allUsers list.
     *
     * @param builder the StringBuilder to append the formatted group of users.
     * @param color the color code to use for formatting the group.
     * @param users the list of users to be added to the group.
     * @param allUsers the list of all users, used to track all players with passwords.
     */
    private void addGroup(StringBuilder builder, String color, List<String> users, List<String> allUsers) {
        if (!users.isEmpty()) {
            builder.append(color);
            users.forEach(user -> {
                builder.append(user).append(" ");
                allUsers.add(user);
            });
        }
    }
}
