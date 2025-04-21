package dev.wrrulosdev.mcpclient.client.spoofing;

import dev.wrrulosdev.mcpclient.client.mixins.session.SessionMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;


public class SessionController {

    private static final String DEFAULT_UUID = "00000000-0000-0000-0000-000000000000";
    private final Session session;
    private boolean uuidSpoofEnabled = false;
    private boolean fakeIpEnabled = false;
    private boolean fakeHostnameEnabled = false;
    private String currentUuid = "";
    private String originalUuid = "";
    private String fakeIp = "1.3.3.7";
    private String fakeHostname = "0.0.0.0";
    private boolean invalidUuid = false;
    private boolean invalidFakeIp = false;
    private boolean invalidFakeHostname = false;

    /**
     * Initializes a new session controller with default values.
     */
    public SessionController() {
        this.session = MinecraftClient.getInstance().getSession();
    }

    /**
     * Toggles multiple spoofing states simultaneously.
     * @param uuid Whether to spoof UUID
     * @param ip Whether to spoof IP
     * @param hostname Whether to spoof hostname
     */
    public void toggleSpoofState(boolean uuid, boolean ip, boolean hostname) {
        this.uuidSpoofEnabled = uuid;
        this.fakeIpEnabled = ip;
        this.fakeHostnameEnabled = hostname;
    }

    /**
     * Manages UUID storage for original/spoofed states.
     * @param newUuid UUID to store
     * @param isOriginal Whether to update original UUID
     */
    public void manageUuid(String newUuid, boolean isOriginal) {
        if (isOriginal) {
            this.originalUuid = newUuid;
        } else {
            this.currentUuid = newUuid;
        }
    }

    /**
     * Gets the effective UUID based on current spoof state.
     * @return Active UUID value
     */
    public String resolveEffectiveUuid() {
        return uuidSpoofEnabled ? currentUuid : getOriginalUuid();
    }

    /**
     * @return Original session UUID
     */
    public String getOriginalUuid() {
        return session != null ? session.getUuidOrNull().toString() : DEFAULT_UUID;
    }

    /**
     * Updates session properties with new values.
     * @param username New username
     * @param uuid New UUID
     */
    public void updateSessionProperty(String username, String uuid) {
        SessionMixin mixinSession = (SessionMixin) session;
        mixinSession.setUsername(username);
        manageUuid(uuid, false);
    }

    // Validation and state checks
    public boolean hasValidationErrors() {
        return (uuidSpoofEnabled && invalidUuid) ||
            (fakeIpEnabled && invalidFakeIp) ||
            (fakeHostnameEnabled && invalidFakeHostname);
    }

    // Accessors and mutators
    public boolean isUuidSpoofEnabled() { return uuidSpoofEnabled; }
    public boolean isFakeIpEnabled() { return fakeIpEnabled; }
    public boolean isFakeHostnameEnabled() { return fakeHostnameEnabled; }
    public String getCurrentUuid() { return currentUuid; }
    public String getFakeIp() { return fakeIp; }
    public void setFakeIp(String ip) { this.fakeIp = ip; }
    public String getFakeHostname() { return fakeHostname; }
    public void setFakeHostname(String hostname) { this.fakeHostname = hostname; }
    public boolean isInvalidUuid() { return invalidUuid; }
    public void setInvalidUuid(boolean invalid) { this.invalidUuid = invalid; }
    public boolean isInvalidFakeIp() { return invalidFakeIp; }
    public void setInvalidFakeIp(boolean invalid) { this.invalidFakeIp = invalid; }
    public boolean isInvalidFakeHostname() { return invalidFakeHostname; }
    public void setInvalidFakeHostname(boolean invalid) { this.invalidFakeHostname = invalid; }
    public String getUsername() { return session != null ? session.getUsername() : "Player"; }
}