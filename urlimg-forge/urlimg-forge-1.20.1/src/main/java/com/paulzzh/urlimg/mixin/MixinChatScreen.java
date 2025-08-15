package com.paulzzh.urlimg.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.gui.screens.ChatScreen.class)
public class MixinChatScreen {
    @Redirect(method = "mouseScrolled", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;hasShiftDown()Z"))
    private boolean injected() {
        return !ChatScreen.hasShiftDown();
    }
}
