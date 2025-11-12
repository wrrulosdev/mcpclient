package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class HClipCommand implements Command {

    /**
     * Registers the hclip command.
     *
     * @return the builder for the command registration
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("hclip")
            .executes(this::executeRoot)
            .then(argument("distance", DoubleArgumentType.doubleArg())
                .executes(this::executeHClip)
            );
    }

    /**
     * Handles the case where no arguments are provided.
     *
     * @param context The command context.
     * @return Always returns 0 (success).
     */
    private int executeRoot(CommandContext<FabricClientCommandSource> context) {
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&cUsage: /mcp hclip <distance>");
        return 0;
    }

    /**
     * Executes the hclip command, teleporting the player horizontally by the specified distance.
     * The direction of movement is based on the player's yaw (rotation).
     *
     * @param context the context of the command, containing the arguments and the command source
     * @return the status code (1 for success)
     */
    private int executeHClip(CommandContext<FabricClientCommandSource> context) {
        double distance = DoubleArgumentType.getDouble(context, "distance");
        ClientPlayerEntity player = context.getSource().getPlayer();
        
        if (player == null) return 0;

        float yaw = player.getYaw();
        double yawRad = Math.toRadians(yaw);
        double deltaX = -Math.sin(yawRad) * distance;
        double deltaZ = Math.cos(yawRad) * distance;

        player.setPosition(player.getX() - deltaZ, player.getY(), player.getZ() + deltaX);
        String direction = distance >= 0 ? "right" : "left";
        Msg.sendFormattedMessage(ClientConstants.PREFIX + "&aTeleported &d" + Math.abs(distance) + " &ablocks &d" + direction);
        return 1;
    }
}
