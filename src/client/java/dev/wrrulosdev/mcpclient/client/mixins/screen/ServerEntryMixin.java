package dev.wrrulosdev.mcpclient.client.mixins.screen;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ServerInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class ServerEntryMixin {

    @Unique
    private MinecraftClient client = Mcpclient.getMinecraftClient();

    @Unique
    private int textX, textY;

    /**
     * Injects custom rendering logic into the "render" method of the ServerEntry class
     * to display additional server information (version and protocol).
     *
     * @param context The DrawContext instance used for drawing elements to the screen.
     * @param index The index of the entry being rendered in the list.
     * @param y The Y coordinate of the entry's position on the screen.
     * @param x The X coordinate of the entry's position on the screen.
     * @param entryWidth The width of the entry.
     * @param entryHeight The height of the entry.
     * @param mouseX The X coordinate of the mouse cursor.
     * @param mouseY The Y coordinate of the mouse cursor.
     * @param hovered Indicates whether the entry is hovered by the mouse.
     * @param tickDelta The time delta since the last frame.
     * @param ci The CallbackInfo instance to control the flow of the method.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        textX = x + entryWidth + 20;
        textY = y + (entryHeight / 2) - 17;
        ServerInfo server = ((MultiplayerServerListWidget.ServerEntry) (Object) this).getServer();
        String version = server.version.getString();
        String protocol = String.valueOf(server.protocolVersion);
        context.drawText(client.textRenderer, version, textX, textY, 0xFFFFAAAA, true);
        textY += 15;
        context.drawText(client.textRenderer, "Protocol: " + protocol, textX, textY, 0xFFFFAAAA, true);
    }
}
