package com.nexaclient.ingame.modules;

import com.nexaclient.ingame.config.NexaConfig;
import java.util.ArrayList;
import java.util.List;

public final class ModuleSettings {
    public record Option(String key, String label, String value) { }
    private record Rule(String key, String label, int initial, int minimum, int maximum, int step, String[] choices) { }
    private ModuleSettings() { }

    private static List<Rule> rules(NexaModule module) {
        return switch (module.id()) {
            case "clock" -> List.of(toggle("seconds", "Segundos", false), toggle("twelveHours", "Formato 12 horas", false));
            case "crosshair" -> List.of(number("gap", "Separación", 3, 1, 8, 1), number("length", "Longitud", 4, 2, 12, 1), number("thickness", "Grosor", 1, 1, 3, 1));
            case "zoom" -> List.of(number("fov", "FOV al mantener C", 30, 10, 70, 5));
            case "fov_changer" -> List.of(number("fov", "Campo de visión", 90, 30, 110, 5));
            case "boss_bar" -> List.of(toggle("hidden", "Ocultar barras", false), number("limit", "Máximo de barras", 3, 1, 6, 1));
            case "scoreboard" -> List.of(toggle("hidden", "Ocultar marcador", false), toggle("scores", "Mostrar puntuación", true), number("limit", "Máximo de filas", 15, 3, 15, 1));
            case "nametags" -> List.of(toggle("distance", "Mostrar distancia", true), number("range", "Alcance en bloques", 32, 8, 64, 8),
                new Rule("color", "Color", 0, 0, 0, 0, new String[]{"aqua", "white", "green", "gold"}));
            case "f3_display" -> List.of(toggle("details", "Bioma, dimensión y RAM", true));
            default -> List.of();
        };
    }

    public static List<Option> options(NexaModule module, NexaConfig.ModuleConfig state) {
        List<Option> result = new ArrayList<>();
        for (Rule rule : rules(module)) {
            String value = rule.choices == null ? Integer.toString(state.intSetting(rule.key, rule.initial, rule.minimum, rule.maximum))
                : state.setting(rule.key, rule.choices[rule.initial]);
            if ("true".equals(value)) value = "Sí";
            if ("false".equals(value)) value = "No";
            result.add(new Option(rule.key, rule.label, value));
        }
        if (module.editableHud()) result.add(new Option("style", "Fondo", "MINIMAL".equals(state.style) ? "Transparente" : "NEXA"));
        if (module.editableHud() || module.id().equals("boss_bar") || module.id().equals("scoreboard") || module.id().equals("crosshair")) {
            if (!module.id().equals("crosshair")) result.add(new Option("scale", "Escala", Math.round(state.scale * 100) + "%"));
            result.add(new Option("opacity", "Opacidad", Math.round(state.opacity * 100) + "%"));
        }
        if (module.editableHud() || module.id().equals("boss_bar") || module.id().equals("scoreboard")) {
            result.add(new Option("x", "Posición horizontal", Math.round(state.x * 100) + "%"));
            result.add(new Option("y", "Posición vertical", Math.round(state.y * 100) + "%"));
        }
        return result;
    }

    public static void cycleOption(NexaModule module, NexaConfig.ModuleConfig state, String key, int direction) {
        int stepDirection = direction < 0 ? -1 : 1;
        switch (key) {
            case "style" -> state.style = "MINIMAL".equals(state.style) ? "NEXA" : "MINIMAL";
            case "scale" -> state.scale = cycleFloat(state.scale, .5f, 2f, .1f * stepDirection);
            case "opacity" -> state.opacity = cycleFloat(state.opacity, .15f, 1f, .05f * stepDirection);
            case "x" -> state.x = cycleFloat(state.x, 0f, 1f, .05f * stepDirection);
            case "y" -> state.y = cycleFloat(state.y, 0f, 1f, .05f * stepDirection);
            default -> {
                for (Rule rule : rules(module)) {
                    if (!rule.key.equals(key)) continue;
                    if (rule.choices == null) {
                        int value = state.intSetting(key, rule.initial, rule.minimum, rule.maximum) + rule.step * stepDirection;
                        if (value > rule.maximum) value = rule.minimum;
                        if (value < rule.minimum) value = rule.maximum;
                        state.settings.put(key, Integer.toString(value));
                    } else {
                        String current = state.setting(key, rule.choices[rule.initial]);
                        int index = java.util.Arrays.asList(rule.choices).indexOf(current);
                        state.settings.put(key, rule.choices[Math.floorMod(index + stepDirection, rule.choices.length)]);
                    }
                }
            }
        }
        state.validate();
    }

    public static String summary(NexaModule module, NexaConfig.ModuleConfig state) {
        var options = options(module, state);
        if (options.isEmpty()) return module.id().equals("hitbox") ? "Colisiones locales" : "Brillo máximo de Minecraft";
        return options.get(0).label() + ": " + options.get(0).value();
    }

    public static void cycle(NexaModule module, NexaConfig.ModuleConfig state) {
        var options = options(module, state);
        if (!options.isEmpty()) cycleOption(module, state, options.get(0).key(), 1);
    }

    private static float cycleFloat(float value, float minimum, float maximum, float step) {
        float next = Math.round((value + step) * 100) / 100f;
        return next > maximum + .001f ? minimum : next < minimum - .001f ? maximum : next;
    }
    private static Rule toggle(String key, String label, boolean initial) {
        return new Rule(key, label, initial ? 1 : 0, 0, 0, 0, new String[]{"false", "true"});
    }
    private static Rule number(String key, String label, int initial, int min, int max, int step) {
        return new Rule(key, label, initial, min, max, step, null);
    }
}
