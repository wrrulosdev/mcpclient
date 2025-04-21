package dev.wrrulosdev.mcpclient.client.hudrender;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.utils.messages.CC;
import dev.wrrulosdev.mcpclient.client.utils.network.ServerAddress;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class HudRenderer implements HudRenderCallback {

    /**
     * Render the HUD on the screen
     * @param drawContext Draw context
     * @param tickCounter Render tick counter
     */
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;

        if (client.getCurrentServerEntry() == null || client.getNetworkHandler() == null || client.getNetworkHandler().getConnection() == null || client.player == null) {
            return;
        }

        ServerAddress serverAddress = new ServerAddress(client);
        String address = serverAddress.getIp();
        String port = serverAddress.getPort();
        String version = client.getCurrentServerEntry().version.getString();
        int protocol = client.getCurrentServerEntry().protocolVersion;
        int playerCount = client.player.networkHandler.getPlayerList().size();
        int fps = client.getCurrentFps();

        // Create the text components
        Text serverIpText = CC.parseColorCodes("&cServer: &a" + address + ":" + port);
        Text versionText = CC.parseColorCodes("&cVersion: &f" + version);
        Text protocolText = CC.parseColorCodes("&cProtocol: &f" + protocol);
        Text playersText = CC.parseColorCodes("&cPlayers: &f" + playerCount);
        Text fpsText = CC.parseColorCodes("&cFPS: &f" + fps);
        Text mcptoolApiText = CC.parseColorCodes(
            "&cMCPTool API: &f" + (Mcpclient.isMCPToolAPIEnabled() ? "&aON" : "&4OFF")
        );

        // Render the text components on the screen
        drawContext.drawText(renderer, serverIpText, 10, 10, 0xFFFFFF, true);
        drawContext.drawText(renderer, versionText, 10, 25, 0xFFFFFF, true);
        drawContext.drawText(renderer, protocolText, 10, 40, 0xFFFFFF, true);
        drawContext.drawText(renderer, playersText, 10, 55, 0xFFFFFF, true);
        drawContext.drawText(renderer, fpsText, 10, 70, 0xFFFFFF, true);
        drawContext.drawText(renderer, mcptoolApiText, 10, 85, 0xFFFFFF, true);
    }
}
