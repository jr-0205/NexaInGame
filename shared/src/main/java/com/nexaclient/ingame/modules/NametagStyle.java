package com.nexaclient.ingame.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class NametagStyle {
    private NametagStyle() { }
    public static boolean visible(Entity entity) {
        var client = MinecraftClient.getInstance();
        int range = ModuleAccess.registry().state("nametags").intSetting("range", 32, 8, 64);
        return client.player != null && !entity.isInvisibleTo(client.player) && client.player.canSee(entity)
            && client.player.squaredDistanceTo(entity) <= range * range;
    }

    public static Text decorate(Text original, Entity entity) {
        var state = ModuleAccess.registry().state("nametags");
        Formatting color = Formatting.byName(state.setting("color", "aqua"));
        var result = original.copy().formatted(color == null ? Formatting.AQUA : color);
        var player = MinecraftClient.getInstance().player;
        if (player != null && state.booleanSetting("distance", true)) {
            result.append(Text.literal("  " + Math.round(Math.sqrt(player.squaredDistanceTo(entity))) + "m").formatted(Formatting.GRAY));
        }
        return result;
    }
}
