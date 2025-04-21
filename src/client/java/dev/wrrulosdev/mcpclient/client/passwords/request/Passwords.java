package dev.wrrulosdev.mcpclient.client.passwords.request;

import java.util.List;

public record Passwords(List<String> passwords, List<String> obfuscatedPasswords) {
}
