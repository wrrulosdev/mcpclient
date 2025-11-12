package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class VClipCommand implements Command {

    /**
     * Registers the vclip command.
     *
     * @return the builder for the command registration
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("vclip")
            .executes(this::executeRoot)
            .then(argument("distance", DoubleArgumentType.doubleArg())
                .executes(this::executeVClip)
            );
    }

    /**
     * Handles the case where no arguments are provided.
     *
     * @param context The command context.
     * @return Always returns 0 (success).
     */
    private int executeRoot(CommandContext<FabricClientCommandSource> context) {
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cUsage: /mcp vclip <distance>");
        return 0;
    }

    /**
     * Executes the vclip command, teleporting the player vertically by the specified distance.
     *
     * @param context the context of the command, containing the arguments and the command source
     * @return the status code (1 for success)
     */
    private int executeVClip(CommandContext<FabricClientCommandSource> context) {
        double distance = DoubleArgumentType.getDouble(context, "distance");
        ClientPlayerEntity player = context.getSource().getPlayer();

        if (player == null) return 0;
        
        player.setPosition(player.getX(), player.getY() + distance, player.getZ());
        String direction = distance >= 0 ? "up" : "down";
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&aTeleported &d" + Math.abs(distance) + " &ablocks &d" + direction);
        return 1;
    }
}
