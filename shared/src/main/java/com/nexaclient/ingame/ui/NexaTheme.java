package com.nexaclient.ingame.ui;

/** Shared state read by the menu background mixin without coupling it to a version entrypoint. */
public final class NexaTheme {
    private static volatile boolean brandedMenus = true;

    private NexaTheme() { }

    public static boolean brandedMenus() { return brandedMenus; }
    public static void setBrandedMenus(boolean enabled) { brandedMenus = enabled; }
}
