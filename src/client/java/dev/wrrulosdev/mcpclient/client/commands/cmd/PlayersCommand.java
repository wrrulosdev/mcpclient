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
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("players")
                .executes(this::executeUuidAll);
    }

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

        if (players.isEmpty()) { // This should NOT happen
            Msg.sendFormattedMessage("&cPlayers not found on the server.", false);
            return 1;
        }

        Msg.sendFormattedMessage("&7Information about connected players: \n", false);

        players.forEach(player -> {
            Msg.sendFormattedMessage("&r&7» &b" + player.getProfile().getName() + " &8[&a" + player.getProfile().getId().toString() + "&8]", false, COPY_TO_CLIPBOARD, player.getProfile().getId().toString());
        });

        return 0;
    }
}
