package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import java.util.Collection;

import static dev.wrrulosdev.mcpclient.client.utils.messages.Msg.ClickAction.COPY_TO_CLIPBOARD;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayersCommand implements Command {

    /**
     * Registers the "players" command.
     *
     * @return A LiteralArgumentBuilder that configures the "players" command and binds it to the executeUuidAll method.
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("players")
                .executes(this::executeUuidAll);
    }

    /**
     * Executes the "players" command to display information about all connected players.
     *
     * @param context The command context containing information about the source executing the command.
     * @return The result code of the command execution (0 for success, 1 for failure).
     */
    private int executeUuidAll(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            Msg.sendFormattedMessage("&cThis command only works on servers.", false);
            return 1;
        }

        ClientPlayNetworkHandler handler = client.getNetworkHandler();

        if (handler == null) {
            Msg.sendFormattedMessage("&cPlayers not found on the server.", false);
            return 1;
        }

        Collection<PlayerListEntry> players = handler.getPlayerList();

        if (players.isEmpty()) { 
            Msg.sendFormattedMessage("&cPlayers not found on the server.", false);
            return 1;
        }

        Msg.sendFormattedMessage("&7Information about connected players: \n", false);
        players.forEach((PlayerListEntry player) -> {
            Msg.sendFormattedMessage(
                    "&r&7» &b" + player.getProfile().getName() + 
                    " &8[&a" + player.getProfile().getId().toString() + "&8]", 
                    false, 
                    COPY_TO_CLIPBOARD, 
                    player.getProfile().getId().toString()
            );
        });

        return 0;
    }
}
