package com.pikselhud.config;

public class HudConfig {
    public ElementConfig coords = new ElementConfig();
    public ElementConfig fps = new ElementConfig();
    public ElementConfig ping = new ElementConfig();

    public HudConfig copy() {
        HudConfig c = new HudConfig();
        c.coords = this.coords == null ? new ElementConfig() : this.coords.copy();
        c.fps = this.fps == null ? new ElementConfig() : this.fps.copy();
        c.ping = this.ping == null ? new ElementConfig() : this.ping.copy();
        return c;
    }

    public void sanitize() {
        if (coords == null) coords = new ElementConfig();
        if (fps == null) fps = new ElementConfig();
        if (ping == null) ping = new ElementConfig();
        coords.sanitize();
        fps.sanitize();
        ping.sanitize();
    }
}
