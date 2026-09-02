package com.pikselhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pikselhud.json");
    public static PikselHudData data = new PikselHudData();

    private ConfigManager() {}

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            data = new PikselHudData();
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            PikselHudData loaded = GSON.fromJson(reader, PikselHudData.class);
            data = loaded != null ? loaded : new PikselHudData();
            data.sanitize();
        } catch (Exception e) {
            System.err.println("[PikselHUD] Nie udało się wczytać konfiguracji, używam domyślnej: " + e.getMessage());
            data = new PikselHudData();
            save();
        }
    }

    public static void save() {
        data.sanitize();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("[PikselHUD] Nie udało się zapisać konfiguracji: " + e.getMessage());
        }
    }
}
