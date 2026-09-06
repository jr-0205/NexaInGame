package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.compat.HudCompat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Reusable visual language for the NEXA menu shell. */
public final class NexaUi {
    public static final int SURFACE_0 = 0xFF061019;
    public static final int SURFACE_1 = 0xEE0B1724;
    public static final int SURFACE_2 = 0xF0122232;
    public static final int SURFACE_3 = 0xF01A3044;
    public static final int BORDER = 0x443C607A;
    public static final int BORDER_HOVER = 0xCC63C6FF;
    public static final int PRIMARY = 0xFF52C7FF;
    public static final int PRIMARY_SOFT = 0x334BBEFF;
    public static final int TEXT = 0xFFF5FAFF;
    public static final int TEXT_2 = 0xFFB7C8D7;
    public static final int TEXT_3 = 0xFF7890A4;
    public static final int SUCCESS = 0xFF63DEAF;
    public static final int WARNING = 0xFFF0B45E;

    private NexaUi() { }

    public static void roundedRect(DrawContext context, int x, int y, int w, int h, int radius, int color) {
        int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
        context.fill(x + r, y, x + w - r, y + h, color);
        context.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            int inset = cornerInset(r, i);
            context.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            context.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    public static void panel(DrawContext context, int x, int y, int w, int h) {
        roundedRect(context, x, y, w, h, 10, 0xA81A2C3C);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 9, SURFACE_1);
    }

    public static void borderedCard(DrawContext context, int x, int y, int w, int h, boolean hover) {
        roundedRect(context, x, y, w, h, 9, hover ? BORDER_HOVER : BORDER);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 8, hover ? SURFACE_3 : SURFACE_2);
    }

    public static void button(DrawContext context, TextRenderer text, int x, int y, int w, int h,
                              String label, boolean hover, boolean primary) {
        int border = primary ? (hover ? 0xFFFFFFFF : PRIMARY) : (hover ? BORDER_HOVER : BORDER);
        int fill = primary ? (hover ? 0xFF77D3FF : 0xFF329ED7) : (hover ? SURFACE_3 : SURFACE_2);
        roundedRect(context, x, y, w, h, 7, border);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 6, fill);
        context.drawCenteredTextWithShadow(text, label, x + w / 2, y + (h - 8) / 2,
            primary ? 0xFF03131D : TEXT);
    }

    public static void toggle(DrawContext context, int x, int y, boolean enabled, boolean available, boolean hover) {
        int track = !available ? 0xFF45515C : enabled ? PRIMARY : 0xFF354858;
        roundedRect(context, x, y, 34, 18, 9, hover ? brighten(track, 18) : track);
        int knobX = enabled ? x + 18 : x + 2;
        roundedRect(context, knobX, y + 2, 14, 14, 7, available ? 0xFFF5FAFF : 0xFF9CAAB5);
    }

    public static void pill(DrawContext context, TextRenderer text, int x, int y, String label, boolean active) {
        int w = text.getWidth(label) + 16;
        roundedRect(context, x, y, w, 20, 10, active ? PRIMARY_SOFT : 0x55233546);
        context.drawTextWithShadow(text, label, x + 8, y + 6, active ? PRIMARY : TEXT_2);
    }

    public static void sectionLabel(DrawContext context, TextRenderer text, String label, int x, int y) {
        context.drawTextWithShadow(text, label.toUpperCase(), x, y, TEXT_3);
    }

    public static String abbreviate(TextRenderer text, String value, int maximumWidth) {
        if (text.getWidth(value) <= maximumWidth) return value;
        String suffix = "...";
        int index = value.length();
        while (index > 0 && text.getWidth(value.substring(0, index) + suffix) > maximumWidth) index--;
        return value.substring(0, index) + suffix;
    }

    public static void backdrop(DrawContext context, int width, int height) {
        HudCompat.drawBrandBackground(context, width, height);
        context.fillGradient(0, 0, width, height, 0x9C091E31, 0xED050A11);
        context.fillGradient(0, 0, width, height / 3, 0x480E7DB8, 0x00000000);
        context.fill(width * 2 / 3, 0, width, height, 0x141B6B9C);
        context.fill(0, height * 4 / 5, width, height, 0x38020712);
    }

    private static int brighten(int color, int amount) {
        int a = color >>> 24;
        int r = Math.min(255, ((color >>> 16) & 255) + amount);
        int g = Math.min(255, ((color >>> 8) & 255) + amount);
        int b = Math.min(255, (color & 255) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int cornerInset(int radius, int row) {
        double dy = radius - row - .5;
        return Math.max(0, radius - (int) Math.sqrt(Math.max(0, radius * radius - dy * dy)));
    }
}
