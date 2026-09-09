package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.modules.ModuleAccess;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.util.Identifier;

@Mixin(DebugHudProfile.class)
abstract class HitboxMixin {
    @Inject(method = "isEntryVisible", at = @At("HEAD"), cancellable = true)
    private void nexa$hitboxes(Identifier entry, CallbackInfoReturnable<Boolean> callback) {
        var client = MinecraftClient.getInstance();
        if (entry.equals(DebugHudEntries.ENTITY_HITBOXES) && ModuleAccess.enabled("hitbox")
            && client.player != null && !client.options.hudHidden) callback.setReturnValue(true);
    }
}
