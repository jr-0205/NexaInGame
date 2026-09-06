package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.hud.HudRenderer;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.NexaModule;
import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModernNexaHudEditorScreen extends Screen {
    private static final int SIDEBAR = 230;
    private final Screen parent; private final ModuleRegistry modules;
    private final Map<NexaModule, HudRenderer.Bounds> bounds = new LinkedHashMap<>();
    private NexaModule dragging, focused; private double dragOffsetX, dragOffsetY;
    public ModernNexaHudEditorScreen(Screen parent, ModuleRegistry modules) { super(Text.literal("NEXA HUD Editor")); this.parent = parent; this.modules = modules; }

    @Override public void render(DrawContext c, int mx, int my, float delta) {
        NexaUi.backdrop(c, width, height); c.fill(0, 0, width, height, 0x8A02070D);
        for (int x = SIDEBAR + 18; x < width; x += 32) c.fill(x, 0, x + 1, height, 0x173D89B9); for (int y = 0; y < height; y += 32) c.fill(SIDEBAR, y, width, y + 1, 0x173D89B9);
        c.fill(width / 2, 0, width / 2 + 1, height, 0x8867CBFF); c.fill(SIDEBAR, height / 2, width, height / 2 + 1, 0x8867CBFF);
        bounds.clear(); if (client != null) for (NexaModule module : ModuleRegistry.MODULES) { NexaConfig.ModuleConfig state = modules.state(module); if (!state.enabled || !module.editableHud()) continue; HudRenderer.Bounds box = HudRenderer.renderModule(c, client, modules, module, state, true); bounds.put(module, box); boolean active = module == dragging || module == focused || box.contains(mx, my); c.drawStrokedRectangle(box.x() - 2, box.y() - 2, box.width() + 4, box.height() + 4, active ? NexaUi.PRIMARY : 0x77859FB2); }
        drawSidebar(c, mx, my);
    }

    private void drawSidebar(DrawContext c, int mx, int my) {
        c.fill(0, 0, SIDEBAR, height, 0xEE081522); c.fill(SIDEBAR - 1, 0, SIDEBAR, height, 0x7754C7FF); NexaUi.roundedRect(c, 16, 17, 28, 28, 8, NexaUi.PRIMARY); c.drawCenteredTextWithShadow(textRenderer, "N", 30, 27, 0xFF06131D); c.drawTextWithShadow(textRenderer, "HUD LAYOUT", 56, 22, NexaUi.TEXT); c.drawTextWithShadow(textRenderer, "Organiza tu interfaz", 56, 35, NexaUi.TEXT_3);
        NexaUi.sectionLabel(c, textRenderer, "CONTROLES", 16, 70); instruction(c, "1", "Arrastra", "Mueve un componente", 16, 84); instruction(c, "2", "Rueda", "Cambia su escala", 16, 119); instruction(c, "3", "Shift + rueda", "Ajusta opacidad", 16, 154); NexaUi.sectionLabel(c, textRenderer, "COMPONENTES ACTIVOS", 16, 204);
        int y = 218; for (Map.Entry<NexaModule, HudRenderer.Bounds> entry : bounds.entrySet()) { NexaModule module = entry.getKey(); boolean hover = inside(mx, my, 12, y, SIDEBAR - 24, 26); if (module == focused || hover) NexaUi.roundedRect(c, 12, y, SIDEBAR - 24, 26, 6, module == focused ? 0x664ABEFF : 0x332B4658); c.drawTextWithShadow(textRenderer, module.name(), 22, y + 9, module == focused ? NexaUi.TEXT : NexaUi.TEXT_2); String size = Math.round(modules.state(module).scale * 100) + "%"; c.drawTextWithShadow(textRenderer, size, SIDEBAR - textRenderer.getWidth(size) - 20, y + 9, NexaUi.TEXT_3); y += 30; if (y > height - 88) break; }
        if (focused != null) { int resetY = height - 58; NexaUi.button(c, textRenderer, 16, resetY, SIDEBAR - 32, 30, "RESTABLECER " + focused.name().toUpperCase(), inside(mx, my, 16, resetY, SIDEBAR - 32, 30), false); } c.drawTextWithShadow(textRenderer, "ESC  guardar y volver", 16, height - 18, NexaUi.TEXT_3);
    }

    private void instruction(DrawContext c, String number, String title, String detail, int x, int y) { NexaUi.roundedRect(c, x, y, 20, 20, 6, 0x443BBEFF); c.drawCenteredTextWithShadow(textRenderer, number, x + 10, y + 6, NexaUi.PRIMARY); c.drawTextWithShadow(textRenderer, title, x + 30, y + 2, NexaUi.TEXT_2); c.drawTextWithShadow(textRenderer, detail, x + 30, y + 12, NexaUi.TEXT_3); }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y(); if (click.button() == 0 && focused != null && inside(mx, my, 16, height - 58, SIDEBAR - 32, 30)) { reset(focused); return true; }
        int listY = 218; if (click.button() == 0) for (NexaModule module : bounds.keySet()) { if (inside(mx, my, 12, listY, SIDEBAR - 24, 26)) { focused = module; return true; } listY += 30; }
        if (click.button() == 1) for (var entry : bounds.entrySet()) if (entry.getValue().contains(mx, my)) { focused = entry.getKey(); reset(focused); return true; }
        if (click.button() == 0) for (var entry : bounds.entrySet()) if (entry.getValue().contains(mx, my)) { dragging = entry.getKey(); focused = dragging; dragOffsetX = mx - entry.getValue().x(); dragOffsetY = my - entry.getValue().y(); return true; }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean mouseDragged(Click click, double deltaX, double deltaY) { if (dragging == null || click.button() != 0) return super.mouseDragged(click, deltaX, deltaY); var state = modules.state(dragging); var box = bounds.get(dragging); double px = click.x() - dragOffsetX, py = click.y() - dragOffsetY; if (Math.abs(px + box.width() / 2.0 - width / 2.0) < 6) px = (width - box.width()) / 2.0; if (Math.abs(py + box.height() / 2.0 - height / 2.0) < 6) py = (height - box.height()) / 2.0; px = Math.max(0, Math.min(width - box.width(), px)); py = Math.max(0, Math.min(height - box.height(), py)); state.x = clamp((float) (px / Math.max(1, width - box.width()))); state.y = clamp((float) (py / Math.max(1, height - box.height()))); return true; }
    @Override public boolean mouseReleased(Click click) { if (dragging != null) { dragging = null; modules.save(); return true; } return super.mouseReleased(click); }
    @Override public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) { for (var entry : bounds.entrySet()) if (entry.getValue().contains(mx, my)) { focused = entry.getKey(); var state = modules.state(focused); long window = client.getWindow().getHandle(); boolean shift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS; if (shift) state.opacity = Math.max(.15f, Math.min(1f, state.opacity + (vertical > 0 ? .05f : -.05f))); else state.scale = Math.max(.5f, Math.min(2f, state.scale + (vertical > 0 ? .1f : -.1f))); return true; } return super.mouseScrolled(mx, my, horizontal, vertical); }
    private void reset(NexaModule module) { var state = modules.state(module); state.x = module.defaultX(); state.y = module.defaultY(); state.scale = 1f; state.opacity = .92f; modules.save(); }
    private static float clamp(float value) { return Math.max(0f, Math.min(1f, value)); }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    @Override public void close() { modules.save(); if (client != null) client.setScreen(parent); }
    @Override public boolean shouldPause() { return false; }
}
