package com.nexaclient.ingame.modules;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Applies reversible modules that modify vanilla client options. */
public final class ModuleRuntime {
    private Double originalGamma;
    private Integer originalFov;

    public void tick(MinecraftClient client, ModuleRegistry modules) {
        applyGamma(client, modules.state("fullbright").enabled);

        var fovState = modules.state("fov_changer");
        var zoomState = modules.state("zoom");
        boolean zooming = zoomState.enabled && GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS;
        if (zooming) {
            applyFov(client, zoomState.intSetting("fov", 30, 10, 70));
        } else if (fovState.enabled) {
            applyFov(client, fovState.intSetting("fov", 90, 30, 110));
        } else {
            restoreFov(client);
        }
    }

    public void restore(MinecraftClient client) {
        if (originalGamma != null) client.options.getGamma().setValue(originalGamma);
        if (originalFov != null) client.options.getFov().setValue(originalFov);
        originalGamma = null;
        originalFov = null;
    }

    private void applyGamma(MinecraftClient client, boolean enabled) {
        if (enabled) {
            if (originalGamma == null) originalGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(1.0);
        } else if (originalGamma != null) {
            client.options.getGamma().setValue(originalGamma);
            originalGamma = null;
        }
    }

    private void applyFov(MinecraftClient client, int value) {
        if (originalFov == null) originalFov = client.options.getFov().getValue();
        client.options.getFov().setValue(value);
    }

    private void restoreFov(MinecraftClient client) {
        if (originalFov == null) return;
        client.options.getFov().setValue(originalFov);
        originalFov = null;
    }
}
