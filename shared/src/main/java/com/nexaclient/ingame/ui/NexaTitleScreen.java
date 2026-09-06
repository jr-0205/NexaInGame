package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.NexaInGameClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

public final class NexaTitleScreen extends Screen {
    private static final String[] ACTIONS = { "JUGAR SOLO", "MULTIJUGADOR", "MODULOS", "AJUSTES" };
    private int actionX;
    private int actionY;
    private int actionW;

    public NexaTitleScreen() { super(Text.literal("NEXA Client")); }

    @Override protected void init() {
        actionW = Math.min(350, Math.max(250, width - 56));
        actionX = width >= 840 ? width / 2 - actionW / 2 + 120 : width / 2 - actionW / 2;
        actionY = Math.max(148, height / 2 - 82);
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        NexaUi.backdrop(context, width, height);
        drawTopBar(context);

        if (width >= 840) drawBrandStatement(context);

        int panelX = actionX - 18;
        int panelY = actionY - 46;
        NexaUi.panel(context, panelX, panelY, actionW + 36, 254);
        NexaUi.sectionLabel(context, textRenderer, "NEXA LAUNCHPAD", actionX, actionY - 30);
        context.drawTextWithShadow(textRenderer, "Elige una experiencia para comenzar", actionX, actionY - 15, NexaUi.TEXT_2);

        for (int index = 0; index < ACTIONS.length; index++) drawAction(context, mouseX, mouseY, index);

        String build = "NEXA IN-GAME  |  ALPHA.2  |  FABRIC";
        context.drawTextWithShadow(textRenderer, build, 22, height - 23, NexaUi.TEXT_3);
        context.drawTextWithShadow(textRenderer, "SHIFT DERECHO  CONTROL CENTER", width - textRenderer.getWidth("SHIFT DERECHO  CONTROL CENTER") - 22,
            height - 23, NexaUi.TEXT_3);
    }

    private void drawTopBar(DrawContext context) {
        context.fill(0, 0, width, 64, 0xD6081420);
        context.fill(0, 63, width, 64, 0x554ABEFF);
        NexaUi.roundedRect(context, 22, 18, 28, 28, 8, NexaUi.PRIMARY);
        context.drawCenteredTextWithShadow(textRenderer, "N", 36, 28, 0xFF04131D);
        context.drawTextWithShadow(textRenderer, "NEXA", 62, 24, NexaUi.TEXT);
        context.drawTextWithShadow(textRenderer, "CLIENT", 101, 24, NexaUi.PRIMARY);
        String user = client == null ? "PLAYER" : client.getSession().getUsername();
        int userWidth = textRenderer.getWidth(user) + 38;
        NexaUi.roundedRect(context, width - userWidth - 22, 21, userWidth, 22, 11, 0x44265D81);
        context.drawTextWithShadow(textRenderer, user, width - userWidth - 10, 28, NexaUi.TEXT_2);
    }

    private void drawBrandStatement(DrawContext context) {
        int x = Math.max(52, width / 2 - 430);
        int y = height / 2 - 84;
        NexaUi.sectionLabel(context, textRenderer, "TU ESPACIO DE JUEGO", x, y - 24);
        context.drawTextWithShadow(textRenderer, "JUEGA", x, y, NexaUi.TEXT);
        context.drawTextWithShadow(textRenderer, " A TU MANERA", x + 36, y, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, "Un cliente modular, ligero y personalizable", x, y + 24, NexaUi.TEXT_2);
        context.drawTextWithShadow(textRenderer, "para cada mundo y cada servidor.", x, y + 38, NexaUi.TEXT_3);
        NexaUi.pill(context, textRenderer, x, y + 66, "HUD MODULAR", true);
        NexaUi.pill(context, textRenderer, x + 108, y + 66, "3 VERSIONES", false);
    }

    private void drawAction(DrawContext context, int mouseX, int mouseY, int index) {
        int y = actionY + index * 42;
        boolean hover = inside(mouseX, mouseY, actionX, y, actionW, 34);
        boolean primary = index == 0;
        NexaUi.borderedCard(context, actionX, y, actionW, 34, hover);
        if (primary) context.fill(actionX, y, actionX + 3, y + 34, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, ACTIONS[index], actionX + 16, y + 13,
            primary ? NexaUi.TEXT : NexaUi.TEXT_2);
        String detail = switch (index) {
            case 0 -> "Mundos locales";
            case 1 -> "Servidores y amigos";
            case 2 -> "Personalizar cliente";
            default -> "Minecraft y accesibilidad";
        };
        int detailX = actionX + actionW - textRenderer.getWidth(detail) - 16;
        context.drawTextWithShadow(textRenderer, detail, detailX, y + 13, hover ? NexaUi.PRIMARY : NexaUi.TEXT_3);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || client == null) return super.mouseClicked(mouseX, mouseY, button);
        for (int index = 0; index < ACTIONS.length; index++) {
            if (!inside(mouseX, mouseY, actionX, actionY + index * 42, actionW, 34)) continue;
            switch (index) {
                case 0 -> client.setScreen(new SelectWorldScreen(this));
                case 1 -> client.setScreen(new MultiplayerScreen(this));
                case 2 -> client.setScreen(new NexaControlCenterScreen(this, NexaInGameClient.MODULES));
                case 3 -> client.setScreen(new OptionsScreen(this, client.options));
                default -> { }
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override public boolean shouldPause() { return false; }
}
