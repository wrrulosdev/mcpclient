package dev.wrrulosdev.mcpclient.client.mixins.hud;

import dev.wrrulosdev.mcpclient.client.Mcpclient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.List;

/**
 * Mixin that modifies the behavior of ChatHud to introduce custom effects like smooth scrolling
 * and adjustments in rendering, including mask height.
 * Code based on the SpigotRCE mixin for ChatHud -> github.com/SpigotRCE/ParadiseClient-Fabric
 */
@Mixin(value = ChatHud.class, priority = 1001)
public class ChatHudMixin {

    @Unique
    float scrollOffset;

    @Unique
    float maskHeightBuffer;

    @Unique
    boolean refreshing = false;

    @Unique
    int scrollValBefore;

    @Unique
    DrawContext savedContext;

    @Unique
    int savedCurrentTick;

    @Unique
    Vec2f mtc = new Vec2f(0, 0);

    @Shadow
    private int scrolledLines;

    @Final
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

    /**
     * Injects at the start of the render method to save context and calculate scrolling.
     *
     * @param context The rendering context for the HUD.
     * @param currentTick The current tick count.
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param focused Whether the chat is focused.
     * @param ci Callback information.
     */
    @Inject(method = "render", at = @At("HEAD"))
    public void renderH(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        savedContext = context;
        savedCurrentTick = currentTick;
        scrollOffset = (float) (scrollOffset * Math.pow(0.3f, getLastFrameDuration()));
        scrollValBefore = scrolledLines;
        scrolledLines -= getChatScrollOffset() / getLineHeight();
        if (scrolledLines < 0) scrolledLines = 0;
    }

    /**
     * Modifies matrix translation parameters for proper positioning.
     *
     * @param args The arguments passed to the matrix translation method.
     */
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    private void matrixTranslateCorrector(Args args) {
        int x = (int) (float) args.get(0) - 4;
        int y = (int) (float) args.get(1);
        var newY = (float) ((mtc.y - y) * Math.pow(0.3f, getLastFrameDuration()) + y);
        args.set(1, (float) Math.round(newY));
        mtc = new Vec2f(x, newY);
    }

