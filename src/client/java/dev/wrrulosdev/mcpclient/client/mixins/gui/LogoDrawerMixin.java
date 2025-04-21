package dev.wrrulosdev.mcpclient.client.mixins.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {

    /**
     * Cancels the rendering of the logo by intercepting the draw method.
     *
     * @param context     The draw context used for rendering.
     * @param screenWidth The width of the screen.
     * @param alpha       The transparency value for rendering.
     * @param ci          The callback info used to cancel the method.
     */
    @Inject(method = "draw(Lnet/minecraft/client/gui/DrawContext;IF)V", at = @At(value = "HEAD"), cancellable = true)
    public void cancelRender(DrawContext context, int screenWidth, float alpha, CallbackInfo ci) {
        ci.cancel();
    }
}
