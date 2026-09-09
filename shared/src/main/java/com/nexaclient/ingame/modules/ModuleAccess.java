package com.nexaclient.ingame.modules;

public final class ModuleAccess {
    private static ModuleRegistry registry;
    private ModuleAccess() { }
    public static void initialize(ModuleRegistry value) { registry = value; }
    public static ModuleRegistry registry() { return registry; }
    public static boolean enabled(String id) { return registry != null && registry.state(id).enabled; }
}
