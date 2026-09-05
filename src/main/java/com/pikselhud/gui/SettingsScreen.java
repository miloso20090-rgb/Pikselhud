package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {
    private final Screen parent;
    private HudConfig working;
    private ButtonWidget coordsToggle;
    private ButtonWidget fpsToggle;
    private ButtonWidget pingToggle;

    public SettingsScreen(Screen parent) {
        super(Text.translatable("pikselhud.screen.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        working = ConfigManager.data.active.copy();
        working.sanitize();

        int x = 20;
        int y = 45;
        coordsToggle = this.addDrawableChild(ButtonWidget.builder(Text.literal("Koordynaty"),
                b -> { working.coords.enabled = !working.coords.enabled; updateLabels(); })
                .dimensions(x, y, 220, 20).build());
        y += 28;
        fpsToggle = this.addDrawableChild(ButtonWidget.builder(Text.literal("FPS"),
                b -> { working.fps.enabled = !working.fps.enabled; updateLabels(); })
                .dimensions(x, y, 220, 20).build());
        y += 28;
        pingToggle = this.addDrawableChild(ButtonWidget.builder(Text.literal("Ping"),
                b -> { working.ping.enabled = !working.ping.enabled; updateLabels(); })
                .dimensions(x, y, 220, 20).build());
        y += 40;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.settings.save"), b -> {
            ConfigManager.data.active = working;
            ConfigManager.save();
            this.client.setScreen(parent);
        }).dimensions(x, y, 105, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Anuluj"), b -> this.client.setScreen(parent))
                .dimensions(x + 115, y, 105, 20).build());
        updateLabels();
    }

    private void updateLabels() {
        coordsToggle.setMessage(Text.literal("Koordynaty: " + (working.coords.enabled ? "WL" : "WYL")));
        fpsToggle.setMessage(Text.literal("FPS: " + (working.fps.enabled ? "WL" : "WYL")));
        pingToggle.setMessage(Text.literal("Ping: " + (working.ping.enabled ? "WL" : "WYL")));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xC0101010);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
