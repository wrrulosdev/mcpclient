package dev.wrrulosdev.mcpclient.client.utils.resources;

import net.minecraft.util.Identifier;

public class ResourcesManager {

    /**
     * Retrieves an Identifier for a resource based on the provided path.
     * The resource path is used to locate the resource within the "mcpclient" namespace.
     *
     * @param path The relative path to the resource.
     * @return An Identifier representing the resource located at the specified path.
     */
    public static Identifier getResource(String path) {
        return Identifier.of("mcpclient", path);
    }
}
