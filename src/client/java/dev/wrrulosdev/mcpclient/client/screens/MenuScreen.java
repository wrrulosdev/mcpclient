package dev.wrrulosdev.mcpclient.client.screens;

import dev.wrrulosdev.mcpclient.client.utils.resources.ResourcesManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MenuScreen extends Screen {

    private final Screen parent;
    private CustomButton settingButton;

    /**
     * Constructs a new MenuScreen.
     *
     * @param parent the parent screen to return to when closing
     */
    public MenuScreen(Screen parent) {
        super(Text.literal("").setStyle(Style.EMPTY.withColor(Formatting.RED)));
        this.parent = parent;
    }

    /**
     * Initializes the screen by setting up buttons and layout.
     */
    @Override
    protected void init() {
        super.init();

        int buttonWidth = 160;
        int iconsButtonWidth = 32;
        int buttonsHeight = 25;
        int padding = 20;

        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = (this.height - buttonsHeight) / 2 + 30;

        // Create the settings button
        this.settingButton = CustomButton.builder(Text.literal("Client Settings"))
            .position(buttonX, buttonY)
            .dimensions(buttonWidth, buttonsHeight)
            .style(style -> style
                .transparent(true)
                .border(true)
                .backgroundColors(0x80202020, 0x80404040, 0x80101010)
                .textColors(0xFFFFFFFF, 0xFFFFFFA0, 0xFFA0A0A0))
            .onPress(button -> this.client.setScreen(new SettingsScreen(this)))
            .tooltip(Tooltip.of(Text.empty()))
            .build();

        this.addDrawableChild(this.settingButton);
    }

    /**
     * Renders the screen, including background, title, logo and buttons.
     *
     * @param context the rendering context
     * @param mouseX  the x position of the mouse
     * @param mouseY  the y position of the mouse
     * @param delta   the frame delta time
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Draw title (currently empty but can be modified)
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            20,
            0xFFFFFF
        );

        // Draw logo above the settings button
        int logoX = this.width / 2 - 65;
        int logoY = this.settingButton.getY() - 80 - 10;

        context.drawTexture(
            RenderLayer::getGuiTextured,
            ResourcesManager.getResource("textures/gui/logo.png"),
            logoX,
            logoY,
            1.0F,
            1.0F,
            130,
            80,
            130,
            80
        );
    }
}
