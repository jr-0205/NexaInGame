package com.nexaclient.ingame.compat;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class HudCompat {
    private HudCompat() { }

    public static void push(DrawContext context, int x, int y, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1f);
    }

    public static void pop(DrawContext context) { context.getMatrices().pop(); }

    public static void drawOverlay(DrawContext context, TextRenderer text, ItemStack stack, int x, int y) {
        context.drawItemInSlot(text, stack, x, y);
    }

    public static void drawBrandBackground(DrawContext context, int width, int height) {
        context.drawTexture(Identifier.of("nexa_ingame", "textures/gui/nexa_night_background.png"),
            0, 0, 0f, 0f, width, height, width, height);
    }
}
