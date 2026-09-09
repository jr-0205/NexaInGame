package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.compat.HudCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public abstract class NexaHomeScreen extends Screen {
    private int contentX;
    private int contentY;
    private boolean compact;

    protected NexaHomeScreen() { super(Text.literal("NEXA · Tu universo")); }
    protected abstract Screen modulesScreen();
    protected abstract Screen editorScreen();

    @Override protected void init() {
        compact = height < 300;
        int contentWidth = Math.min(264, width - 40);
        contentX = width >= 560 ? Math.max(28, width / 10) : (width - contentWidth) / 2;
        contentY = Math.max(40, (height - (compact ? 186 : 234)) / 2);
        int actionY = contentY + (compact ? 65 : 103);
        int actionHeight = compact ? 30 : 36;
        addDrawableChild(new MenuButton(contentX, actionY, contentWidth, actionHeight,
            "Un jugador", "Explora tus mundos", true, button -> client.setScreen(new SelectWorldScreen(this))));
        addDrawableChild(new MenuButton(contentX, actionY + actionHeight + 6, contentWidth, actionHeight,
            "Multijugador", "Conecta con tu comunidad", false, button -> client.setScreen(new MultiplayerScreen(this))));
        int half = (contentWidth - 6) / 2;
        int toolsY = actionY + (actionHeight + 6) * 2;
        addDrawableChild(new MenuButton(contentX, toolsY, half, 26,
            "Módulos", "", false, button -> client.setScreen(modulesScreen())));
        addDrawableChild(new MenuButton(contentX + half + 6, toolsY, contentWidth - half - 6, 26,
            "Editar HUD", "", false, button -> client.setScreen(editorScreen())));
        addDrawableChild(new MenuButton(width - 116, 12, 64, 22,
            "Ajustes", "", false, button -> client.setScreen(new OptionsScreen(this, client.options))));
        addDrawableChild(new MenuButton(width - 46, 12, 34, 22,
            "Salir", "", false, button -> client.scheduleStop()));
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        NexaUi.backdrop(context, width, height);
        context.fillGradient(0, 0, width, 42, 0xA0050B14, 0x00050B14);
        context.drawTextWithShadow(textRenderer, "N / CLIENT", 16, 19, NexaUi.TEXT_2);
        context.fill(contentX, contentY, contentX + 18, contentY + 2, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, "TU UNIVERSO. TUS REGLAS.", contentX + 25, contentY - 3, NexaUi.TEXT_2);
        HudCompat.push(context, contentX - 2, contentY + 12, compact ? 3.4f : 5f);
        context.drawText(textRenderer, "NEXA", 0, 0, NexaUi.TEXT, false);
        HudCompat.pop(context);
        context.drawTextWithShadow(textRenderer, "Entra. Explora. Hazlo tuyo.", contentX, contentY + (compact ? 48 : 72), NexaUi.TEXT_2);
        if (width >= 640 && height >= 300) {
            int infoX = width - 204;
            int infoY = height - 94;
            context.fill(infoX, infoY, infoX + 2, infoY + 40, NexaUi.PRIMARY);
            context.drawTextWithShadow(textRenderer, "HECHO A TU MEDIDA", infoX + 12, infoY + 2, NexaUi.PRIMARY);
            context.drawTextWithShadow(textRenderer, "Tu HUD, tus módulos, tu perfil.", infoX + 12, infoY + 18, NexaUi.TEXT);
            context.drawTextWithShadow(textRenderer, "Todo comienza aquí.", infoX + 12, infoY + 32, NexaUi.TEXT_2);
        }
        String user = client == null ? "Jugador" : client.getSession().getUsername();
        context.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, user, width / 2 - 24), 16, height - 17, NexaUi.TEXT_2);
        String version = "NEXA / ALPHA.2";
        context.drawTextWithShadow(textRenderer, version, width - textRenderer.getWidth(version) - 16, height - 17, NexaUi.TEXT_3);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean shouldPause() { return false; }

    private static final class MenuButton extends ButtonWidget {
        private final String detail;
        private final boolean primary;

        private MenuButton(int x, int y, int width, int height, String label, String detail,
                           boolean primary, PressAction action) {
            super(x, y, width, height, Text.literal(label), action, DEFAULT_NARRATION_SUPPLIER);
            this.detail = detail;
            this.primary = primary;
        }

        @Override protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            var text = MinecraftClient.getInstance().textRenderer;
            boolean highlighted = isHovered() || isFocused();
            int border = highlighted ? NexaUi.TEXT : primary ? NexaUi.PRIMARY : 0x80627A8B;
            int fill = primary ? (highlighted ? 0xFF94E5FA : 0xFF6BD3EB) : (highlighted ? 0xF023394B : 0xDA0D1B29);
            NexaUi.roundedRect(context, getX(), getY(), width, height, 6, border);
            NexaUi.roundedRect(context, getX() + 1, getY() + 1, width - 2, height - 2, 5, fill);
            int color = primary ? 0xFF08222D : NexaUi.TEXT;
            if (detail.isEmpty()) {
                context.drawCenteredTextWithShadow(text, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, color);
            } else {
                context.drawText(text, getMessage(), getX() + 12, getY() + (height >= 36 ? 7 : 5), color, false);
                context.drawText(text, detail, getX() + 12, getY() + (height >= 36 ? 21 : 17), primary ? 0xFF214A58 : NexaUi.TEXT_2, false);
                context.drawText(text, ">", getX() + width - 18, getY() + (height - 8) / 2, color, false);
            }
        }
    }
}
