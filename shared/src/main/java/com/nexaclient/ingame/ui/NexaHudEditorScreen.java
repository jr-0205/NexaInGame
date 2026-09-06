package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.hud.HudRenderer;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.NexaModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public final class NexaHudEditorScreen extends Screen {
    private static final int SIDEBAR = 230;
    private final Screen parent;
    private final ModuleRegistry modules;
    private final Map<NexaModule, HudRenderer.Bounds> bounds = new LinkedHashMap<>();
    private NexaModule dragging;
    private NexaModule focused;
    private double dragOffsetX, dragOffsetY;

    public NexaHudEditorScreen(Screen parent, ModuleRegistry modules) {
        super(Text.literal("NEXA HUD Editor")); this.parent = parent; this.modules = modules;
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        NexaUi.backdrop(context, width, height);
        context.fill(0, 0, width, height, 0x8A02070D);
        drawGrid(context);
        bounds.clear();
        if (client != null) for (NexaModule module : ModuleRegistry.MODULES) {
            NexaConfig.ModuleConfig state = modules.state(module);
            if (!state.enabled || !module.editableHud()) continue;
            HudRenderer.Bounds box = HudRenderer.renderModule(context, client, modules, module, state, true);
            bounds.put(module, box);
            boolean active = module == dragging || module == focused || box.contains(mouseX, mouseY);
            context.drawBorder(box.x() - 2, box.y() - 2, box.width() + 4, box.height() + 4, active ? NexaUi.PRIMARY : 0x77859FB2);
        }
        drawSidebar(context, mouseX, mouseY);
    }

    private void drawGrid(DrawContext context) {
        for (int x = SIDEBAR + 18; x < width; x += 32) context.fill(x, 0, x + 1, height, 0x173D89B9);
        for (int y = 0; y < height; y += 32) context.fill(SIDEBAR, y, width, y + 1, 0x173D89B9);
        context.fill(width / 2, 0, width / 2 + 1, height, 0x8867CBFF);
        context.fill(SIDEBAR, height / 2, width, height / 2 + 1, 0x8867CBFF);
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY) {
        context.fill(0, 0, SIDEBAR, height, 0xEE081522);
        context.fill(SIDEBAR - 1, 0, SIDEBAR, height, 0x7754C7FF);
        NexaUi.roundedRect(context, 16, 17, 28, 28, 8, NexaUi.PRIMARY);
        context.drawCenteredTextWithShadow(textRenderer, "N", 30, 27, 0xFF06131D);
        context.drawTextWithShadow(textRenderer, "HUD LAYOUT", 56, 22, NexaUi.TEXT);
        context.drawTextWithShadow(textRenderer, "Organiza tu interfaz", 56, 35, NexaUi.TEXT_3);
        NexaUi.sectionLabel(context, textRenderer, "CONTROLES", 16, 70);
        instruction(context, "1", "Arrastra", "Mueve un componente", 16, 84);
        instruction(context, "2", "Rueda", "Cambia su escala", 16, 119);
        instruction(context, "3", "Shift + rueda", "Ajusta opacidad", 16, 154);

        NexaUi.sectionLabel(context, textRenderer, "COMPONENTES ACTIVOS", 16, 204);
        int y = 218;
        for (Map.Entry<NexaModule, HudRenderer.Bounds> entry : bounds.entrySet()) {
            NexaModule module = entry.getKey();
            boolean hover = inside(mouseX, mouseY, 12, y, SIDEBAR - 24, 26);
            if (module == focused || hover) NexaUi.roundedRect(context, 12, y, SIDEBAR - 24, 26, 6, module == focused ? 0x664ABEFF : 0x332B4658);
            context.drawTextWithShadow(textRenderer, module.name(), 22, y + 9, module == focused ? NexaUi.TEXT : NexaUi.TEXT_2);
            String size = Math.round(modules.state(module).scale * 100) + "%";
            context.drawTextWithShadow(textRenderer, size, SIDEBAR - textRenderer.getWidth(size) - 20, y + 9, NexaUi.TEXT_3);
            y += 30;
            if (y > height - 88) break;
        }
        if (focused != null) {
            int resetY = height - 58;
            boolean hover = inside(mouseX, mouseY, 16, resetY, SIDEBAR - 32, 30);
            NexaUi.button(context, textRenderer, 16, resetY, SIDEBAR - 32, 30, "RESTABLECER " + focused.name().toUpperCase(), hover, false);
        }
        context.drawTextWithShadow(textRenderer, "ESC  guardar y volver", 16, height - 18, NexaUi.TEXT_3);
    }

    private void instruction(DrawContext context, String number, String title, String detail, int x, int y) {
        NexaUi.roundedRect(context, x, y, 20, 20, 6, 0x443BBEFF);
        context.drawCenteredTextWithShadow(textRenderer, number, x + 10, y + 6, NexaUi.PRIMARY);
        context.drawTextWithShadow(textRenderer, title, x + 30, y + 2, NexaUi.TEXT_2);
        context.drawTextWithShadow(textRenderer, detail, x + 30, y + 12, NexaUi.TEXT_3);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && focused != null && inside(mouseX, mouseY, 16, height - 58, SIDEBAR - 32, 30)) { reset(focused); return true; }
        int listY = 218;
        if (button == 0) for (NexaModule module : bounds.keySet()) {
            if (inside(mouseX, mouseY, 12, listY, SIDEBAR - 24, 26)) { focused = module; return true; }
            listY += 30;
        }
        if (button == 1) for (var entry : bounds.entrySet()) if (entry.getValue().contains(mouseX, mouseY)) { focused = entry.getKey(); reset(focused); return true; }
        if (button == 0) for (var entry : bounds.entrySet()) if (entry.getValue().contains(mouseX, mouseY)) {
            dragging = entry.getKey(); focused = dragging; dragOffsetX = mouseX - entry.getValue().x(); dragOffsetY = mouseY - entry.getValue().y(); return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging == null || button != 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        var state = modules.state(dragging); var box = bounds.get(dragging); double px = mouseX - dragOffsetX, py = mouseY - dragOffsetY;
        if (Math.abs(px + box.width() / 2.0 - width / 2.0) < 6) px = (width - box.width()) / 2.0;
        if (Math.abs(py + box.height() / 2.0 - height / 2.0) < 6) py = (height - box.height()) / 2.0;
        px = Math.max(0, Math.min(width - box.width(), px)); py = Math.max(0, Math.min(height - box.height(), py));
        state.x = clamp((float) (px / Math.max(1, width - box.width()))); state.y = clamp((float) (py / Math.max(1, height - box.height()))); return true;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) { if (dragging != null) { dragging = null; modules.save(); return true; } return super.mouseReleased(mouseX, mouseY, button); }
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (var entry : bounds.entrySet()) if (entry.getValue().contains(mouseX, mouseY)) { focused = entry.getKey(); var state = modules.state(focused); long window = client.getWindow().getHandle(); boolean shift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS; if (shift) state.opacity = Math.max(.15f, Math.min(1f, state.opacity + (verticalAmount > 0 ? .05f : -.05f))); else state.scale = Math.max(.5f, Math.min(2f, state.scale + (verticalAmount > 0 ? .1f : -.1f))); return true; }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void reset(NexaModule module) { var state = modules.state(module); state.x = module.defaultX(); state.y = module.defaultY(); state.scale = 1f; state.opacity = .92f; modules.save(); }
    private static float clamp(float value) { return Math.max(0f, Math.min(1f, value)); }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    @Override public void close() { modules.save(); if (client != null) client.setScreen(parent); }
    @Override public boolean shouldPause() { return false; }
}
