package com.nexaclient.ingame.hud;

import com.nexaclient.ingame.compat.HudCompat;
import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.input.InputTracker;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.NexaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public final class HudRenderer {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final Map<String, HudWidget> WIDGETS = widgets();

    private HudRenderer() { }

    public static void render(DrawContext context, ModuleRegistry modules) {
        MinecraftClient client = MinecraftClient.getInstance();
        InputTracker.update(client);
        if (client.options.hudHidden || client.player == null) return;

        if (modules.state("crosshair").enabled) renderCrosshair(context, client, modules);

        ModuleRegistry.MODULES.stream()
            .filter(NexaModule::editableHud)
            .filter(module -> modules.state(module).enabled)
            .sorted(Comparator.comparingInt(module -> modules.state(module).zIndex))
            .forEach(module -> renderModule(context, client, modules, module, modules.state(module), false));
    }

    public static Bounds renderModule(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                                      NexaModule module, NexaConfig.ModuleConfig state, boolean editor) {
        state.validate();
        HudWidget widget = WIDGETS.getOrDefault(module.id(), new TextWidget((ignored, ignoredState) -> module.name()));
        Size logical = widget.measure(client, state);
        int width = Math.max(1, Math.round(logical.width * state.scale));
        int height = Math.max(1, Math.round(logical.height * state.scale));
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = Math.round(state.x * Math.max(0, screenWidth - width));
        int y = Math.round(state.y * Math.max(0, screenHeight - height));

        HudCompat.push(context, x, y, state.scale);
        widget.draw(context, client, modules, state, editor);
        HudCompat.pop(context);
        return new Bounds(x, y, width, height);
    }

    private static Map<String, HudWidget> widgets() {
        Map<String, HudWidget> values = new LinkedHashMap<>();
        values.put("fps", new TextWidget((client, state) -> client.getCurrentFps() + " FPS"));
        values.put("ping", new TextWidget((client, state) -> ping(client)));
        values.put("memory", new TextWidget((client, state) -> memory()));
        values.put("clock", new TextWidget(HudRenderer::clock));
        values.put("coordinates", new TextWidget(HudRenderer::coordinates));
        values.put("armor", new ArmorWidget());
        values.put("inventory", new InventoryWidget());
        values.put("keystrokes", new KeystrokesWidget());
        values.put("cps", new TextWidget((client, state) ->
            InputTracker.leftCps() + " | " + InputTracker.rightCps() + " CPS"));
        return Map.copyOf(values);
    }

    private static String ping(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null) return "Ping --";
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        return entry == null ? "Ping --" : entry.getLatency() + " ms";
    }

    private static String memory() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1_048_576L;
        long max = Math.max(1, runtime.maxMemory() / 1_048_576L);
        return "RAM " + used + " MB  " + (used * 100 / max) + "%";
    }

    private static String clock(MinecraftClient client, NexaConfig.ModuleConfig state) {
        boolean seconds = state.booleanSetting("seconds", false);
        boolean twelveHours = state.booleanSetting("twelveHours", false);
        String pattern = twelveHours ? (seconds ? "hh:mm:ss a" : "hh:mm a") : (seconds ? "HH:mm:ss" : "HH:mm");
        return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern, Locale.ROOT));
    }

    private static String coordinates(MinecraftClient client, NexaConfig.ModuleConfig state) {
        if (client.player == null) return "XYZ 0 / 64 / 0  SOUTH";
        String direction = client.player.getHorizontalFacing().asString().toUpperCase(Locale.ROOT);
        return String.format(Locale.ROOT, "XYZ %.0f / %.0f / %.0f  %s",
            client.player.getX(), client.player.getY(), client.player.getZ(), direction);
    }

    private static void renderCrosshair(DrawContext context, MinecraftClient client, ModuleRegistry modules) {
        NexaConfig.ModuleConfig state = modules.state("crosshair");
        int gap = state.intSetting("gap", 3, 1, 8);
        int length = state.intSetting("length", 4, 2, 12);
        int thickness = state.intSetting("thickness", 1, 1, 3);
        int color = withAlpha(modules.config().accentColor, Math.round(state.opacity * 255));
        int x = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() / 2;
        context.fill(x - thickness / 2, y - gap - length, x + (thickness + 1) / 2, y - gap, color);
        context.fill(x - thickness / 2, y + gap, x + (thickness + 1) / 2, y + gap + length, color);
        context.fill(x - gap - length, y - thickness / 2, x - gap, y + (thickness + 1) / 2, color);
        context.fill(x + gap, y - thickness / 2, x + gap + length, y + (thickness + 1) / 2, color);
    }

    private static void panel(DrawContext context, ModuleRegistry modules, NexaConfig.ModuleConfig state,
                              int width, int height, boolean editor) {
        int alpha = Math.max(38, Math.min(255, Math.round(state.opacity * 255)));
        if ((modules.config().hudBackground || editor) && !"MINIMAL".equalsIgnoreCase(state.style))
            context.fill(0, 0, width, height, withAlpha(0xFF07111E, alpha));
        context.fill(0, 0, 2, height, withAlpha(modules.config().accentColor, alpha));
    }

    private static int withAlpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0x00FFFFFF);
    }

    private interface HudWidget {
        Size measure(MinecraftClient client, NexaConfig.ModuleConfig state);
        void draw(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                  NexaConfig.ModuleConfig state, boolean editor);
    }

    private record TextWidget(BiFunction<MinecraftClient, NexaConfig.ModuleConfig, String> value) implements HudWidget {
        @Override public Size measure(MinecraftClient client, NexaConfig.ModuleConfig state) {
            return new Size(Math.max(50, client.textRenderer.getWidth(value.apply(client, state)) + 12), 18);
        }

        @Override public void draw(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                                   NexaConfig.ModuleConfig state, boolean editor) {
            String text = value.apply(client, state);
            Size size = measure(client, state);
            panel(context, modules, state, size.width, size.height, editor);
            context.drawTextWithShadow(client.textRenderer, text, 7, 5,
                withAlpha(0xFFF4F8FC, Math.round(state.opacity * 255)));
        }
    }

    private static final class InventoryWidget implements HudWidget {
        @Override public Size measure(MinecraftClient client, NexaConfig.ModuleConfig state) { return new Size(170, 62); }

        @Override public void draw(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                                   NexaConfig.ModuleConfig state, boolean editor) {
            Size size = measure(client, state);
            panel(context, modules, state, size.width, size.height, editor);
            if (client.player == null) return;
            for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) {
                ItemStack stack = client.player.getInventory().getStack(9 + row * 9 + column);
                int x = 5 + column * 18;
                int y = 4 + row * 18;
                if (!stack.isEmpty()) {
                    context.drawItem(stack, x, y);
                    HudCompat.drawOverlay(context, client.textRenderer, stack, x, y);
                }
            }
        }
    }

    private static final class ArmorWidget implements HudWidget {
        @Override public Size measure(MinecraftClient client, NexaConfig.ModuleConfig state) { return new Size(88, 25); }

        @Override public void draw(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                                   NexaConfig.ModuleConfig state, boolean editor) {
            Size size = measure(client, state);
            panel(context, modules, state, size.width, size.height, editor);
            for (int index = 0; index < ARMOR_SLOTS.length; index++) {
                ItemStack stack = client.player == null ? ItemStack.EMPTY : client.player.getEquippedStack(ARMOR_SLOTS[index]);
                int x = 5 + index * 20;
                if (!stack.isEmpty()) {
                    context.drawItem(stack, x, 3);
                    HudCompat.drawOverlay(context, client.textRenderer, stack, x, 3);
                    if (stack.isDamageable()) {
                        int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamage());
                        int bar = Math.round(16f * remaining / Math.max(1, stack.getMaxDamage()));
                        context.fill(x, 21, x + 16, 23, 0xAA101820);
                        context.fill(x, 21, x + bar, 23, withAlpha(modules.config().accentColor, 255));
                    }
                }
            }
        }
    }

    private static final class KeystrokesWidget implements HudWidget {
        @Override public Size measure(MinecraftClient client, NexaConfig.ModuleConfig state) { return new Size(68, 62); }

        @Override public void draw(DrawContext context, MinecraftClient client, ModuleRegistry modules,
                                   NexaConfig.ModuleConfig state, boolean editor) {
            panel(context, modules, state, 68, 62, editor);
            key(context, client, "W", 25, 4, 18, 16, client.options.forwardKey.isPressed(), state);
            key(context, client, "A", 4, 23, 18, 16, client.options.leftKey.isPressed(), state);
            key(context, client, "S", 25, 23, 18, 16, client.options.backKey.isPressed(), state);
            key(context, client, "D", 46, 23, 18, 16, client.options.rightKey.isPressed(), state);
            key(context, client, "LMB", 4, 42, 29, 16, InputTracker.leftMouse(), state);
            key(context, client, "RMB", 36, 42, 28, 16, InputTracker.rightMouse(), state);
        }

        private static void key(DrawContext context, MinecraftClient client, String label, int x, int y,
                                int width, int height, boolean pressed, NexaConfig.ModuleConfig state) {
            int alpha = Math.round(state.opacity * 255);
            context.fill(x, y, x + width, y + height, withAlpha(pressed ? 0xFF38A7FF : 0xFF223548, alpha));
            context.drawCenteredTextWithShadow(client.textRenderer, label, x + width / 2, y + 4,
                withAlpha(0xFFF4F8FC, alpha));
        }
    }

    private record Size(int width, int height) { }

    public record Bounds(int x, int y, int width, int height) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
}
