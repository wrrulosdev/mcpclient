package dev.wrrulosdev.mcpclient.client.mixins.screen;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.screens.CustomButton;
import dev.wrrulosdev.mcpclient.client.utils.resources.ResourcesManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin() {
        super(null);
    }

    @Shadow
    private SplashTextRenderer splashText;

    /**
     * Cancels the default splash text initialization.
     * @param ci CallbackInfo for controlling the original method flow
     */
    @Inject(
        method = "init",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onInit(CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Replaces the panorama background with a custom gradient and animated texture.
     * @param context The DrawContext for rendering operations
     * @param delta Time since last frame in seconds
     * @param ci CallbackInfo for controlling the original method flow
     */
    @Inject(
        method = "renderPanoramaBackground",
        at = @At("HEAD"),
        cancellable = true
    )
    private void replacePanoramaBackground(DrawContext context, float delta, CallbackInfo ci) {
        context.fillGradient(0, 0, this.width, this.height, 0xCC000000, 0xCC000000);
        context.drawTexture(RenderLayer::getGuiTextured, Mcpclient.getGifTextureManager().getCurrentFrame(), 0, 0, this.width, this.height, this.width, this.height, this.width, this.height, this.width, this.height);
        ci.cancel();
    }

    /**
     * Adds custom widgets including logo, social media buttons, and main menu buttons.
     * @param context The DrawContext for rendering operations
     * @param mouseX Current X position of the mouse cursor
     * @param mouseY Current Y position of the mouse cursor
     * @param delta Time since last frame in seconds
     * @param ci CallbackInfo for controlling the original method flow
     */
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
            ordinal = 0
        )
    )
    private void addCustomWidgets(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource("textures/gui/logo.png"),
            this.width / 2 - 55, this.height / 8, 1.0F, 1.0F, 120, 80, 120, 80
        );

        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource("textures/gui/icons/discord.png"),
            0, 0, 1.0F, 1.0F, 35, 30, 35, 30
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(0, 0)
                .dimensions(28, 23)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> MinecraftClient.getInstance()
                    .execute(() -> Util.getOperatingSystem()
                        .open(URI.create("https://discord.mcptool.net"))))
                .tooltip(Tooltip.of(Text.literal("Join our Discord community!")))
                .build()
        );

        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource("textures/gui/icons/github.png"),
            30, 3, 1.0F, 1.0F, 30, 25, 30, 25
        );

        this.addDrawableChild(
            CustomButton.builder(Text.empty())
                .position(30, 0)
                .dimensions(26, 25)
                .style(style -> style
                    .transparent(true)
                    .border(false))
                .onPress(button -> MinecraftClient.getInstance()
                    .execute(() -> Util.getOperatingSystem()
                        .open(URI.create("https://github.com/wrrulosdev/mcpclient"))))
                .tooltip(Tooltip.of(Text.literal("View Project Source Code")))
                .build()
        );

        int centerX = this.width / 2 - 100;
        int startY = this.height / 4 + 48;

        this.addDrawableChild(
            CustomButton.builder(Text.literal("Singleplayer"))
                .position(centerX, startY)
                .dimensions(200, 20)
                .style(style -> style
                    .transparent(false)
                    .border(true)
                    .backgroundColors(0x80202020, 0x80404040, 0x80101010)
                    .textColors(0xFFFFFFFF, 0xFFFFFFA0, 0xFFA0A0A0))
                .onPress(button -> MinecraftClient.getInstance()
                    .setScreen(new SelectWorldScreen(this)))
                .build()
        );

        this.addDrawableChild(
            CustomButton.builder(Text.literal("Multiplayer"))
                .position(centerX, startY + 25)
                .dimensions(200, 20)
                .style(style -> style
                    .transparent(false)
                    .border(true)
                    .backgroundColors(0x80202020, 0x80404040, 0x80101010)
                    .textColors(0xFFFFFFFF, 0xFFFFFFA0, 0xFFA0A0A0))
                .onPress(button -> MinecraftClient.getInstance()
                    .setScreen(new MultiplayerScreen(this)))
                .build()
        );

        this.addDrawableChild(
            CustomButton.builder(Text.literal("Options"))
                .position(centerX, startY + 50)
                .dimensions(95, 20)
                .style(style -> style
                    .transparent(false)
                    .border(true)
                    .backgroundColors(0x80202020, 0x80404040, 0x80101010)
                    .textColors(0xFFFFFFFF, 0xFFFFFFA0, 0xFFA0A0A0))
                .onPress(button -> MinecraftClient.getInstance()
                    .setScreen(new OptionsScreen(this, Mcpclient.getMinecraftClient().options)))
                .build()
        );

        this.addDrawableChild(
            CustomButton.builder(Text.literal("Quit"))
                .position(centerX + 105, startY + 50)
                .dimensions(95, 20)
                .style(style -> style
                    .transparent(false)
                    .border(true)
                    .backgroundColors(0x80202020, 0x80404040, 0x80101010)
                    .textColors(0xFFFFFFFF, 0xFFFFA0A0, 0xFFA0A0A0))
                .onPress(button -> Mcpclient.getMinecraftClient().stop())
                .build()
        );
    }

    /**
     * Modifies the version text displayed in the bottom left corner.
     * @param original The original version string
     * @return Custom version string for the client
     */
    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I",
            ordinal = 0
        ),
        index = 1
    )
    private String modifyVersionText(String original) {
        return "MCPClient v1.0.0";
    }
}