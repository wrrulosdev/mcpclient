package dev.wrrulosdev.mcpclient.client.commands.cmd;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.plugins.PluginTabStorage;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PluginsCommand implements Command {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Registers the "plugins" command.
     * This method sets up the "plugins" command to be executed when triggered by the user.
     *
     * @return A LiteralArgumentBuilder that configures the "plugins" command.
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("plugins")
            .executes(this::executePlugin);
    }

    /**
     * Executes the "plugins" command logic.
     * This method is triggered when the "plugins" command is run by the user.
     * It informs the user that the plugin information is being requested and sends the payload.
     *
     * @param context The command context, containing information about the player running the command.
     * @return The result of the command execution (0 indicates success).
     */
    private int executePlugin(CommandContext<FabricClientCommandSource> context) {
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&fSending payloads to try to view server plugins via TAB autocomplete...");
        PluginTabStorage pluginTabStorage = Mcpclient.getPluginTabStorage();
        pluginTabStorage.setListening(true);
        executor.submit(() -> requestPluginCompletions(pluginTabStorage));
        return 0;
    }

    /**
     * Requests command completions for a set of plugin-related commands.
     * This method runs in a separate thread to avoid blocking the main thread.
     *
     * @param pluginTabStorage The PluginTabStorage instance to store and send plugin names.
     */
    private static void requestPluginCompletions(PluginTabStorage pluginTabStorage) {
        try {
            List<String> commands = Arrays.asList("/about ", "/pl ", "/plugins ", "/ver ", "/version ");

            for (String command : commands) {
                sendCompletionRequest(command);
                Thread.sleep(50);
            }

            pluginTabStorage.setListening(false);
            pluginTabStorage.sendStoredPluginNames();

        } catch (InterruptedException e) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "The request for plugin names was interrupted.");
            System.err.println("Error during plugin request: " + e.getMessage());
        } catch (Exception e) {
            Msg.sendFormattedMessage(ClientConstants.PREFIX + "An error occurred while requesting command completions.");
            System.err.println("Error occurred while requesting command completions: " + e.getMessage());
        }
    }

    /**
     * Sends a request for command completions for a specific command.
     * This method requests the server to complete a command, which may include a list of available plugins.
     *
     * @param command The command for which to request completions.
     */
    private static void sendCompletionRequest(String command) {
        try {
            System.out.println("Sending packet for command: " + command);
            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new RequestCommandCompletionsC2SPacket(0, command));
        } catch (Exception e) {
            System.err.println("Failed to send completion request for command: " + command + ". Error: " + e.getMessage());
        }
    }
}
