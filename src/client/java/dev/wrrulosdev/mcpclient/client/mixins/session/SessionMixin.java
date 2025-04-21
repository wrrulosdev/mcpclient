package dev.wrrulosdev.mcpclient.client.mixins.session;

import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Session.class)
public interface SessionMixin {

    /**
     * Sets the username in the Session class.
     * This method provides access to modify the private "username" field.
     *
     * @param username The new username to be set in the session.
     */
    @Mutable
    @Accessor("username")
    void setUsername(String username);

    /**
     * Retrieves the username from the Session class.
     * This method provides access to the private "username" field.
     *
     * @return The current username stored in the session.
     */
    @Accessor("username")
    String getUsername();
}
