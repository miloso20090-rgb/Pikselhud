package com.pikselhud.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class PikselHudData {
    public HudConfig active = new HudConfig();
    public Map<String, HudConfig> presets = new LinkedHashMap<>();
    public com.pikselhud.nameprotect.NameProtectConfig nameProtect = new com.pikselhud.nameprotect.NameProtectConfig();

    public void sanitize() {
        if (active == null) active = new HudConfig();
        active.sanitize();
        if (presets == null) presets = new LinkedHashMap<>();
        if (nameProtect == null) nameProtect = new com.pikselhud.nameprotect.NameProtectConfig();
        if (nameProtect.replacement == null) nameProtect.replacement = "Player";
        if (nameProtect.friends == null) nameProtect.friends = "";
        presets.entrySet().removeIf(e -> e.getKey() == null || e.getKey().isBlank() || e.getValue() == null);
        presets.values().forEach(HudConfig::sanitize);
    }
}
