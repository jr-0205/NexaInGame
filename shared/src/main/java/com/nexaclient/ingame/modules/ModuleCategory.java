package com.nexaclient.ingame.modules;

public enum ModuleCategory {
    HUD("HUD"), VISUAL("Visual"), GAMEPLAY("Gameplay"), PERFORMANCE("Rendimiento");
    public final String label;
    ModuleCategory(String label) { this.label = label; }
}

