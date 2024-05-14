package com.paulzzh.urlimg.mixin;

import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.paulzzh.urlimg.Mod.line_map;

@Mixin(net.minecraft.client.gui.RenderComponentsUtil.class)
public class MixinChatMessages {
    @Inject(method = "*(Ljava/util/List;Lnet/minecraft/util/text/ITextProperties;Ljava/lang/Boolean;)V", at = @At("RETURN"))
    private static void injected(List list, ITextProperties p_243256_1_, Boolean p_243256_2_, CallbackInfo ci) {
        line_map.put((IReorderingProcessor) list.get(list.size() - 1), p_243256_1_);
    }
}
