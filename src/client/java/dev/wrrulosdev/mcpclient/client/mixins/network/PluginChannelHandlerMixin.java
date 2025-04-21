package dev.wrrulosdev.mcpclient.client.mixins.network;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.plugins.PluginChannelStorage;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.impl.networking.RegistrationPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(RegistrationPayload.class)
public class PluginChannelHandlerMixin {

    /**
     * Intercepts the method responsible for registering custom plugin message channels.
     *
     * @param ids The list of registered channel identifiers.
     * @param sb  The StringBuilder containing the name of the channel being registered.
     * @param ci  Callback information for mixin injection handling.
     */
    @Inject(method = "addId", at = @At(value = "HEAD"), remap = false)
    private static void onChannelRegistration(List<Identifier> ids, StringBuilder sb, CallbackInfo ci) {
        String channel = sb.toString();
        System.out.println("Channel founded: " + channel);

        if (!channel.isEmpty()) {
            System.out.println("[LOG] Registered plugin message channel: " + channel);
            PluginChannelStorage pluginChannelStorage = Mcpclient.getPluginChannelStorage();
            pluginChannelStorage.addPluginMessage(channel);
        }
    }
}
