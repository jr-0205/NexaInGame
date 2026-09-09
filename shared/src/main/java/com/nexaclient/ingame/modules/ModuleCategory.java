package com.nexaclient.ingame.modules;

public enum ModuleCategory {
    HUD("HUD"),
    VISUAL("Visual"),
    GAMEPLAY("Gameplay"),
    CAMERA("Camara"),
    WORLD("Mundo"),
    UTILITY("Utilidad"),
    PERFORMANCE("Rendimiento");

    public final String label;

    ModuleCategory(String label) {
        this.label = label;
    }
}
