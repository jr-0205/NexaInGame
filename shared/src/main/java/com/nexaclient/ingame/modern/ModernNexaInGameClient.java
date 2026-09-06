package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.config.ConfigStore;
import com.nexaclient.ingame.hud.HudRenderer;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.ModuleRuntime;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import com.nexaclient.ingame.ui.NexaTheme;
import org.lwjgl.glfw.GLFW;

public final class ModernNexaInGameClient implements ClientModInitializer {
    public static final ConfigStore CONFIG_STORE = new ConfigStore();
    public static final ModuleRegistry MODULES = new ModuleRegistry(CONFIG_STORE);
    private static final ModuleRuntime RUNTIME = new ModuleRuntime();
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
        Identifier.of("nexa_ingame", "controls"));
    private static KeyBinding menuKey;
    private static KeyBinding editorKey;
    private static boolean titleTransition;

    @Override public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nexa_ingame.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
        editorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nexa_ingame.open_editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            NexaTheme.setBrandedMenus(MODULES.config().customTitleScreen);
            RUNTIME.tick(client, MODULES);
            if (!titleTransition && MODULES.config().customTitleScreen && client.currentScreen instanceof TitleScreen) {
                titleTransition = true;
                client.setScreen(new ModernNexaTitleScreen());
            } else if (!(client.currentScreen instanceof TitleScreen)) titleTransition = false;
            while (menuKey.wasPressed()) client.setScreen(new ModernNexaControlCenterScreen(client.currentScreen, MODULES));
            while (editorKey.wasPressed()) client.setScreen(new ModernNexaHudEditorScreen(client.currentScreen, MODULES));
        });
        HudRenderCallback.EVENT.register((context, tickCounter) -> HudRenderer.render(context, MODULES));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> { RUNTIME.restore(client); MODULES.save(); });
        Runtime.getRuntime().addShutdownHook(new Thread(MODULES::save, "nexa-config-save"));
    }
}
