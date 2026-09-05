package com.pikselhud.config;

public class HudConfig {
    public ElementConfig coords;
    public ElementConfig fps;
    public ElementConfig ping;

    public HudConfig() {
        coords = new ElementConfig(10, 10);
        fps = new ElementConfig(10, 30);
        ping = new ElementConfig(10, 50);
    }

    public HudConfig copy() {
        HudConfig c = new HudConfig();
        c.coords = this.coords == null ? new ElementConfig(10, 10) : this.coords.copy();
        c.fps = this.fps == null ? new ElementConfig(10, 30) : this.fps.copy();
        c.ping = this.ping == null ? new ElementConfig(10, 50) : this.ping.copy();
        return c;
    }

    public void sanitize() {
        if (coords == null) coords = new ElementConfig(10, 10);
        if (fps == null) fps = new ElementConfig(10, 30);
        if (ping == null) ping = new ElementConfig(10, 50);

        // Migrate the old version where every indicator defaulted to 10,10.
        if (coords.x == 10 && coords.y == 10 && fps.x == 10 && fps.y == 10 && ping.x == 10 && ping.y == 10) {
            fps.x = 10; fps.y = 30;
            ping.x = 10; ping.y = 50;
        }

        coords.sanitize();
        fps.sanitize();
        ping.sanitize();
    }
}
