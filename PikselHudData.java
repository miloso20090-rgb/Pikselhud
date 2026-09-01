package com.pikselhud.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class PikselHudData {
    public HudConfig active = new HudConfig();
    public Map<String, HudConfig> presets = new LinkedHashMap<>();

    public void sanitize() {
        if (active == null) active = new HudConfig();
        active.sanitize();
        if (presets == null) presets = new LinkedHashMap<>();
        presets.entrySet().removeIf(e -> e.getKey() == null || e.getKey().isBlank() || e.getValue() == null);
        presets.values().forEach(HudConfig::sanitize);
    }
}
