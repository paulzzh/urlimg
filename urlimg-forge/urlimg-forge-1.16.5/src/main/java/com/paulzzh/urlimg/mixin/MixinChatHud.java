package com.paulzzh.urlimg.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.paulzzh.urlimg.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.paulzzh.urlimg.Mod.IM;
import static com.paulzzh.urlimg.Mod.line_map;
import static net.minecraft.client.gui.AbstractGui.blit;

@Mixin(net.minecraft.client.gui.NewChatGui.class)
public abstract class MixinChatHud {
    @Final
    @Shadow
    private Minecraft minecraft;
    private int indexY;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/NewChatGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"))
    private void injected1(Args args) {
        indexY = args.get(2);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawShadow(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/IReorderingProcessor;FFI)I"))
    private int injected3(FontRenderer instance, MatrixStack p_238407_1_, IReorderingProcessor p_238407_2_, float p_238407_3_, float p_238407_4_, int p_238407_5_) {
        ITextProperties sv = line_map.get(p_238407_2_);
        if (sv != null && sv.getString().startsWith("urlimg=")) {
            String uuid = sv.getString().substring(7, 39);
            RenderSystem.enableBlend();
            int indexX = 0;
            for (Image<?> img : IM.getImages(uuid)) {
                minecraft.getTextureManager().bind(new ResourceLocation("urlimg", img.getHash()));
                blit(p_238407_1_, indexX, indexY, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
                indexX += img.getWidth();
            }
            RenderSystem.disableBlend();
            return 0;
        }
        return instance.drawShadow(p_238407_1_, p_238407_2_, p_238407_3_, p_238407_4_, p_238407_5_);
    }
}
