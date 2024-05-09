package com.paulzzh.urlimg.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.systems.RenderSystem;
import com.paulzzh.urlimg.Image;
import com.paulzzh.urlimg.ImageManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.paulzzh.urlimg.Mod.line_map;
import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class MixinChatHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0))
    private void injected2(MatrixStack matrixStack, int i, int j, int k, int l, int m, @Share("indexY") LocalIntRef argRef) {
        argRef.set(j);
        ChatHud.fill(matrixStack, i, j, k, l, m);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"))
    private int injected3(TextRenderer instance, MatrixStack matrixStack, OrderedText orderedText, float f, float g, int i, @Share("indexY") LocalIntRef argRef) {
        StringVisitable sv = line_map.get(orderedText);
        if (sv != null && sv.getString().startsWith("urlimg=")) {
            String uuid = sv.getString().substring(7, 39);
            RenderSystem.enableBlend();
            int indexX = 0;
            for (Image img : ImageManager.getImages(uuid)) {
                RenderSystem.setShaderTexture(0, Identifier.of("urlimg", img.getHash()));
                drawTexture(matrixStack, indexX, argRef.get(), 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
                indexX += img.getWidth();
            }
            RenderSystem.disableBlend();
            return 0;
        }
        return instance.drawWithShadow(matrixStack, orderedText, f, g, i);
    }
}