    /**
     * Adjusts the height of the mask based on the chat state.
     *
     * @param m The current mask height value.
     * @return The adjusted mask height.
     */
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 7)
    private int mask(int m) {
        var targetHeight = getTargetHeight();
        maskHeightBuffer = (float) ((maskHeightBuffer - targetHeight) * Math.pow(0.3f, getLastFrameDuration()) + targetHeight);
        var maskTop = m - Math.round(maskHeightBuffer) + (int) mtc.y;
        var maskBottom = m + (int) mtc.y;

        if (getChatScrollOffset() == 0 && Math.round(maskHeightBuffer) != 0) {
            if (Math.round(maskHeightBuffer) == targetHeight) {
                maskBottom += 2;
                maskTop -= 2;
            } else {
                maskBottom += 2;
            }
        }

        if (FabricLoader.getInstance().getObjectShare().get("raised:chat") instanceof Integer distance) {
            maskTop -= distance;
            maskBottom -= distance;
        }

        savedContext.enableScissor(0, maskTop, savedContext.getScaledWindowWidth(), maskBottom);
        return m;
    }

    /**
     * Calculates the target height for the mask based on the chat state.
     *
     * @return The target height of the mask.
     */
    @Unique
    private int getTargetHeight() {
        var shownLineCount = 0;

        for (int r = 0; r + scrolledLines < visibleMessages.size() && r < getVisibleLineCount(); r++) {
            if (savedCurrentTick - visibleMessages.get(r).addedTime() < 200 || isChatFocused()) shownLineCount++;
        }

        return shownLineCount * getLineHeight();
    }

    /**
     * Adjusts Y position to properly align chat during rendering.
     *
     * @param y The current Y position.
     * @return The adjusted Y position.
     */
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 18)
    private int changePosY(int y) {
        return y - getChatDrawOffset();
    }

    /**
     * Cleans up OpenGL state by disabling the scissor test post-render.
     *
     * @param a The scissor state before rendering.
     * @return The updated scissor state after rendering.
     */
    @ModifyVariable(method = "render", at = @At("STORE"))
    private long demask(long a) {
        savedContext.disableScissor();
        return a;
    }

    /**
     * Restores scroll value after rendering is completed.
     *
     * @param context The rendering context for the HUD.
     * @param currentTick The current tick count.
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param focused Whether the chat is focused.
     * @param ci Callback information.
     */
    @Inject(method = "render", at = @At("TAIL"))
    public void renderT(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        scrolledLines = scrollValBefore;
    }

    /**
     * Changes scroll behavior when a new message is added to the chat.
     *
     * @param ot The list of ordered text to be added to the chat.
     * @return The modified list of ordered text.
     */
    @ModifyVariable(method = "addVisibleMessage", at = @At("STORE"), ordinal = 0)
    List<OrderedText> onNewMessage(List<OrderedText> ot) {
        if (refreshing) return ot;
        scrollOffset -= ot.size() * getLineHeight();
        return ot;
    }

    /**
     * Stores scroll state before modification.
     *
     * @param scroll The amount of scroll applied.
     * @param ci Callback information.
     */
    @Inject(method = "scroll", at = @At("HEAD"))
    public void scrollH(int scroll, CallbackInfo ci) {
        scrollValBefore = scrolledLines;
    }

    /**
     * Updates the scroll offset after scrolling is done.
     *
     * @param scroll The amount of scroll applied.
     * @param ci Callback information.
     */
    @Inject(method = "scroll", at = @At("TAIL"))
    public void scrollT(int scroll, CallbackInfo ci) {
        scrollOffset += (scrolledLines - scrollValBefore) * getLineHeight();
    }

    /**
     * Captures scroll value before reset.
     *
     * @param ci Callback information.
     */
    @Inject(method = "resetScroll", at = @At("HEAD"))
    public void scrollResetH(CallbackInfo ci) {
        scrollValBefore = scrolledLines;
    }

    /**
     * Modifies scroll offset after resetting the scroll position.
     *
     * @param ci Callback information.
     */
    @Inject(method = "resetScroll", at = @At("TAIL"))
    public void scrollResetT(CallbackInfo ci) {
        scrollOffset += (scrolledLines - scrollValBefore) * getLineHeight();
    }

    /**
     * Adds lines above based on mask height calculation.
     *
     * @param i The current line count.
     * @return The adjusted line count.
     */
    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getLineHeight()I"), ordinal = 3)
    private int addLinesAbove(int i) {
        return (int) Math.ceil(Math.round(maskHeightBuffer) / (float) getLineHeight()) + (getChatScrollOffset() < 0 ? 1 : 0);
    }

    /**
     * Subtracts a line if needed, depending on scroll state.
     *
     * @param r The current line count.
     * @return The adjusted line count.
     */
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 12)
    private int addLinesUnder(int r) {
        if (scrolledLines == 0 || getChatScrollOffset() <= 0) return r;
        return r - 1;
    }

    /**
     * Flags chat as refreshing during the process.
     *
     * @param ci Callback information.
     */
    @Inject(method = "refresh", at = @At("HEAD"))
    private void refreshH(CallbackInfo ci) {
        refreshing = true;
    }

    /**
     * Indicates the end of the chat refresh process.
     *
     * @param ci Callback information.
     */
    @Inject(method = "refresh", at = @At("TAIL"))
    private void refreshT(CallbackInfo ci) {
        refreshing = false;
    }

    @Shadow
    private int getLineHeight() {
        return 0;
    }

    @Shadow
    public int getVisibleLineCount() {
        return 0;
    }

    @Shadow
    public boolean isChatFocused() {
        return false;
    }

    /**
     * Returns the offset for drawing chat based on scroll position.
     *
     * @return The chat draw offset.
     */
    @Unique
    int getChatDrawOffset() {
        return Math.round(scrollOffset) - (Math.round(scrollOffset) / getLineHeight() * getLineHeight());
    }

    /**
     * Returns the current scroll offset.
     *
     * @return The current scroll offset.
     */
    @Unique
    int getChatScrollOffset() {
        return Math.round(scrollOffset);
    }

    /**
     * Retrieves the duration of the last frame for smooth animations.
     *
     * @return The duration of the last frame.
     */
    @Unique
    public float getLastFrameDuration() {
        return Mcpclient.getMinecraftClient().getRenderTickCounter().getDynamicDeltaTicks();
    }
}
