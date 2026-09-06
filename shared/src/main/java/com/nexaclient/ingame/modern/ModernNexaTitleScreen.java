package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

public final class ModernNexaTitleScreen extends Screen {
    private static final String[] ACTIONS = { "JUGAR SOLO", "MULTIJUGADOR", "MODULOS", "AJUSTES" };
    private int actionX, actionY, actionW;

    public ModernNexaTitleScreen() { super(Text.literal("NEXA Client")); }

    @Override protected void init() {
        actionW = Math.min(350, Math.max(250, width - 56));
        actionX = width >= 840 ? width / 2 - actionW / 2 + 120 : width / 2 - actionW / 2;
        actionY = Math.max(148, height / 2 - 82);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        NexaUi.backdrop(context, width, height);
        context.fill(0, 0, width, 64, 0xD6081420); context.fill(0, 63, width, 64, 0x6657C8FF);
        NexaUi.roundedRect(context, 22, 18, 28, 28, 8, NexaUi.PRIMARY);
        context.drawCenteredTextWithShadow(textRenderer, "N", 36, 28, 0xFF04131D);
        context.drawTextWithShadow(textRenderer, "NEXA", 62, 24, NexaUi.TEXT); context.drawTextWithShadow(textRenderer, "CLIENT", 101, 24, NexaUi.PRIMARY);
        String user = client == null ? "PLAYER" : client.getSession().getUsername(); int userWidth = textRenderer.getWidth(user) + 38;
        NexaUi.roundedRect(context, width - userWidth - 22, 21, userWidth, 22, 11, 0x44265D81);
        context.drawTextWithShadow(textRenderer, user, width - userWidth - 10, 28, NexaUi.TEXT_2);
        if (width >= 840) drawBrand(context);
        NexaUi.panel(context, actionX - 18, actionY - 46, actionW + 36, 254);
        NexaUi.sectionLabel(context, textRenderer, "NEXA LAUNCHPAD", actionX, actionY - 30);
        context.drawTextWithShadow(textRenderer, "Elige una experiencia para comenzar", actionX, actionY - 15, NexaUi.TEXT_2);
        for (int index = 0; index < ACTIONS.length; index++) drawAction(context, mouseX, mouseY, index);
        context.drawTextWithShadow(textRenderer, "NEXA IN-GAME  |  ALPHA.2  |  FABRIC", 22, height - 23, NexaUi.TEXT_3);
        String shortcut = "SHIFT DERECHO  CONTROL CENTER";
        context.drawTextWithShadow(textRenderer, shortcut, width - textRenderer.getWidth(shortcut) - 22, height - 23, NexaUi.TEXT_3);
    }

    private void drawBrand(DrawContext context) {
        int x = Math.max(52, width / 2 - 430), y = height / 2 - 84;
        NexaUi.sectionLabel(context, textRenderer, "TU ESPACIO DE JUEGO", x, y - 24);
        context.drawTextWithShadow(textRenderer, "JUEGA", x, y, NexaUi.TEXT); context.drawTextWithShadow(textRenderer, " A TU MANERA", x + 36, y, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, "Un cliente modular, ligero y personalizable", x, y + 24, NexaUi.TEXT_2);
        context.drawTextWithShadow(textRenderer, "para cada mundo y cada servidor.", x, y + 38, NexaUi.TEXT_3);
        NexaUi.pill(context, textRenderer, x, y + 66, "HUD MODULAR", true); NexaUi.pill(context, textRenderer, x + 108, y + 66, "3 VERSIONES", false);
    }

    private void drawAction(DrawContext context, int mx, int my, int index) {
        int y = actionY + index * 42; boolean hover = inside(mx, my, actionX, y, actionW, 34); boolean primary = index == 0;
        NexaUi.borderedCard(context, actionX, y, actionW, 34, hover); if (primary) context.fill(actionX, y, actionX + 3, y + 34, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, ACTIONS[index], actionX + 16, y + 13, primary ? NexaUi.TEXT : NexaUi.TEXT_2);
        String detail = switch (index) { case 0 -> "Mundos locales"; case 1 -> "Servidores y amigos"; case 2 -> "Personalizar cliente"; default -> "Minecraft y accesibilidad"; };
        context.drawTextWithShadow(textRenderer, detail, actionX + actionW - textRenderer.getWidth(detail) - 16, y + 13, hover ? NexaUi.PRIMARY : NexaUi.TEXT_3);
    }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0 || client == null) return super.mouseClicked(click, doubled);
        for (int index = 0; index < ACTIONS.length; index++) {
            if (!inside(click.x(), click.y(), actionX, actionY + index * 42, actionW, 34)) continue;
            switch (index) { case 0 -> client.setScreen(new SelectWorldScreen(this)); case 1 -> client.setScreen(new MultiplayerScreen(this)); case 2 -> client.setScreen(new ModernNexaControlCenterScreen(this, ModernNexaInGameClient.MODULES)); case 3 -> client.setScreen(new OptionsScreen(this, client.options)); default -> { } }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    @Override public boolean shouldPause() { return false; }
}
