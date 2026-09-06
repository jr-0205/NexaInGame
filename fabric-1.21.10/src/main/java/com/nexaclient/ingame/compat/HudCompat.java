package com.nexaclient.ingame.compat;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;

public final class HudCompat {
    private HudCompat() { }

    public static void push(DrawContext context, int x, int y, float scale) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);
        context.getMatrices().scale(scale, scale);
    }

    public static void pop(DrawContext context) { context.getMatrices().popMatrix(); }

    public static void drawOverlay(DrawContext context, TextRenderer text, ItemStack stack, int x, int y) {
        context.drawStackOverlay(text, stack, x, y);
    }

    public static void drawBrandBackground(DrawContext context, int width, int height) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
            Identifier.of("nexa_ingame", "textures/gui/nexa_night_background.png"),
            0, 0, 0f, 0f, width, height, width, height);
    }
}
