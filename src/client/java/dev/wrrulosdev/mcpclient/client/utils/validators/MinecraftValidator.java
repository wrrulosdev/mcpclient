package dev.wrrulosdev.mcpclient.client.utils.validators;

import java.util.regex.Pattern;

public class MinecraftValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    /**
     * Validates if the provided username is a valid Minecraft username.
     *
     * @param username The username to validate.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean isValidMinecraftUsername(String username) {
        if (username == null) {
            return false;
        }

        return USERNAME_PATTERN.matcher(username).matches();
    }
}
