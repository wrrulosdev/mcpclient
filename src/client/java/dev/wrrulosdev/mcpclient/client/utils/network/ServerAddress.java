package dev.wrrulosdev.mcpclient.client.utils.network;

import net.minecraft.client.MinecraftClient;
import java.util.Objects;

public class ServerAddress {

    private final String domain;
    private final String ip;
    private final String port;

    /**
     * Constructs a ServerAddress object based on the current Minecraft client's server connection details.
     * This constructor extracts the server address and splits it into IP and port, handling both domain-based
     * and IP-based server addresses.
     *
     * @param client The MinecraftClient instance used to retrieve server connection details.
     */
    public ServerAddress(MinecraftClient client) {
        String address = Objects.requireNonNull(client.getCurrentServerEntry()).address;

        if (client.getNetworkHandler() != null && client.getNetworkHandler().getConnection() != null) {
            address = client.getNetworkHandler().getConnection().getAddressAsString(true);

            if (address.contains("/")) {
                address = address.split("/")[1];
            }
        }

        String[] addressParts = address.split(":");
        this.ip = addressParts[0];
        this.port = addressParts.length > 1 ? addressParts[1] : "25565";
        this.domain = client.getCurrentServerEntry().address;
    }

    /**
     * Retrieves the domain (full address) of the server.
     *
     * @return The domain (server address) as a string.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Retrieves the IP address of the server.
     *
     * @return The IP address of the server as a string.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Retrieves the port number of the server.
     *
     * @return The port number of the server as a string.
     */
    public String getPort() {
        return port;
    }

    /**
     * Checks whether two ServerAddress objects are equal based on their IP address and port number.
     *
     * @param o The object to compare this instance with.
     * @return true if the given object is equal to this ServerAddress, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerAddress that = (ServerAddress) o;
        return Objects.equals(ip, that.ip) && Objects.equals(port, that.port);
    }

    /**
     * Generates a hash code for this ServerAddress based on its IP and port.
     *
     * @return A hash code representing this ServerAddress.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
