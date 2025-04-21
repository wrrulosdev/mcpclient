package dev.wrrulosdev.mcpclient.client.utils.resources;

import com.madgag.gif.fmsware.GifDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GifTextureManager {

    private final List<Identifier> frames = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();
    private final int frameDuration;
    private long lastFrameTime = 0;
    private int currentFrame = 0;

    /**
     * Constructs a GifTextureManager to load and manage the frames of a GIF texture.
     * The constructor loads the GIF frames based on the provided file path and frame rate.
     *
     * @param gifPath The path to the GIF file.
     * @param frameRate The frame rate (frames per second) of the GIF.
     */
    public GifTextureManager(String gifPath, int frameRate) {
        this.frameDuration = 1000 / frameRate;
        loadGifFrames(gifPath);
    }

    /**
     * Loads the frames of a GIF from the specified path and stores them as textures.
     * Each frame is decoded, processed, and registered as a texture for use in Minecraft.
     *
     * @param gifPath The path to the GIF file to load.
     */
    private void loadGifFrames(String gifPath) {
        try (InputStream gifStream = MinecraftClient.getInstance().getResourceManager()
            .getResource(ResourcesManager.getResource(gifPath))
            .get().getInputStream()) {

            GifDecoder gifDecoder = new GifDecoder();
            gifDecoder.read(gifStream);

            for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
                Identifier frameId = ResourcesManager.getResource("textures/gui/frame" + i + ".png");
                frames.add(frameId);

                NativeImage nativeImage = new NativeImage(
                    gifDecoder.getFrame(i).getWidth(),
                    gifDecoder.getFrame(i).getHeight(),
                    false
                );

                for (int y = 0; y < gifDecoder.getFrame(i).getHeight(); y++) {
                    for (int x = 0; x < gifDecoder.getFrame(i).getWidth(); x++) {
                        int pixel = gifDecoder.getFrame(i).getRGB(x, y);
                        int alpha = (pixel >> 24) & 0xFF;
                        int red = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = pixel & 0xFF;
                        int argbPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        nativeImage.setColorArgb(x, y, argbPixel);
                    }
                }

                int finalI = i;
                Supplier<String> nameSupplier = () -> "gif_frame_" + finalI;
                NativeImageBackedTexture texture = new NativeImageBackedTexture(nameSupplier, nativeImage);
                MinecraftClient.getInstance().getTextureManager().registerTexture(frameId, texture);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load GIF frames from path: {}", gifPath, e);
        }
    }

    /**
     * Retrieves the current frame of the GIF based on the elapsed time and frame rate.
     * This method ensures that frames are updated at the specified frame rate.
     *
     * @return The identifier of the current frame to be displayed.
     */
    public Identifier getCurrentFrame() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }

        return frames.get(currentFrame);
    }
}
