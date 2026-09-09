package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.modules.ModuleAccess;
import com.nexaclient.ingame.modules.NametagStyle;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.state.EntityRenderState;

@Mixin(EntityRenderer.class)
abstract class NametagMixin {
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void nexa$label(Entity entity, EntityRenderState state, float delta, CallbackInfo callback) {
        if (!ModuleAccess.enabled("nametags") || state.displayName == null) return;
        state.displayName = NametagStyle.visible(entity) ? NametagStyle.decorate(state.displayName, entity) : null;
    }
}
