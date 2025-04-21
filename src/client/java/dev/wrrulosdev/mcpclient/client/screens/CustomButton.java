package dev.wrrulosdev.mcpclient.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.wrrulosdev.mcpclient.client.Mcpclient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class CustomButton extends ClickableWidget {
    private final PressAction pressAction;
    private final ButtonStyle style;
    private Tooltip tooltip;

    /**
     * Constructs a customizable button.
     *
     * @param x            X position.
     * @param y            Y position.
     * @param width        Width of the button.
     * @param height       Height of the button.
     * @param message      Text label of the button.
     * @param style        Button style options.
     * @param pressAction  Callback when the button is pressed.
     */
    private CustomButton(int x, int y, int width, int height, Text message,
                         ButtonStyle style, PressAction pressAction) {
        super(x, y, width, height, message);
        this.style = style;
        this.pressAction = pressAction;
    }

    /**
     * Renders the button, including background, border, image, and text.
     */
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isHovered() && this.tooltip != null) {
            this.setTooltip(this.tooltip);
        }

        if (!style.transparent) {
            int bgColor = getBackgroundColor();
            context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
        }

        if (style.borderEnabled) {
            renderBorder(context);
        }

        if (style.texture != null) {
            renderButtonImage(context);
        }

        if (!getMessage().getString().isEmpty()) {
            renderButtonText(context);
        }
    }

    /**
     * Gets the background color depending on button state.
     */
    private int getBackgroundColor() {
        if (!this.active) return style.disabledBackgroundColor;
        return this.isHovered() ? style.hoverBackgroundColor : style.normalBackgroundColor;
    }

    /**
     * Draws the button's border.
     */
    private void renderBorder(DrawContext context) {
        int borderColor = this.active ? style.borderColor : style.disabledBorderColor;
        context.fill(getX(), getY(), getX() + width, getY() + 1, borderColor); // Top
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor); // Bottom
        context.fill(getX(), getY(), getX() + 1, getY() + height, borderColor); // Left
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor); // Right
    }

    /**
     * Renders the image centered inside the button.
     */
    private void renderButtonImage(DrawContext context) {
        int imageX = getX() + (width - style.imageWidth) / 2;
        int imageY = getY() + (height - style.imageHeight) / 2;

        context.drawTexture(
            RenderLayer::getGuiTextured,
            style.texture,
            imageX,
            imageY,
            style.imageU,
            style.imageV,
            style.imageWidth,
            style.imageHeight,
            style.textureWidth,
            style.textureHeight
        );
    }

    /**
     * Draws the button text centered inside the button.
     */
    private void renderButtonText(DrawContext context) {
        TextRenderer textRenderer = Mcpclient.getMinecraftClient().textRenderer;
        int textColor = getTextColor();
        String buttonText = getMessage().getString();

        int textWidth = textRenderer.getWidth(buttonText);
        int xPosition = getX() + (width - textWidth) / 2;
        int yPosition = getY() + (height - textRenderer.fontHeight) / 2;

        context.drawTextWithShadow(textRenderer, buttonText, xPosition, yPosition, textColor);
    }

    /**
     * Gets the text color depending on state.
     */
    private int getTextColor() {
        if (!this.active) return style.disabledTextColor;
        return this.isHovered() ? style.hoverTextColor : style.normalTextColor;
    }

    /**
     * Called when the button is clicked.
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.pressAction != null) {
            this.pressAction.onPress(this);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    /**
     * Starts building a new {@link CustomButton}.
     *
     * @param message The button label.
     * @return A new {@link Builder} instance.
     */
    public static Builder builder(Text message) {
        return new Builder(message);
    }

    /**
     * Builder class for {@link CustomButton}.
     */
    public static class Builder {
        private final Text message;
        private int x;
        private int y;
        private int width = 200;
        private int height = 20;
        private ButtonStyle style = new ButtonStyle();
        private PressAction pressAction;
        private Tooltip tooltip;

        public Builder(Text message) {
            this.message = message;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder dimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder style(Consumer<ButtonStyle> styleConsumer) {
            styleConsumer.accept(this.style);
            return this;
        }

        public Builder onPress(PressAction pressAction) {
            this.pressAction = pressAction;
            return this;
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link CustomButton} with the specified settings.
         *
         * @return The new {@link CustomButton} instance.
         */
        public CustomButton build() {
            CustomButton button = new CustomButton(x, y, width, height, message, style, pressAction);
            if (tooltip != null) {
                button.tooltip = tooltip;
            }
            return button;
        }
    }

    /**
     * Class that defines the visual style of a {@link CustomButton}.
     */
    public static class ButtonStyle {
        // Visual properties
        private boolean transparent = false;
        private boolean borderEnabled = true;

        // Background colors
        private int normalBackgroundColor = 0x02000000;
        private int hoverBackgroundColor = 0x04000000;
        private int disabledBackgroundColor = 0x01000000;

        // Text colors
        private int normalTextColor = 0xFFFFFFFF;
        private int hoverTextColor = 0xFFFFFFA0;
        private int disabledTextColor = 0xFFA0A0A0;

        // Border colors
        private int borderColor = 0xFFFF0000;
        private int disabledBorderColor = 0xFF606060;

        // Image properties
        private Identifier texture = null;
        private float imageU = 1.0F;
        private float imageV = 1.0F;
        private int imageWidth = 16;
        private int imageHeight = 16;
        private int textureWidth = 256;
        private int textureHeight = 256;

        public ButtonStyle transparent(boolean transparent) {
            this.transparent = transparent;
            return this;
        }

        public ButtonStyle border(boolean enabled) {
            this.borderEnabled = enabled;
            return this;
        }

        public ButtonStyle backgroundColors(int normal, int hover, int disabled) {
            this.normalBackgroundColor = normal;
            this.hoverBackgroundColor = hover;
            this.disabledBackgroundColor = disabled;
            return this;
        }

        public ButtonStyle textColors(int normal, int hover, int disabled) {
            this.normalTextColor = normal;
            this.hoverTextColor = hover;
            this.disabledTextColor = disabled;
            return this;
        }

        public ButtonStyle borderColors(int normal, int disabled) {
            this.borderColor = normal;
            this.disabledBorderColor = disabled;
            return this;
        }

        public ButtonStyle image(Identifier texture) {
            this.texture = texture;
            return this;
        }

        public ButtonStyle image(Identifier texture, int u, int v, int regionWidth, int regionHeight) {
            return image(texture, u, v, regionWidth, regionHeight, 256, 256);
        }

        public ButtonStyle image(Identifier texture, int u, int v, int regionWidth, int regionHeight,
                                 int textureWidth, int textureHeight) {
            this.texture = texture;
            this.imageU = u;
            this.imageV = v;
            this.imageWidth = regionWidth;
            this.imageHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public ButtonStyle imageSize(int width, int height) {
            this.imageWidth = width;
            this.imageHeight = height;
            return this;
        }

        public ButtonStyle imagePosition(int u, int v) {
            this.imageU = u;
            this.imageV = v;
            return this;
        }

        public ButtonStyle textureDimensions(int width, int height) {
            this.textureWidth = width;
            this.textureHeight = height;
            return this;
        }
    }

    /**
     * Functional interface for handling button press actions.
     */
    public interface PressAction {
        void onPress(CustomButton button);
    }
}
