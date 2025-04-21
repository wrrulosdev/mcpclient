package dev.wrrulosdev.mcpclient.client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface Command {

    LiteralArgumentBuilder<FabricClientCommandSource> register();
}