package com.pikselhud.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MainScreen extends Screen {
    private final Screen parent;

    public MainScreen(Screen parent) {
        super(Text.translatable("pikselhud.screen.main.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("pikselhud.screen.main.settings"),
                button -> this.client.setScreen(new SettingsScreen(this))
        ).dimensions(centerX - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("pikselhud.screen.main.presets"),
                button -> this.client.setScreen(new PresetsScreen(this))
        ).dimensions(centerX - 100, centerY, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 30, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
