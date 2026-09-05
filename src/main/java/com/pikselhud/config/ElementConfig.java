package com.pikselhud.config;

public class ElementConfig {
    public boolean enabled = true;
    public int x = 10;
    public int y = 10;
    public int color = 0xFFFFFF;
    public float scale = 1.0f;

    public ElementConfig copy() {
        ElementConfig c = new ElementConfig();
        c.enabled = enabled;
        c.x = x;
        c.y = y;
        c.color = color;
        c.scale = scale;
        return c;
    }

    public void sanitize() {
        color &= 0xFFFFFF;
        if (!Float.isFinite(scale)) scale = 1.0f;
        scale = Math.max(0.2f, Math.min(10.0f, scale));
        x = Math.max(-10000, Math.min(10000, x));
        y = Math.max(-10000, Math.min(10000, y));
    }
}
