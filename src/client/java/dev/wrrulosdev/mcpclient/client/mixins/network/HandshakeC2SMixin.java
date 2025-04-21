package dev.wrrulosdev.mcpclient.client.mixins.network;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.spoofing.SessionController;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandshakeC2SPacket.class)
public class HandshakeC2SMixin {
    @Mutable
    @Shadow
    @Final
    private String address;

    /**
     * Handshake C2S packet
     * @param i Integer
     * @param string String
     * @param j Integer
     * @param connectionIntent Connection intent
     * @param ci Callback info
     */
    @Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/packet/c2s/handshake/ConnectionIntent;)V", at = @At("RETURN"))
    private void HandshakeC2SPacket(int i, String string, int j, ConnectionIntent connectionIntent, CallbackInfo ci) {
        if (connectionIntent != ConnectionIntent.LOGIN) return;
        SessionController sessionController = Mcpclient.getSessionController();
        if (!sessionController.isFakeIpEnabled() && !sessionController.isUuidSpoofEnabled()) return;

        if (sessionController.isFakeHostnameEnabled()) {
            this.address = sessionController.getFakeHostname();
        }

        if (sessionController.isFakeIpEnabled()) {
            this.address += "\000" + sessionController.getFakeIp();
        } else {
            this.address += "\000" + "127.0.0.1"; // Default IP
        }

        if (sessionController.isUuidSpoofEnabled()) {
            this.address += "\000" + sessionController.getCurrentUuid();
        } else {
            this.address += "\000" + sessionController.getOriginalUuid(); // Default UUID
        }
    }
}
