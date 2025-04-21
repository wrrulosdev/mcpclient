package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.passwords.request.PasswordFinder;
import dev.wrrulosdev.mcpclient.client.passwords.request.PlayerPasswords;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import static dev.wrrulosdev.mcpclient.client.utils.messages.Msg.ClickAction.COPY_TO_CLIPBOARD;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PasswordCommand implements Command {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * Registers the password command with the client command system.
     * This method sets up the password command and associates it with the `executePassword` method.
     * It also allows for the "username" argument to be provided and suggests usernames dynamically.
     *
     * @return a LiteralArgumentBuilder used to register the password command.
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("password")
            .executes(this::executeRoot)
            .then(argument("username", StringArgumentType.string())
                .suggests(this::suggestUsernames)
                .executes(this::executePassword)
            );
    }

    /**
     * Handles the case where no arguments are provided.
     *
     * @param context The command context.
     * @return Always returns 0 (success).
     */
    private int executeRoot(CommandContext<FabricClientCommandSource> context) {
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cUsage: /mcp password <username>");
        return 0;
    }

    /**
     * Suggests usernames based on the input provided by the user.
     * This method filters and suggests usernames that start with the current input.
     *
     * @param context the command context containing information about the command execution.
     * @param builder the SuggestionsBuilder used to add suggestions.
     * @return a CompletableFuture containing the suggestions for the user.
     */
    private CompletableFuture<Suggestions> suggestUsernames(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        String currentInput = builder.getRemaining().toLowerCase();
        MinecraftClient client = Mcpclient.getMinecraftClient();
        if (client.getNetworkHandler() == null) return builder.buildFuture();

        client.getNetworkHandler().getPlayerList().stream()
            .map(entry -> entry.getProfile().getName())
            .filter(Objects::nonNull)
            .filter(name -> name.toLowerCase().startsWith(currentInput))
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    /**
     * Executes the password command and retrieves the leaked passwords for the specified username.
     * This method retrieves the passwords (both clean and obfuscated) for the given user
     * and sends them to the user if found.
     *
     * @param context the command context containing information about the command execution.
     * @return an integer representing the success status of the command execution.
     */
    private int executePassword(CommandContext<FabricClientCommandSource> context) {
        if (!Mcpclient.isMCPToolAPIEnabled()) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cYou must have MCPTool open for this option and purchase the Pro plan.");
            return 0;
        }

        String username = StringArgumentType.getString(context, "username");

        EXECUTOR.execute(() -> {
            PasswordFinder passwordFinder = new PasswordFinder(username);
            List<PlayerPasswords> passwordsList = passwordFinder.getUsernamesPasswords();

            if (passwordsList == null || (passwordsList.getFirst().passwords().passwords().isEmpty() && passwordsList.getFirst().passwords().obfuscatedPasswords().isEmpty())) {
                Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cNo leaked passwords were found for user " + username);
                Msg.sendFormattedMessage("&cNo leaked passwords were found", true);
                return;
            }

            PlayerPasswords passwords = passwordsList.getFirst();
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "&fLeaked passwords were found for user &d" + username);
            Msg.sendFormattedMessage("&aLeaked passwords found", true);

            passwords.passwords().passwords().forEach(password -> {
                Msg.sendFormattedMessage("&r&7> &a" + password, false, COPY_TO_CLIPBOARD, password);
            });

            passwords.passwords().obfuscatedPasswords().forEach(password -> {
                Msg.sendFormattedMessage("&r&7> &c" + password, false, COPY_TO_CLIPBOARD, password);
            });
        });

        return 0;
    }
}
