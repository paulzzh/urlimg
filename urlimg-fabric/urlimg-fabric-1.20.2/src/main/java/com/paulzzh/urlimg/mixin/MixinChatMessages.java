package com.paulzzh.urlimg.mixin;

import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.paulzzh.urlimg.Mod.line_map;

@Mixin(net.minecraft.client.util.ChatMessages.class)
public class MixinChatMessages {
    @Final
    @Shadow
    private static final OrderedText SPACES = OrderedText.styled(32, Style.EMPTY);
    //private static ArrayList<OrderedText> list = Lists.newArrayList();

    @Inject(method = "method_30886", at = @At("RETURN"))
    private static void injected(List list, StringVisitable stringVisitable, Boolean boolean_, CallbackInfo ci) {
        line_map.put((OrderedText) list.get(list.size() - 1), stringVisitable);
    }

    /*@Inject(method = "breakRenderedChatMessageLines", at = @At(value = "TAIL"))
    private static void injected2(StringVisitable stringVisitable, int i, TextRenderer textRenderer, CallbackInfoReturnable<List<OrderedText>> cir) {
        cir.setReturnValue(list);
    }*/
}
