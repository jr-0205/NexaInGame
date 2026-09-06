package com.nexaclient.ingame.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class NexaConfig {
    public static final int CURRENT_SCHEMA = 2;

    public int schema = CURRENT_SCHEMA;
    public boolean customTitleScreen = true;
    public boolean hudBackground = true;
    public int accentColor = 0xFF38A7FF;
    public String activeProfile = "Default";
    public Map<String, ProfileConfig> profiles = new LinkedHashMap<>();

    /** Present only to migrate configurations written by alpha.1. */
    @Deprecated public Map<String, ModuleConfig> modules;

    public ModuleConfig module(String id, boolean defaultEnabled, float x, float y) {
        ProfileConfig profile = profiles.computeIfAbsent(activeProfile, ignored -> new ProfileConfig());
        return profile.modules.computeIfAbsent(id, ignored -> new ModuleConfig(defaultEnabled, x, y));
    }

    public boolean useProfile(String value) {
        String name = normalizeProfileName(value);
        if (name == null) return false;
        profiles.computeIfAbsent(name, ignored -> new ProfileConfig());
        activeProfile = name;
        return true;
    }

    public boolean deleteProfile(String name) {
        if (name == null || name.equals("Default") || !profiles.containsKey(name)) return false;
        profiles.remove(name);
        if (name.equals(activeProfile)) activeProfile = "Default";
        return true;
    }

    public void nextBuiltInProfile() {
        String next = switch (activeProfile) {
            case "Default" -> "PvP";
            case "PvP" -> "Survival";
            default -> "Default";
        };
        useProfile(next);
    }

    public void validate() {
        if ((accentColor >>> 24) == 0) accentColor |= 0xFF000000;
        if (profiles == null) profiles = new LinkedHashMap<>();
        if (schema < CURRENT_SCHEMA && modules != null && !modules.isEmpty()) {
            ProfileConfig migrated = new ProfileConfig();
            migrated.modules.putAll(modules);
            profiles.putIfAbsent("Default", migrated);
        }
        modules = null;
        if (normalizeProfileName(activeProfile) == null) activeProfile = "Default";
        profiles.computeIfAbsent("Default", ignored -> new ProfileConfig());
        profiles.computeIfAbsent(activeProfile, ignored -> new ProfileConfig());
        profiles.values().removeIf(Objects::isNull);
        for (ProfileConfig profile : profiles.values()) {
            if (profile.modules == null) profile.modules = new LinkedHashMap<>();
            profile.modules.values().removeIf(Objects::isNull);
            profile.modules.values().forEach(ModuleConfig::validate);
        }
        schema = CURRENT_SCHEMA;
    }

    private static String normalizeProfileName(String value) {
        if (value == null) return null;
        String name = value.trim();
        return name.isEmpty() || name.length() > 32 ? null : name;
    }

    public static final class ProfileConfig {
        public Map<String, ModuleConfig> modules = new LinkedHashMap<>();
    }

    public static final class ModuleConfig {
        public boolean enabled;
        public float x;
        public float y;
        public float scale = 1.0f;
        public float opacity = 0.92f;
        public String style = "NEXA";
        public String horizontalAnchor = "LEFT";
        public String verticalAnchor = "TOP";
        public int zIndex;
        public Map<String, String> settings = new LinkedHashMap<>();

        public ModuleConfig() { }

        public ModuleConfig(boolean enabled, float x, float y) {
            this.enabled = enabled;
            this.x = x;
            this.y = y;
        }

        public String setting(String key, String fallback) {
            if (settings == null) settings = new LinkedHashMap<>();
            return settings.getOrDefault(key, fallback);
        }

        public boolean booleanSetting(String key, boolean fallback) {
            return Boolean.parseBoolean(setting(key, Boolean.toString(fallback)));
        }

        public int intSetting(String key, int fallback, int minimum, int maximum) {
            try {
                return Math.max(minimum, Math.min(maximum,
                    Integer.parseInt(setting(key, Integer.toString(fallback)))));
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }

        public void validate() {
            x = clamp(x, 0f, 1f);
            y = clamp(y, 0f, 1f);
            scale = clamp(scale, .5f, 2f);
            opacity = clamp(opacity, .15f, 1f);
            if (style == null || style.isBlank()) style = "NEXA";
            if (horizontalAnchor == null) horizontalAnchor = "LEFT";
            if (verticalAnchor == null) verticalAnchor = "TOP";
            zIndex = Math.max(-100, Math.min(100, zIndex));
            if (settings == null) settings = new LinkedHashMap<>();
            settings.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null);
        }

        private static float clamp(float value, float minimum, float maximum) {
            return Float.isFinite(value) ? Math.max(minimum, Math.min(maximum, value)) : minimum;
        }
    }
}
