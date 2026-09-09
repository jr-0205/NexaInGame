package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.modules.ModuleAccess;
import com.nexaclient.ingame.modules.NametagStyle;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.text.Text;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
abstract class NametagMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void nexa$visibility(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertices, int light, float delta, CallbackInfo callback) {
        if (ModuleAccess.enabled("nametags") && !NametagStyle.visible(entity)) callback.cancel();
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
    private Text nexa$label(Text original, Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertices, int light, float delta) {
        return ModuleAccess.enabled("nametags") ? NametagStyle.decorate(original, entity) : original;
    }
}
