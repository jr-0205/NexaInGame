package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.compat.HudCompat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Shared premium-client visual language for all supported Minecraft targets. */
public final class NexaUi {
    public static final int SURFACE_0 = 0xFF090B0F;
    public static final int SURFACE_1 = 0xF010141A;
    public static final int SURFACE_2 = 0xF0151A22;
    public static final int SURFACE_3 = 0xF01B222C;
    public static final int BORDER = 0xFF252C37;
    public static final int BORDER_HOVER = 0xFF3C4655;
    public static final int PRIMARY = 0xFF438BFF;
    public static final int PRIMARY_HOVER = 0xFF639FFF;
    public static final int PRIMARY_SOFT = 0x33438BFF;
    public static final int TEXT = 0xFFF5F7FA;
    public static final int TEXT_2 = 0xFFA5ADB8;
    public static final int TEXT_3 = 0xFF68717D;
    public static final int SUCCESS = 0xFF37C978;
    public static final int WARNING = 0xFFF5B94C;
    public static final int DANGER = 0xFFE45162;

    private NexaUi() { }

    public static void roundedRect(DrawContext context, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
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
        roundedRect(context, x + 2, y + 3, w, h, 11, 0x44000000);
        roundedRect(context, x, y, w, h, 11, BORDER);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 10, SURFACE_1);
    }

    public static void borderedCard(DrawContext context, int x, int y, int w, int h, boolean hover) {
        roundedRect(context, x, y, w, h, 10, hover ? BORDER_HOVER : BORDER);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 9, hover ? SURFACE_3 : SURFACE_2);
    }

    public static void moduleCard(DrawContext context, int x, int y, int w, int h,
                                  boolean hover, boolean selected, boolean enabled) {
        int border = selected ? PRIMARY : hover ? BORDER_HOVER : BORDER;
        int fill = selected ? 0xF0182432 : hover ? SURFACE_3 : SURFACE_2;
        roundedRect(context, x, y, w, h, 10, border);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 9, fill);
        if (enabled) roundedRect(context, x + 1, y + h - 5, w - 2, 4, 2, PRIMARY);
    }

    public static void button(DrawContext context, TextRenderer text, int x, int y, int w, int h,
                              String label, boolean hover, boolean primary) {
        int border = primary ? (hover ? PRIMARY_HOVER : PRIMARY) : (hover ? BORDER_HOVER : BORDER);
        int fill = primary ? (hover ? PRIMARY_HOVER : PRIMARY) : (hover ? SURFACE_3 : SURFACE_2);
        roundedRect(context, x, y, w, h, 7, border);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 6, fill);
        context.drawCenteredTextWithShadow(text, label, x + w / 2, y + (h - 8) / 2,
            primary ? 0xFFFFFFFF : TEXT);
    }

    public static void toggle(DrawContext context, int x, int y, boolean enabled, boolean available, boolean hover) {
        int track = !available ? 0xFF303640 : enabled ? PRIMARY : 0xFF323944;
        if (hover && available) track = enabled ? PRIMARY_HOVER : 0xFF414B58;
        roundedRect(context, x, y, 36, 20, 10, track);
        int knobX = enabled ? x + 18 : x + 2;
        roundedRect(context, knobX, y + 2, 16, 16, 8, available ? 0xFFF5F7FA : 0xFF7A828D);
    }

    public static void pill(DrawContext context, TextRenderer text, int x, int y, String label, boolean active) {
        int w = text.getWidth(label) + 16;
        roundedRect(context, x, y, w, 20, 10, active ? PRIMARY_SOFT : 0x55252C37);
        context.drawTextWithShadow(text, label, x + 8, y + 6, active ? PRIMARY : TEXT_2);
    }

    public static void navItem(DrawContext context, TextRenderer text, int x, int y, int w,
                               String label, boolean active, boolean hover) {
        if (active || hover) roundedRect(context, x, y, w, 28, 7, active ? PRIMARY_SOFT : 0x551B222C);
        if (active) roundedRect(context, x, y + 6, 3, 16, 1, PRIMARY);
        context.drawTextWithShadow(text, label, x + 14, y + 10, active ? TEXT : TEXT_2);
    }

    public static void sectionLabel(DrawContext context, TextRenderer text, String label, int x, int y) {
        context.drawTextWithShadow(text, label.toUpperCase(), x, y, TEXT_3);
    }

    public static void divider(DrawContext context, int x, int y, int w) {
        context.fill(x, y, x + w, y + 1, BORDER);
    }

    public static String abbreviate(TextRenderer text, String value, int maximumWidth) {
        if (value == null || maximumWidth <= 0) return "";
        if (text.getWidth(value) <= maximumWidth) return value;
        String suffix = "...";
        if (text.getWidth(suffix) > maximumWidth) return "";
        int index = value.length();
        while (index > 0 && text.getWidth(value.substring(0, index) + suffix) > maximumWidth) index--;
        return value.substring(0, index) + suffix;
    }

    public static void backdrop(DrawContext context, int width, int height) {
        HudCompat.drawBrandBackground(context, width, height);
        context.fillGradient(0, 0, width, height, 0x66000000, 0xD0080A0E);
    }

    private static int cornerInset(int radius, int row) {
        double dy = radius - row - .5;
        return Math.max(0, radius - (int) Math.sqrt(Math.max(0, radius * radius - dy * dy)));
    }
}
