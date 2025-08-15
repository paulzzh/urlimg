package com.paulzzh.urlimg.mixin;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.paulzzh.urlimg.Mod.line_map;

@Mixin(net.minecraft.client.gui.components.ComponentRenderUtils.class)
public class MixinChatMessages {
    @Inject(method = "m_94001_", at = @At("RETURN"))
    private static void injected(List list, FormattedText stringVisitable, Boolean p_94004_, CallbackInfo ci) {
        line_map.put((FormattedCharSequence) list.get(list.size() - 1), stringVisitable);
    }
}
