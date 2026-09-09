package com.nexaclient.ingame.mixin;

import com.nexaclient.ingame.hud.HudRenderer;
import com.nexaclient.ingame.hud.VisualHud;
import com.nexaclient.ingame.modules.ModuleAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)
    private void nexa$scoreboard(DrawContext context, ScoreboardObjective objective, CallbackInfo callback) {
        if (!ModuleAccess.enabled("scoreboard")) return;
        VisualHud.scoreboard(context, objective);
        callback.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void nexa$crosshair(DrawContext context, RenderTickCounter ticks, CallbackInfo callback) {
        if (!ModuleAccess.enabled("crosshair")) return;
        var client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator() || client.options.hudHidden
            || !client.options.getPerspective().isFirstPerson() || client.getDebugHud().shouldShowDebugHud()) return;
        HudRenderer.renderCrosshair(context, client, ModuleAccess.registry());
        callback.cancel();
    }
}
