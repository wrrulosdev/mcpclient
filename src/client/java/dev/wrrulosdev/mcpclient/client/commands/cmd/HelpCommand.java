package dev.wrrulosdev.mcpclient.client.commands.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.wrrulosdev.mcpclient.client.commands.Command;
import dev.wrrulosdev.mcpclient.client.constants.ClientConstants;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import dev.wrrulosdev.mcpclient.client.utils.messages.Msg;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HelpCommand implements Command {

    private final  String HELP_MESSAGE = "&7&l-------------------------\n" +
        ClientConstants.PREFIX + "&7- &fHelp\n" +
        "&7&l-------------------------\n" +
        "&7- &b/mcp authmevelocity &7- &fSend the payload to force authentication to the AuthMe Velocity plugin\n" +
        "&7- &b/mcp cloudsync <username> <command> &7- &fSubmit the payload to run a command in the console using the Cloud Sync plugin.\n" +
        "&7- &b/mcp easycommandblocker <consoleCommand> &7- &fSubmit the payload to run a command in the console using the Easy Command Blocker plugin.\n" +
        "&7- &b/mcp signedvelocity <uuid> <command> &7- &fSend payload to handle a user's next command using the Signed Velocity plugin.\n" +
        "&7- &b/mcp multichat <consoleCommand> &7- &fSubmit the payload to run a command in the proxy using the MultiChat plugin.\n" +
        "&7- &b/mcp t2copsecurity <consoleCommand> &7- &fSubmit the payload to run a command in the console using the T2CodeLib plugin.\n" +
        "&7- &b/mcp plugins &7- &fTry to get the plugins from the server using the tab autocomplete.\n" +
        "&7- &b/mcp hclip <distance> &7- &fTeleport horizontally by the specified distance.\n" +
        "&7- &b/mcp vclip <distance> &7- &fTeleport vertically by the specified distance.\n" +
        "&7- &b/mcp password <username> &7- &fDisplays all users with leaked passwords from the server. &8(&cMCPTool PRO plan only&8)\n" +
        "&7- &b/mcp password <username> &7- &fView a user's account passwords. &8(&cMCPTool PRO plan only&8)\n" +
        "&7&l-------------------------";

    /**
     * Registers the help command with the client command system.
     *
     * @return a LiteralArgumentBuilder used to register the command.
     */
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal("help")
            .executes(this::executeHelp);
    }

    /**
     * Executes the "help" command and sends the help menu to the user.
     * This method is called when the user types "/help" in the client chat.
     *
     * @param context the command context containing information about the command execution
     * @return an integer representing the success status of the command execution.
     */
    private int executeHelp(CommandContext<FabricClientCommandSource> context) {
        sendHelpMenu(context.getSource());
        return 1;
    }

    /**
     * Sends a formatted help menu message to the player using the specified source.
     * The help menu is displayed with the formatted message highlighting the MCP Client's help.
     *
     * @param source the source from which the command was issued, used to send the message
     */
    private void sendHelpMenu(FabricClientCommandSource source) {
        Msg.sendFormattedMessage(HELP_MESSAGE);
    }
}
