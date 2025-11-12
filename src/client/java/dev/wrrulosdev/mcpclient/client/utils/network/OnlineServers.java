package dev.wrrulosdev.mcpclient.client.utils.network;

import java.util.Collections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.option.ServerList;

public class OnlineServers {

    /**
     * Connects to a Minecraft server at the specified address.
     *
     * <p>This method schedules the connection on the Minecraft client thread. It creates
     * a ServerAddress and ServerInfo for the given address and initiates the connection
     * using an empty CookieStorage.</p>
     *
     * @param address the server address string (host[:port]) to connect to
     * @param client the MinecraftClient instance used to perform the connection
     */
    public static void connect(String address, MinecraftClient client) {
        client.execute(() -> {
            try {
                ServerAddress serverAddress = ServerAddress.parse(address);
                ServerInfo serverInfo = new ServerInfo("API Server", address, ServerType.OTHER);
                CookieStorage cookieStorage = new CookieStorage(Collections.emptyMap());
                ConnectScreen.connect(
                    client.currentScreen,
                    client, 
                    serverAddress, 
                    serverInfo, 
                    false,
                    cookieStorage
                );
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Adds a server entry to the client's server list if it does not already exist.
     *
     * <p>This method schedules the addition on the Minecraft client thread. It loads the
     * current server list, checks if the server already exists by address, and if not,
     * adds a new ServerInfo entry and saves the list.</p>
     *
     * @param address the server address string (host[:port]) to add
     * @param client the MinecraftClient instance used to access the server list
     */
    public static void addServer(String address, MinecraftClient client) {
        client.execute(() -> {
            try {
                ServerList serverList = new ServerList(client);
                serverList.loadFile();

                boolean serverExists = serverList.get(address) != null;
                if (serverExists) return;

                ServerInfo newServer = new ServerInfo("Server from MCPTool", address, ServerType.OTHER);
                serverList.add(newServer, false);
                serverList.saveFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
     }
}
