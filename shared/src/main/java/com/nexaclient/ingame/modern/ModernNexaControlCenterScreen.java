package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.modules.ModuleCategory;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.ModuleSettings;
import com.nexaclient.ingame.modules.NexaModule;
import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModernNexaControlCenterScreen extends Screen {
    private final Screen parent;
    private final ModuleRegistry modules;
    private final Map<NexaModule, Rect> cards = new LinkedHashMap<>();
    private ModuleCategory category;
    private NexaModule selected;
    private int scroll, railWidth, detailWidth;

    public ModernNexaControlCenterScreen(Screen parent, ModuleRegistry modules) {
        super(Text.literal("NEXA Control Center")); this.parent = parent; this.modules = modules;
    }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        NexaUi.backdrop(context, width, height);
        railWidth = Math.min(190, Math.max(154, width / 5)); detailWidth = width >= 900 ? Math.min(290, width / 3) : 0;
        drawHeader(context, mouseX, mouseY); drawNavigation(context, mouseX, mouseY); drawCatalog(context, mouseX, mouseY);
        if (detailWidth > 0) drawDetails(context, mouseX, mouseY);
    }

    private void drawHeader(DrawContext c, int mx, int my) {
        c.fill(0, 0, width, 68, 0xEC081521); c.fill(0, 67, width, 68, 0x6657C8FF);
        NexaUi.roundedRect(c, 18, 19, 30, 30, 8, NexaUi.PRIMARY); c.drawCenteredTextWithShadow(textRenderer, "N", 33, 30, 0xFF03131D);
        c.drawTextWithShadow(textRenderer, "NEXA", 60, 26, NexaUi.TEXT); c.drawTextWithShadow(textRenderer, "CONTROL CENTER", 100, 26, NexaUi.PRIMARY);
        NexaUi.pill(c, textRenderer, width - 188, 24, "H  EDITOR HUD", inside(mx, my, width - 194, 17, 126, 31)); NexaUi.pill(c, textRenderer, width - 60, 24, "ESC", false);
    }

    private void drawNavigation(DrawContext c, int mx, int my) {
        c.fill(0, 68, railWidth, height, 0xD8091723); NexaUi.sectionLabel(c, textRenderer, "BIBLIOTECA", 16, 86);
        int y = 102; drawCategory(c, mx, my, y, null, "Todo"); y += 34;
        for (ModuleCategory value : ModuleCategory.values()) { drawCategory(c, mx, my, y, value, value.label); y += 34; }
        int profileY = height - 104; NexaUi.sectionLabel(c, textRenderer, "PERFIL ACTIVO", 16, profileY);
        boolean hover = inside(mx, my, 12, profileY + 12, railWidth - 24, 36); NexaUi.borderedCard(c, 12, profileY + 12, railWidth - 24, 36, hover);
        c.drawTextWithShadow(textRenderer, modules.config().activeProfile, 24, profileY + 25, NexaUi.TEXT); c.drawTextWithShadow(textRenderer, "CAMBIAR", railWidth - 70, profileY + 25, NexaUi.PRIMARY);
        c.drawTextWithShadow(textRenderer, "Los perfiles guardan modulos y HUD por separado", 16, height - 30, NexaUi.TEXT_3);
    }

    private void drawCategory(DrawContext c, int mx, int my, int y, ModuleCategory value, String label) {
        boolean active = category == value, hover = inside(mx, my, 10, y, railWidth - 20, 28);
        if (active || hover) NexaUi.roundedRect(c, 10, y, railWidth - 20, 28, 6, active ? 0x664ABEFF : 0x332A4356);
        if (active) c.fill(10, y + 5, 13, y + 23, NexaUi.PRIMARY); c.drawTextWithShadow(textRenderer, label, 23, y + 10, active ? NexaUi.TEXT : NexaUi.TEXT_2);
    }

    private void drawCatalog(DrawContext c, int mx, int my) {
        int x = railWidth + 22, right = width - detailWidth - 18, contentWidth = Math.max(180, right - x); List<NexaModule> visible = visible();
        if (selected == null || !visible.contains(selected)) selected = visible.isEmpty() ? null : visible.get(0);
        NexaUi.sectionLabel(c, textRenderer, category == null ? "MODULOS" : category.label, x, 87);
        int enabled = (int) visible.stream().filter(module -> modules.state(module).enabled).count(); c.drawTextWithShadow(textRenderer, enabled + " ACTIVOS  /  " + visible.size() + " DISPONIBLES", x, 102, NexaUi.TEXT_3);
        cards.clear(); int columns = contentWidth >= 520 ? 2 : 1, gap = 10, cardWidth = (contentWidth - gap * (columns - 1)) / columns, cardHeight = 78, start = Math.min(scroll, Math.max(0, visible.size() - 1));
        for (int index = start; index < visible.size(); index++) { int local = index - start, cardX = x + (local % columns) * (cardWidth + gap), cardY = 120 + (local / columns) * (cardHeight + gap); if (cardY + cardHeight > height - 18) break; NexaModule module = visible.get(index); cards.put(module, new Rect(cardX, cardY, cardWidth, cardHeight)); drawModuleCard(c, mx, my, module, cardX, cardY, cardWidth, cardHeight); }
    }

    private void drawModuleCard(DrawContext c, int mx, int my, NexaModule module, int x, int y, int w, int h) {
        boolean hover = inside(mx, my, x, y, w, h), active = module == selected; NexaUi.borderedCard(c, x, y, w, h, hover || active); if (active) c.fill(x, y + 10, x + 3, y + h - 10, NexaUi.PRIMARY);
        NexaConfig.ModuleConfig state = modules.state(module); NexaUi.roundedRect(c, x + 13, y + 13, 28, 28, 8, module.implemented() ? 0x443EC7FF : 0x443F5360);
        c.drawCenteredTextWithShadow(textRenderer, module.name().substring(0, 1).toUpperCase(), x + 27, y + 23, module.implemented() ? NexaUi.PRIMARY : NexaUi.TEXT_3);
        c.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, module.name(), w - 112), x + 51, y + 15, NexaUi.TEXT); c.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, module.description(), w - 70), x + 51, y + 31, NexaUi.TEXT_3);
        String status = module.implemented() ? ModuleSettings.summary(module, state) : "PROXIMAMENTE"; c.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, status, w - 72), x + 13, y + 58, module.implemented() ? NexaUi.TEXT_2 : NexaUi.WARNING);
        int toggleX = x + w - 47; NexaUi.toggle(c, toggleX, y + 15, state.enabled, module.implemented(), inside(mx, my, toggleX, y + 15, 34, 18));
    }

    private void drawDetails(DrawContext c, int mx, int my) {
        int x = width - detailWidth; c.fill(x, 68, width, height, 0xD9091723); NexaUi.sectionLabel(c, textRenderer, "CONFIGURACION", x + 18, 87); if (selected == null) return;
        NexaConfig.ModuleConfig state = modules.state(selected); NexaUi.panel(c, x + 14, 103, detailWidth - 28, 256); c.drawTextWithShadow(textRenderer, selected.name(), x + 28, 122, NexaUi.TEXT); c.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, selected.description(), detailWidth - 56), x + 28, 140, NexaUi.TEXT_3);
        NexaUi.pill(c, textRenderer, x + 28, 162, selected.implemented() ? "LISTO PARA USAR" : "EN DESARROLLO", selected.implemented()); NexaUi.sectionLabel(c, textRenderer, "ESTADO", x + 28, 198);
        boolean toggleHover = inside(mx, my, x + 28, 210, detailWidth - 56, 34); NexaUi.borderedCard(c, x + 28, 210, detailWidth - 56, 34, toggleHover); c.drawTextWithShadow(textRenderer, state.enabled ? "ACTIVADO" : "DESACTIVADO", x + 40, 223, state.enabled ? NexaUi.SUCCESS : NexaUi.TEXT_2); NexaUi.toggle(c, x + detailWidth - 76, 218, state.enabled, selected.implemented(), toggleHover);
        NexaUi.sectionLabel(c, textRenderer, "AJUSTE RAPIDO", x + 28, 261); boolean settingHover = inside(mx, my, x + 28, 273, detailWidth - 56, 34); NexaUi.button(c, textRenderer, x + 28, 273, detailWidth - 56, 34, ModuleSettings.summary(selected, state), settingHover, false);
        if (selected.editableHud()) { boolean editorHover = inside(mx, my, x + 28, 320, detailWidth - 56, 28); NexaUi.button(c, textRenderer, x + 28, 320, detailWidth - 56, 28, "ABRIR EDITOR HUD", editorHover, true); }
        c.drawTextWithShadow(textRenderer, "Selecciona una tarjeta para ver sus opciones.", x + 18, height - 28, NexaUi.TEXT_3);
    }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled); double mx = click.x(), my = click.y();
        if (inside(mx, my, width - 194, 17, 126, 31)) { if (client != null) client.setScreen(new ModernNexaHudEditorScreen(this, modules)); return true; }
        int categoryY = 102; if (inside(mx, my, 10, categoryY, railWidth - 20, 28)) { category = null; scroll = 0; return true; } categoryY += 34;
        for (ModuleCategory value : ModuleCategory.values()) { if (inside(mx, my, 10, categoryY, railWidth - 20, 28)) { category = value; scroll = 0; return true; } categoryY += 34; }
        int profileY = height - 92; if (inside(mx, my, 12, profileY, railWidth - 24, 36)) { modules.config().nextBuiltInProfile(); modules.save(); return true; }
        if (detailWidth > 0 && selected != null) { int x = width - detailWidth; if (inside(mx, my, x + 28, 210, detailWidth - 56, 34)) return toggleSelected(); if (inside(mx, my, x + 28, 273, detailWidth - 56, 34)) { ModuleSettings.cycle(selected, modules.state(selected)); modules.save(); return true; } if (selected.editableHud() && inside(mx, my, x + 28, 320, detailWidth - 56, 28)) { if (client != null) client.setScreen(new ModernNexaHudEditorScreen(this, modules)); return true; } }
        for (Map.Entry<NexaModule, Rect> entry : cards.entrySet()) { Rect rect = entry.getValue(); if (!rect.contains(mx, my)) continue; selected = entry.getKey(); if (inside(mx, my, rect.x + rect.w - 47, rect.y + 15, 34, 18)) return toggleSelected(); return true; }
        return super.mouseClicked(click, doubled);
    }

    private boolean toggleSelected() { if (selected == null || !selected.implemented()) return true; NexaConfig.ModuleConfig state = modules.state(selected); state.enabled = !state.enabled; modules.save(); return true; }
    @Override public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) { scroll = Math.max(0, Math.min(Math.max(0, visible().size() - 1), scroll + (vertical < 0 ? 1 : -1))); return true; }
    private List<NexaModule> visible() { return ModuleRegistry.MODULES.stream().filter(module -> category == null || module.category() == category).toList(); }
    private static boolean inside(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    private record Rect(int x, int y, int w, int h) { boolean contains(double mx, double my) { return inside(mx, my, x, y, w, h); } }
    @Override public void close() { modules.save(); if (client != null) client.setScreen(parent); }
    @Override public boolean shouldPause() { return false; }
}
