package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.ui.NexaTheme;
import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps vanilla out-of-game screens visually continuous with the NEXA shell. */
@Mixin(Screen.class)
abstract class ScreenBackgroundMixin {
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void nexa$drawMenuBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (NexaTheme.brandedMenus() && client.world == null) {
            NexaUi.backdrop(context, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
            ci.cancel();
        }
    }
}
