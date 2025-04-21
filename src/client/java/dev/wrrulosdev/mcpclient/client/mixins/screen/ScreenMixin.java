package dev.wrrulosdev.mcpclient.client.mixins.screen;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import dev.wrrulosdev.mcpclient.client.screens.SpoofSectionScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    public int height;

    @Shadow
    public int width;

    /**
     * Determines if the current screen is one of the target screens for rendering a custom background.
     *
     * @param screen The screen instance to check.
     * @return true if the screen is one of the target screens (TitleScreen, DisconnectedScreen, etc.), false otherwise.
     */
    @Unique
    private boolean isTargetScreen(Screen screen) {
        return screen instanceof TitleScreen || screen instanceof DisconnectedScreen || screen instanceof MultiplayerScreen ||
            screen instanceof AddServerScreen || screen instanceof DirectConnectScreen || screen instanceof ConnectScreen ||
            screen instanceof SelectWorldScreen || screen instanceof OptionsScreen || screen instanceof ConfirmScreen || screen instanceof SpoofSectionScreen;
    }

    /**
     * Injects into the renderBackground method of the Screen class to render a custom background
     * for specific target screens.
     *
     * @param context The DrawContext instance to handle the drawing operations.
     * @param mouseX The mouse's X coordinate.
     * @param mouseY The mouse's Y coordinate.
     * @param delta The time delta since the last render frame.
     * @param ci The CallbackInfo instance to control the flow of the injected method.
     */
    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen currentScreen = (Screen) (Object) this;
        if (!(isTargetScreen(currentScreen))) return;

        if (Mcpclient.getMinecraftClient().player != null && Mcpclient.getMinecraftClient().player.getServer() == null)
            return;

        context.drawTexture(RenderLayer::getGuiTextured, Mcpclient.getGifTextureManager().getCurrentFrame(), 0, 0, this.width, this.height, this.width, this.height, this.width, this.height, this.width, this.height);

        // PNG (not used now)
        //context.drawTexture(RenderLayer::getGuiTextured, ResourcesManager.getResource("textures/gui/wallpaper1.png"), 0, 0, this.width, this.height, this.width, this.height, this.width, this.height, this.width, this.height);
        ci.cancel();
    }
}
