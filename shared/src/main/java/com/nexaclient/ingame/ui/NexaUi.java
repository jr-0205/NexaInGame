package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.compat.HudCompat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Shared NEXA UI V2 visual language for all supported Minecraft targets. */
public final class NexaUi {
    public static final int BG = 0xFF080A0D;
    public static final int SURFACE_0 = 0xFF0B0E13;
    public static final int SURFACE_1 = 0xF0101319;
    public static final int SURFACE_2 = 0xF015181F;
    public static final int SURFACE_3 = 0xF01C2028;
    public static final int SURFACE_4 = 0xF0232832;
    public static final int BORDER = 0xFF292F39;
    public static final int BORDER_HOVER = 0xFF404956;
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

    public static void window(DrawContext c, int x, int y, int w, int h) {
        roundedRect(c, x + 3, y + 5, w, h, 14, 0x66000000);
        roundedRect(c, x, y, w, h, 14, 0xF10A0D12);
        roundedRect(c, x + 1, y + 1, w - 2, h - 2, 13, SURFACE_0);
    }

    public static void panel(DrawContext context, int x, int y, int w, int h) {
        roundedRect(context, x + 2, y + 3, w, h, 11, 0x44000000);
        roundedRect(context, x, y, w, h, 11, BORDER);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 10, SURFACE_1);
    }

    public static void softPanel(DrawContext c, int x, int y, int w, int h) {
        roundedRect(c, x, y, w, h, 10, SURFACE_1);
    }

    public static void borderedCard(DrawContext context, int x, int y, int w, int h, boolean hover) {
        roundedRect(context, x, y, w, h, 10, hover ? BORDER_HOVER : BORDER);
        roundedRect(context, x + 1, y + 1, w - 2, h - 2, 9, hover ? SURFACE_3 : SURFACE_2);
    }

    public static void moduleCard(DrawContext context, int x, int y, int w, int h,
                                  boolean hover, boolean selected, boolean enabled) {
        int fill = selected ? 0xFF202633 : hover ? SURFACE_3 : SURFACE_2;
        int border = selected ? PRIMARY : hover ? BORDER_HOVER : 0x00292F39;
        if (selected || hover) {
            roundedRect(context, x, y, w, h, 10, border);
            roundedRect(context, x + 1, y + 1, w - 2, h - 2, 9, fill);
        } else {
            roundedRect(context, x, y, w, h, 10, fill);
        }
        if (enabled) roundedRect(context, x + 8, y + h - 5, w - 16, 3, 2, PRIMARY);
    }

    public static void moduleIcon(DrawContext c, TextRenderer text, int x, int y, String glyph, boolean enabled, boolean hover) {
        roundedRect(c, x, y, 34, 34, 9, enabled ? PRIMARY_SOFT : hover ? SURFACE_4 : 0xFF20242C);
        c.drawCenteredTextWithShadow(text, glyph, x + 17, y + 13, enabled ? PRIMARY : hover ? TEXT : TEXT_2);
    }

    public static void button(DrawContext context, TextRenderer text, int x, int y, int w, int h,
                              String label, boolean hover, boolean primary) {
        int fill = primary ? (hover ? PRIMARY_HOVER : PRIMARY) : (hover ? SURFACE_4 : SURFACE_2);
        roundedRect(context, x, y, w, h, 7, fill);
        context.drawCenteredTextWithShadow(text, label, x + w / 2, y + (h - 8) / 2,
            primary ? 0xFFFFFFFF : TEXT);
    }

    public static void iconButton(DrawContext c, TextRenderer text, int x, int y, int size, String glyph, boolean hover, boolean active) {
        roundedRect(c, x, y, size, size, 7, active ? PRIMARY_SOFT : hover ? SURFACE_4 : SURFACE_2);
        c.drawCenteredTextWithShadow(text, glyph, x + size / 2, y + (size - 8) / 2, active ? PRIMARY : TEXT_2);
    }

    public static void toggle(DrawContext context, int x, int y, boolean enabled, boolean available, boolean hover) {
        int track = !available ? 0xFF303640 : enabled ? PRIMARY : 0xFF323944;
        if (hover && available) track = enabled ? PRIMARY_HOVER : 0xFF414B58;
        roundedRect(context, x, y, 34, 18, 9, track);
        int knobX = enabled ? x + 18 : x + 2;
        roundedRect(context, knobX, y + 2, 14, 14, 7, available ? 0xFFF5F7FA : 0xFF7A828D);
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

    public static void search(DrawContext c, TextRenderer text, int x, int y, int w, boolean hover) {
        roundedRect(c, x, y, w, 28, 8, hover ? SURFACE_4 : SURFACE_2);
        c.drawTextWithShadow(text, "⌕", x + 10, y + 10, TEXT_3);
        c.drawTextWithShadow(text, "Buscar modulos...", x + 28, y + 10, TEXT_3);
    }

    public static void sectionLabel(DrawContext context, TextRenderer text, String label, int x, int y) {
        context.drawTextWithShadow(text, label.toUpperCase(), x, y, TEXT_3);
    }

    public static void divider(DrawContext context, int x, int y, int w) {
        context.fill(x, y, x + w, y + 1, BORDER);
    }

    public static void toolbar(DrawContext c, int x, int y, int w, int h) {
        roundedRect(c, x + 2, y + 3, w, h, 10, 0x55000000);
        roundedRect(c, x, y, w, h, 10, 0xF0181C23);
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
        context.fillGradient(0, 0, width, height, 0x6A000000, 0xC9080A0E);
    }

    private static int cornerInset(int radius, int row) {
        double dy = radius - row - .5;
        return Math.max(0, radius - (int) Math.sqrt(Math.max(0, radius * radius - dy * dy)));
    }
}
