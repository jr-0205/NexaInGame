package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.modules.ModuleAccess;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Mixin(EntityRenderDispatcher.class)
abstract class HitboxMixin {
    @Inject(method = "shouldRenderHitboxes", at = @At("HEAD"), cancellable = true)
    private void nexa$hitboxes(CallbackInfoReturnable<Boolean> callback) {
        var client = MinecraftClient.getInstance();
        if (ModuleAccess.enabled("hitbox") && client.player != null && !client.options.hudHidden)
            callback.setReturnValue(true);
    }
}
