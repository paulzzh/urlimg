package com.paulzzh.urlimg;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.network.chat.ClickEvent.Action.OPEN_URL;

// The value here should match an entry in the META-INF/mods.toml file
@net.minecraftforge.fml.common.Mod("urlimg")
public class Mod {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger("urlimg");
    public static final Map<FormattedCharSequence, FormattedText> line_map = new HashMap<>();
    private static final int line = 5;
    private static final int line_height = 10;
    public static final ImageManager IM = new ImageManager() {
        @Override
        public void showImages(String msg) {
            ChatComponent mc = Minecraft.getInstance().gui.getChat();
            mc.addMessage(Component.literal("urlimg=" + msg));
            for (int i = 0; i < line; i++) {
                mc.addMessage(Component.literal(""));
            }
        }

        @Override
        public Image<?> readImage(String hash, InputStream in) {
            try {
                NativeImage nativeImage = NativeImage.read(in);
                ResourceLocation id = new ResourceLocation("urlimg", hash);
                Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(nativeImage));
                return new Image<>(hash, nativeImage, nativeImage.getWidth(), nativeImage.getHeight(), line * line_height);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Image<?> readAndSaveImage(String hash, InputStream in, File out) {
            try {
                BufferedImage bufferedImage = ImageIO.read(in); //1.20.3+ can't read jpeg
                ImageIO.write(bufferedImage, "png", out);
                LOGGER.info("cache image: {}", out.getName());
                return readImage(hash, new FileInputStream(out));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    public Mod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static List<String> getLinksFromChat(Component message) {
        List<String> list = new ArrayList<>();
        getLinksFromChat0(list, message);
        return list;
    }

    public static void getLinksFromChat0(List<String> list, Component message) {
        List<Component> chats = message.getSiblings();
        getLinksFromChat1(list, message);
        for (Component chat : chats) {
            getLinksFromChat1(list, chat);
            getLinksFromChat0(list, chat);
        }
    }

    public static void getLinksFromChat1(List<String> list, Component message) {
        ClickEvent event = message.getStyle().getClickEvent();
        if (event != null && event.getAction() == OPEN_URL) {
            String url = event.getValue();
            if (!list.contains(url)) {
                list.add(url);
                //LOGGER.info(url);
            }
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
    }

    @SubscribeEvent
    public void onReceiveChat(ClientChatReceivedEvent event) {
        // do something when the server starts
        IM.addImages(getLinksFromChat(event.getMessage()));
    }
}