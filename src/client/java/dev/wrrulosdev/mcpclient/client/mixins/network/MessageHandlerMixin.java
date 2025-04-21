package dev.wrrulosdev.mcpclient.client.mixins.network;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.settings.LuckPermsEditorSettings;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {

    /**
     * Redirects the call to {@link ChatHud#addMessage(Text)} during {@code onGameMessage}
     * to inspect and optionally extract LuckPerms group names from the chat message.
     *
     * @param instance The chat HUD instance where the message would be added.
     * @param text     The chat message to display.
     */
    @Redirect(method = "onGameMessage(Lnet/minecraft/text/Text;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    private void onGameMessage(ChatHud instance, Text text) {
        String message = text.getString();

        if (Mcpclient.getSettings().getLuckPermsEditorSettings().isEnabled() && message.startsWith("[LP] - ")) {
            String groupName = LuckPermsEditorSettings.extractGroupName(message);

            if (groupName != null && !groupName.isEmpty()) {
                Mcpclient.getSettings().getLuckPermsEditorSettings().addRank(groupName);
            }
        }

        instance.addMessage(text);
    }
}