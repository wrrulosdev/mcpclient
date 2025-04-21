package dev.wrrulosdev.mcpclient.client.mixins.window;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * This method changes the title to a custom value that includes the MCPClient version.
     *
     * @param callback The CallbackInfoReturnable instance to control the return value of the method.
     */
    @Inject(method = "getWindowTitle", at = @At(
        value = "INVOKE",
        target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;",
        ordinal = 1),
        cancellable = true)
    private void getClientTitle(CallbackInfoReturnable<String> callback) {
        callback.setReturnValue("MCPClient v1.0.0 - " + SharedConstants.getGameVersion().getName());
    }
}
