package com.paulzzh.urlimg.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.systems.RenderSystem;
import com.paulzzh.urlimg.Image;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.paulzzh.urlimg.Mod.IM;
import static com.paulzzh.urlimg.Mod.line_map;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class MixinChatHud {

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void injected1(Args args, @Share("indexY") LocalIntRef argRef) {
        argRef.set(args.get(1));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"))
    private int injected3(DrawContext instance, TextRenderer textRenderer, OrderedText orderedText, int i, int j, int k, @Share("indexY") LocalIntRef argRef) {
        StringVisitable sv = line_map.get(orderedText);
        if (sv != null && sv.getString().startsWith("urlimg=")) {
            String uuid = sv.getString().substring(7, 39);
            RenderSystem.enableBlend();
            int indexX = 0;
            for (Image<?> img : IM.getImages(uuid)) {
                instance.drawTexture(Identifier.of("urlimg", img.getHash()), indexX, argRef.get(), 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
                indexX += img.getWidth();
            }
            RenderSystem.disableBlend();
            return 0;
        }
        return instance.drawTextWithShadow(textRenderer, orderedText, i, j, k);
    }
}
