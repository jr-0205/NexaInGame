package com.nexaclient.ingame.modules;

import com.nexaclient.ingame.config.NexaConfig;

/** Small in-game settings controller used until dedicated settings panels are added. */
public final class ModuleSettings {
    private ModuleSettings() { }

    public static String summary(NexaModule module, NexaConfig.ModuleConfig state) {
        return switch (module.id()) {
            case "clock" -> state.booleanSetting("seconds", false) ? "CON SEGUNDOS" : "SIN SEGUNDOS";
            case "crosshair" -> "GAP " + state.intSetting("gap", 3, 1, 8);
            case "zoom" -> "FOV " + state.intSetting("fov", 30, 10, 70) + " | C";
            case "fov_changer" -> "FOV " + state.intSetting("fov", 90, 30, 110);
            case "fullbright" -> "GAMMA SEGURA";
            default -> state.style.toUpperCase();
        };
    }

    public static void cycle(NexaModule module, NexaConfig.ModuleConfig state) {
        switch (module.id()) {
            case "clock" -> state.settings.put("seconds",
                Boolean.toString(!state.booleanSetting("seconds", false)));
            case "crosshair" -> state.settings.put("gap",
                Integer.toString(next(state.intSetting("gap", 3, 1, 8), 1, 8, 1)));
            case "zoom" -> state.settings.put("fov",
                Integer.toString(next(state.intSetting("fov", 30, 10, 70), 10, 70, 10)));
            case "fov_changer" -> state.settings.put("fov",
                Integer.toString(next(state.intSetting("fov", 90, 30, 110), 30, 110, 10)));
            case "fullbright" -> { }
            default -> state.style = "MINIMAL".equalsIgnoreCase(state.style) ? "NEXA" : "MINIMAL";
        }
    }

    private static int next(int value, int minimum, int maximum, int step) {
        int result = value + step;
        return result > maximum ? minimum : result;
    }
}
