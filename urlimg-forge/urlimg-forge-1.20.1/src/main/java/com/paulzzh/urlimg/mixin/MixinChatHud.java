package com.paulzzh.urlimg.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paulzzh.urlimg.Image;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.paulzzh.urlimg.Mod.IM;
import static com.paulzzh.urlimg.Mod.line_map;

@Mixin(net.minecraft.client.gui.components.ChatComponent.class)
public abstract class MixinChatHud {
    private int indexY;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    private void injected1(GuiGraphics instance, int i, int j, int k, int l, int m) {
        indexY = j;
        instance.fill(i, j, k, l, m);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int injected3(GuiGraphics instance, Font textRenderer, FormattedCharSequence orderedText, int i, int j, int k) {
        FormattedText sv = line_map.get(orderedText);
        if (sv != null && sv.getString().startsWith("urlimg=")) {
            String uuid = sv.getString().substring(7, 39);
            RenderSystem.enableBlend();
            int indexX = 0;
            for (Image<?> img : IM.getImages(uuid)) {
                instance.blit(new ResourceLocation("urlimg", img.getHash()), indexX, indexY, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
                indexX += img.getWidth();
            }
            RenderSystem.disableBlend();
            return 0;
        }
        return instance.drawString(textRenderer, orderedText, i, j, k);
    }
}
