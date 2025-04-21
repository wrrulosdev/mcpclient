package dev.wrrulosdev.mcpclient.client.payloads;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadRegistry {

    /**
     * Registers all payloads from the client to the server (C2S).
     */
    private static void registerC2SPayloads() {
        PayloadTypeRegistry.playC2S().register(AuthMeVelocityPayloadPacket.ID, AuthMeVelocityPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(MultiChatPayloadPacket.ID, MultiChatPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(EasyCommandBlockerPayloadPacket.ID, EasyCommandBlockerPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(CloudSyncPayloadPacket.ID, CloudSyncPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SignedVelocityPayloadPacket.ID, SignedVelocityPayloadPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(T2cOpSecurityPayloadPacket.ID, T2cOpSecurityPayloadPacket.CODEC);
    }

    /**
     * Registers all payloads from the client to the server (C2S) and from the server to the client (S2C).
     */
    public static void registerAllPayloads() {
        registerC2SPayloads();
    }
}
