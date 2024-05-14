package com.paulzzh.urlimg;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.text.ClickEvent.Action.OPEN_URL;

public class Mod implements ClientModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("urlimg");
    public static final Map<OrderedText, StringVisitable> line_map = new HashMap<>();
    private static final int line = 5;
    private static final int line_height = 10;
    public static final ImageManager IM = new ImageManager() {
        public void showImages(String msg) {
            ChatHud hud = MinecraftClient.getInstance().inGameHud.getChatHud();
            hud.addMessage(Text.of("urlimg=" + msg));
            for (int i = 0; i < line; i++) {
                hud.addMessage(Text.of(""));
            }
        }

        @Override
        public Image<NativeImage> readImage(String hash, InputStream in) {
            try {
                NativeImage nativeImage = NativeImage.read(in);
                Identifier id = Identifier.of("urlimg", hash);
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(nativeImage));
                return new Image<>(hash, nativeImage, nativeImage.getWidth(), nativeImage.getHeight(), line * line_height);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Image<NativeImage> readAndSaveImage(String hash, InputStream in, File out) {
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

    public static List<String> getLinksFromChat(Text message) {
        List<String> list = new ArrayList<>();
        getLinksFromChat0(list, message);
        return list;
    }

    public static void getLinksFromChat0(List<String> list, Text message) {
        List<Text> chats = message.getSiblings();
        getLinksFromChat1(list, message);
        for (Text chat : chats) {
            getLinksFromChat1(list, chat);
            getLinksFromChat0(list, chat);
        }
    }

    public static void getLinksFromChat1(List<String> list, Text message) {
        ClickEvent event = message.getStyle().getClickEvent();
        if (event != null && event.getAction() == OPEN_URL) {
            String url = event.getValue();
            if (!list.contains(url)) {
                list.add(url);
                //LOGGER.info(url);
            }
        }
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Hello Fabric world!");
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> IM.addImages(getLinksFromChat(message)));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> IM.addImages(getLinksFromChat(message)));
    }
}