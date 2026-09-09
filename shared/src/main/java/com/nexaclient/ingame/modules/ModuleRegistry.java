package com.nexaclient.ingame.modules;

import com.nexaclient.ingame.config.ConfigStore;
import com.nexaclient.ingame.config.NexaConfig;

import java.util.List;

public final class ModuleRegistry {
    public static final List<NexaModule> MODULES = List.of(
        hud("fps", "FPS", "Fotogramas por segundo", true, .02f, .04f),
        hud("ping", "Ping", "Latencia con el servidor", true, .02f, .09f),
        hud("memory", "Memory Usage", "Uso de memoria de Java", false, .02f, .14f),
        hud("clock", "Clock", "Hora local", false, .88f, .04f),
        hud("coordinates", "Coordinates", "Posicion, direccion y dimension", true, .02f, .84f),
        hud("armor", "Armor Status", "Armadura, herramientas y durabilidad", true, .80f, .68f),
        hud("inventory", "Inventory HUD", "Inventario visible con estilo configurable", false, .66f, .78f),
        hud("keystrokes", "Keystrokes", "WASD, mouse y teclas activas", false, .02f, .55f),
        hud("cps", "CPS", "Clics por segundo", false, .02f, .70f),

        implementedVisual("crosshair", "Crosshair", "Mira personalizable"),
        new NexaModule("boss_bar", "Boss Bar", "Barras de jefe, escala y posicion", ModuleCategory.VISUAL, false, .5f, .04f, false, true),
        new NexaModule("scoreboard", "Scoreboard", "Marcador, puntuacion y posicion", ModuleCategory.VISUAL, false, .98f, .4f, false, true),
        implementedVisual("nametags", "Nametags", "Distancia y estilo de etiquetas"),
        hud("f3_display", "F3 Display", "Diagnostico compacto del mundo", false, .02f, .18f),
        implementedVisual("hitbox", "Hitbox", "Colisiones locales de entidades"),
        implementedVisual("fullbright", "Lighting / Fullbright", "Iluminacion accesible"),

        camera("zoom", "Zoom", "Acercamiento temporal", true),
        camera("fov_changer", "FOV Changer", "Campo de vision configurable", true),
        camera("perspective", "Perspective", "Vista libre temporal alrededor del jugador", false),
        camera("freecam", "Freecam", "Camara desacoplada del jugador", false),

        upcoming("waypoints", "Waypoints", "Puntos visuales del mundo", ModuleCategory.WORLD),
        upcoming("screenshot_manager", "Screenshot Manager", "Gestion de capturas dentro del cliente", ModuleCategory.UTILITY),
        upcoming("particle_control", "Particle Controls", "Control fino de particulas", ModuleCategory.PERFORMANCE)
    );

    private final ConfigStore store;
    private final NexaConfig config;

    public ModuleRegistry(ConfigStore store) {
        this.store = store;
        this.config = store.load();
        for (NexaModule module : MODULES) state(module);
    }

    public NexaConfig.ModuleConfig state(NexaModule module) {
        return config.module(module.id(), module.defaultEnabled(), module.defaultX(), module.defaultY());
    }

    public NexaConfig.ModuleConfig state(String id) {
        NexaModule module = MODULES.stream().filter(value -> value.id().equals(id)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown module: " + id));
        return state(module);
    }

    public NexaConfig config() { return config; }
    public void save() { store.save(); }

    private static NexaModule hud(String id, String name, String description, boolean enabled, float x, float y) {
        return new NexaModule(id, name, description, ModuleCategory.HUD, enabled, x, y, true, true);
    }

    private static NexaModule implementedVisual(String id, String name, String description) {
        return new NexaModule(id, name, description, ModuleCategory.VISUAL, false, .5f, .5f, false, true);
    }

    private static NexaModule camera(String id, String name, String description, boolean implemented) {
        return new NexaModule(id, name, description, ModuleCategory.CAMERA, false, .5f, .5f, false, implemented);
    }

    private static NexaModule upcoming(String id, String name, String description, ModuleCategory category) {
        return new NexaModule(id, name, description, category, false, .5f, .5f, false, false);
    }
}
