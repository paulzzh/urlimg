package com.paulzzh.urlimg.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)
public class MixinChatScreen {
    @Redirect(method = "mouseScrolled", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;hasShiftDown()Z"))
    private boolean injected() {
        return !ChatScreen.hasShiftDown();
    }
}
