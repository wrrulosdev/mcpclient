package dev.wrrulosdev.mcpclient.client.mixins.network;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.passwords.request.PlayerPasswords;
import dev.wrrulosdev.mcpclient.client.utils.messages.CC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Collection;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    /**
     * Mixin method for rendering the player list.
     * <p>
     * @param context The draw context.
     * @param scaledWindowWidth The scaled window width.
     * @param scoreboard The scoreboard.
     * @param objective The scoreboard objective.
     * @param ci Callback information for mixin injection handling.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (!Mcpclient.getSettings().getPasswordTrackerSettings().isCustomTabList()) return;
        MinecraftClient client = Mcpclient.getMinecraftClient();

        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        Collection<PlayerListEntry> playerListEntries = client.getNetworkHandler().getPlayerList();

        for (PlayerListEntry entry : playerListEntries) {
            PlayerPasswords passwords = Mcpclient.getPlayerTracker().getPasswordsForPlayer(entry.getProfile().getName());
            boolean hasClean = !passwords.passwords().passwords().isEmpty();
            boolean hasObfuscated = !passwords.passwords().obfuscatedPasswords().isEmpty();

            if (hasClean || hasObfuscated) {
                String leakedText = "&cLEAKED &r" + entry.getProfile().getName();
                MutableText vulnerableText = CC.parseColorCodes(leakedText);
                entry.setDisplayName(vulnerableText);
            } else {
                entry.setDisplayName(Text.of(entry.getProfile().getName()));
            }
        }
    }
}
