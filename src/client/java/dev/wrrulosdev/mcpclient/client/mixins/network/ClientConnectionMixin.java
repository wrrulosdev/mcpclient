package dev.wrrulosdev.mcpclient.client.mixins.network;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.plugins.PluginTabStorage;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    /*
     * Intercept packets received by the client connection.
     *
     * @param channelHandlerContext The channel handler context
     * @param packet The packet received
     * @param ci The callback info
     */
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void channelRead0Head(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        String packetType = packet.getClass().getSimpleName();
        PluginTabStorage pluginTabStorage = Mcpclient.getPluginTabStorage();

        // Intercept command suggestions packet if listening for plugin tab completions is enabled
        if (packetType.equals("CommandSuggestionsS2CPacket") && pluginTabStorage.getListening()) {
            CommandSuggestionsS2CPacket commandSuggestionsS2CPacket = (CommandSuggestionsS2CPacket) packet;
            Suggestions suggestions = commandSuggestionsS2CPacket.getSuggestions();

            for (Suggestion suggestion : suggestions.getList()) {
                pluginTabStorage.addPluginName(suggestion.getText());
            }
        }
    }
}