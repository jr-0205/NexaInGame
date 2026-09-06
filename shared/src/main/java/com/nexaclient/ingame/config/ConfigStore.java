package com.nexaclient.ingame.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ConfigStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("nexa-ingame.json");
    private NexaConfig config;

    public synchronized NexaConfig load() {
        if (config != null) return config;
        try {
            if (Files.isRegularFile(PATH)) {
                config = GSON.fromJson(Files.readString(PATH, StandardCharsets.UTF_8), NexaConfig.class);
            }
        } catch (Exception error) {
            backupBrokenConfiguration();
            System.err.println("[NEXA In-Game] Invalid configuration; safe defaults will be used.");
            error.printStackTrace(System.err);
        }
        if (config == null) config = new NexaConfig();
        config.validate();
        return config;
    }

    public synchronized void save() {
        if (config == null) return;
        config.validate();
        try {
            Files.createDirectories(PATH.getParent());
            Path temporary = PATH.resolveSibling(PATH.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(config), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException unsupportedAtomicMove) {
                Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException error) {
            System.err.println("[NEXA In-Game] Could not save " + PATH);
            error.printStackTrace(System.err);
        }
    }

    private static void backupBrokenConfiguration() {
        if (!Files.isRegularFile(PATH)) return;
        try {
            Path backup = PATH.resolveSibling("nexa-ingame.broken-" + System.currentTimeMillis() + ".json");
            Files.copy(PATH, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) { }
    }
}
