package dev.wrrulosdev.mcpclient.client.spoofing;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class SpoofColors {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_ ]{1,16}$";
    private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private final SessionController session;

    public SpoofColors() {
        this.session = Mcpclient.getSessionController();
    }

    /**
     * Returns the style (color) for the username field based on its validity.
     *
     * @param input The input text for the username.
     * @return The Style representing the color of the text.
     */
    public Style getUsernameTextColor(String input) {
        return validatePattern(input);
    }

    /**
     * Returns the style (color) for the UUID field based on its validity and the UUID spoof feature.
     *
     * @param input The input text for the UUID.
     * @return The Style representing the color of the text.
     */
    public Style getUUIDTextColor(String input) {
        return validateFeature(input, session.isUuidSpoofEnabled(), UUID_PATTERN, session::setInvalidUuid);
    }

    /**
     * Returns the style (color) for the Fake IP field based on its validity and the Fake IP spoof feature.
     *
     * @param input The input text for the Fake IP.
     * @return The Style representing the color of the text.
     */
    public Style getFakeIPTextColor(String input) {
        return validateFeature(input, session.isFakeIpEnabled(), ".+", session::setInvalidFakeIp);
    }

    /**
     * Returns the style (color) for the Fake Hostname field based on its validity and the Fake Hostname spoof feature.
     *
     * @param input The input text for the Fake Hostname.
     * @return The Style representing the color of the text.
     */
    public Style getFakeHostnameTextColor(String input) {
        return validateFeature(input, session.isFakeHostnameEnabled(), ".+", session::setInvalidFakeHostname);
    }

    /**
     * Returns the style (color) for the status field (ON/OFF) based on whether the feature is enabled.
     *
     * @param enabled A boolean indicating if the feature is enabled.
     * @return The Style representing the color of the status.
     */
    public Style getStatusTextColor(boolean enabled) {
        return Style.EMPTY.withColor(enabled ? Formatting.GREEN : Formatting.RED);
    }

    /**
     * Validates the input for a feature based on whether it is enabled, using a regex pattern.
     *
     * @param input The input text to validate.
     * @param featureEnabled Whether the feature is enabled.
     * @param regex The regex pattern to match the input against.
     * @param invalidSetter A Consumer that sets the invalid state of the feature if the validation fails.
     * @return The Style representing the color of the input based on validity.
     */
    private Style validateFeature(String input, boolean featureEnabled, String regex, Consumer<Boolean> invalidSetter) {
        if (!featureEnabled) return Style.EMPTY.withColor(Formatting.GRAY); // Gray if the feature is disabled.

        boolean valid = !input.isEmpty() && input.matches(regex);
        invalidSetter.accept(!valid); // Set the invalid state if the input is not valid.

        return Style.EMPTY.withColor(valid ? Formatting.GREEN : Formatting.RED); // Green if valid, red if invalid.
    }

    /**
     * Validates the username input based on a predefined pattern.
     *
     * @param input The input text for the username.
     * @return The Style representing the color of the text based on validity.
     */
    private Style validatePattern(String input) {
        boolean valid = input.matches(SpoofColors.USERNAME_PATTERN);
        return Style.EMPTY.withColor(valid ? Formatting.GREEN : Formatting.RED); // Green if valid, red if invalid.
    }
}
