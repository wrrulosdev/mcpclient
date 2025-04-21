package dev.wrrulosdev.mcpclient.client.mixins.screen;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.screens.CustomButton;
import dev.wrrulosdev.mcpclient.client.screens.SpoofSectionScreen;
import dev.wrrulosdev.mcpclient.client.utils.resources.ResourcesManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Shadow
    private boolean initialized;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    private ServerList serverList;

    @Shadow
    private LanServerQueryManager.LanServerEntryList lanServers;

    @Shadow
    @Nullable
    private LanServerQueryManager.LanServerDetector lanServerDetector;

    @Shadow
    private ButtonWidget buttonJoin;

    @Shadow
    private ServerInfo selectedEntry;

    @Shadow
    private ButtonWidget buttonEdit;

    @Shadow
    private ButtonWidget buttonDelete;

    @Shadow
    protected abstract void directConnect(boolean confirmedAction);

    @Shadow
    public abstract void connect();

    @Shadow
    protected abstract void addEntry(boolean confirmedAction);

    @Shadow
    protected abstract void editEntry(boolean confirmedAction);

    @Shadow
    protected abstract void removeEntry(boolean confirmedAction);

    @Shadow
    protected abstract void updateButtonActivationStates();

    @Shadow
    protected abstract void refresh();

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    /**
     * Initializes the multiplayer screen. This includes loading the server list, starting
     * LAN server detection, creating the server list widget, and adding buttons for interaction
     * (e.g., join, add, edit, delete).
     * <p>
     * This method overwrites the original {@code init()} method in {@link MultiplayerScreen}.
     * @author wRRulosDEV
     * @reason New Multiplayer menu
     */
    @Overwrite
    public void init() {
        if (this.initialized) {
            this.serverListWidget.setDimensionsAndPosition(this.width, this.height - 64 - 32, 0, 32);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            this.serverList.loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();

            try {
                this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
                this.lanServerDetector.start();
            } catch (Exception exception) {
                System.out.println("Unable to start LAN server detection: " + exception.getMessage());
            }

            this.serverListWidget = new MultiplayerServerListWidget((MultiplayerScreen) Mcpclient.getMinecraftClient().currentScreen, this.client, this.width, this.height - 64 - 32, 32, 36);
            this.serverListWidget.setServers(this.serverList);
        }

        this.addDrawableChild(this.serverListWidget);
        this.buttonJoin = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.select"), (button) -> this.connect()).width(0).build());
        ButtonWidget buttonWidget = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.direct"), (button) -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", ServerInfo.ServerType.OTHER);
            this.client.setScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
        }).width(0).build());
        ButtonWidget buttonWidget2 = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.add"), (button) -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", ServerInfo.ServerType.OTHER);
            this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
        }).width(0).build());
        this.buttonEdit = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.edit"), (button) -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, ServerInfo.ServerType.OTHER);
                this.selectedEntry.copyWithSettingsFrom(serverInfo);
                this.client.setScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
            }

        }).width(0).build());
        this.buttonDelete = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.delete"), (button) -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                String string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name;
                if (string != null) {
                    Text text = Text.translatable("selectServer.deleteQuestion");
                    Text text2 = Text.translatable("selectServer.deleteWarning", new Object[]{string});
                    Text text3 = Text.translatable("selectServer.deleteButton");
                    Text text4 = ScreenTexts.CANCEL;
                    this.client.setScreen(new ConfirmScreen(this::removeEntry, text, text2, text3, text4));
                }
            }

        }).width(0).build());
        this.updateButtonActivationStates();
    }

    /**
     * Renders the screen, including the title and various interactive buttons represented by icons.
     * Buttons include: delete, edit, refresh, join, add server, and spoof settings.
     * Custom textures are loaded depending on the button hover state and the currently selected server.
     *
     * @param context the drawing context used for rendering GUI elements
     * @param mouseX the X coordinate of the mouse pointer
     * @param mouseY the Y coordinate of the mouse pointer
     * @param delta the partial tick time used for animation/frame updates
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int textureWidth = 35;
        int textureHeight = 30;

        // Delete Button
        int deleteX = (screenWidth - 35) / 2 - 130;
        int deleteY = screenHeight - 50;
        String deleteTexture;
        MultiplayerServerListWidget.Entry currentEntry = this.serverListWidget.getSelectedOrNull();

        if (currentEntry == null) {
            deleteTexture = "textures/gui/icons/delete_before.png";
        } else {
            boolean isHovered = mouseX >= deleteX && mouseX <= deleteX + (textureWidth - 2.5) && mouseY >= deleteY && mouseY <= deleteY + (textureHeight - 2.5);
            deleteTexture = isHovered ? "textures/gui/icons/delete_hover.png" : "textures/gui/icons/delete.png";
        }

        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(deleteTexture),
            deleteX,
            deleteY,
            1.0F,
            1.0F,
            35,
            30,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(deleteX, deleteY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> {
                    MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
                    if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                        String serverName = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name;
                        if (serverName != null) {
                            Text title = Text.translatable("selectServer.deleteQuestion");
                            Text message = Text.translatable("selectServer.deleteWarning", serverName);
                            Text confirm = Text.translatable("selectServer.deleteButton");
                            this.client.setScreen(new ConfirmScreen(
                                this::removeEntry,
                                title,
                                message,
                                confirm,
                                ScreenTexts.CANCEL
                            ));
                        }
                    }
                })
                .tooltip(Tooltip.of(Text.literal("Click to delete")))
                .build()
        );

        // Edit Button
        int editX = (screenWidth - 35) / 2 - 80;
        int editY = screenHeight - 50;
        boolean isEditHovered = mouseX >= editX && mouseX <= editX + (textureWidth - 2.5) && mouseY >= editY && mouseY <= editY + (textureHeight - 2.5);
        String editTexture;

        if (currentEntry == null) {
            editTexture = "textures/gui/icons/edit_before.png";
        } else {
            editTexture =  isEditHovered ? "textures/gui/icons/edit_hover.png" : "textures/gui/icons/edit.png";
        }

        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(editTexture),
            editX,
            editY,
            1.0F,
            1.0F,
            33,
            29,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(editX, editY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> {
                    MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
                    if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                        ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                        this.selectedEntry = new ServerInfo(
                            serverInfo.name,
                            serverInfo.address,
                            ServerInfo.ServerType.OTHER
                        );
                        this.selectedEntry.copyWithSettingsFrom(serverInfo);
                        this.client.setScreen(new AddServerScreen(
                            this,
                            this::editEntry,
                            this.selectedEntry
                        ));
                    }
                })
                .tooltip(Tooltip.of(Text.literal("Click to edit")))
                .build()
        );

        // Sync Button
        int syncX = (screenWidth - 35) / 2 - 30;
        int syncY = screenHeight - 50;
        boolean isSyncHovered = mouseX >= syncX && mouseX <= syncX + (textureWidth - 2.5) && mouseY >= syncY && mouseY <= syncY + (textureHeight - 2.5);
        String syncTexture = isSyncHovered ? "textures/gui/icons/sync_hover.png" : "textures/gui/icons/sync.png";
        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(syncTexture),
            syncX,
            syncY,
            1.0F,
            1.0F,
            33,
            29,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(syncX, syncY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> this.refresh())
                .tooltip(Tooltip.of(Text.literal("Refresh server list!")))
                .build()
        );

        // Join Button
        int joinX = (screenWidth - 35) / 2 + 20;
        int joinY = screenHeight - 50;
        boolean isJoinHovered = mouseX >= joinX && mouseX <= joinX + (textureWidth - 2.5) && mouseY >= joinY && mouseY <= joinY + (textureHeight - 2.5);
        String joinTexture = isJoinHovered ? "textures/gui/icons/join_hover.png" : "textures/gui/icons/join.png";
        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(joinTexture),
            joinX,
            joinY,
            1.0F,
            1.0F,
            33,
            29,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(joinX, joinY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> {
                    this.selectedEntry = new ServerInfo(
                        I18n.translate("selectServer.defaultName"),
                        "",
                        ServerInfo.ServerType.OTHER
                    );
                    this.client.setScreen(new DirectConnectScreen(
                        this,
                        this::directConnect,
                        this.selectedEntry
                    ));
                })
                .tooltip(Tooltip.of(Text.literal("Direct connect to server")))
                .build()
        );

        // Add Button
        int addX = (screenWidth - 35) / 2 + 70;
        int addY = screenHeight - 50;
        boolean isAddHovered = mouseX >= addX && mouseX <= addX + (textureWidth - 2.5) && mouseY >= addY && mouseY <= addY + (textureHeight - 2.5);
        String addTexture = isAddHovered ? "textures/gui/icons/add_hover.png" : "textures/gui/icons/add.png";
        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(addTexture),
            addX,
            addY,
            1.0F,
            1.0F,
            33,
            29,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(addX, addY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> {
                    this.selectedEntry = new ServerInfo(
                        I18n.translate("selectServer.defaultName"),
                        "",
                        ServerInfo.ServerType.OTHER
                    );
                    this.client.setScreen(new AddServerScreen(
                        this,
                        this::addEntry,
                        this.selectedEntry
                    ));
                })
                .tooltip(Tooltip.of(Text.literal("Add server")))
                .build()
        );

        // Settings Button
        int settingsX = (screenWidth - 35) / 2 + 120;
        int settingsY = screenHeight - 50;
        boolean isSettingsHovered = mouseX >= settingsX && mouseX <= settingsX + (textureWidth - 2.5) && mouseY >= settingsY && mouseY <= settingsY + (textureHeight - 2.5);
        String settingsTexture = isSettingsHovered ? "textures/gui/icons/setting_hover.png" : "textures/gui/icons/setting.png";
        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource(settingsTexture),
            settingsX,
            settingsY,
            1.0F,
            1.0F,
            33,
            29,
            textureWidth,
            textureHeight
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(settingsX, settingsY)
                .dimensions(textureWidth - 2, textureHeight - 2)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> this.client.setScreen(new SpoofSectionScreen(this)))
                .tooltip(Tooltip.of(Text.literal("Spoof Settings")))
                .build()
        );
    }
}