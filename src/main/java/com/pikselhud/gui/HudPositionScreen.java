package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.ElementConfig;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HudPositionScreen extends Screen {
    private final Screen parent;
    private final HudConfig working;

    public HudPositionScreen(Screen parent) {
        super(Text.literal("Pozycja HUD"));
        this.parent = parent;
        this.working = ConfigManager.data.active.copy();
        this.working.sanitize();
    }

    @Override
    protected void init() {
        int left = width / 2 - 145;
        addElementControls("Koordynaty", working.coords, left, 50);
        addElementControls("FPS", working.fps, left, 115);
        addElementControls("Ping", working.ping, left, 180);

        addDrawableChild(ButtonWidget.builder(Text.literal("Zapisz"), b -> {
            ConfigManager.data.active = working;
            ConfigManager.save();
            client.setScreen(parent);
        }).dimensions(width / 2 - 105, height - 30, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Anuluj"), b -> client.setScreen(parent))
                .dimensions(width / 2 + 5, height - 30, 100, 20).build());
    }

    private void addElementControls(String name, ElementConfig element, int x, int y) {
        addDrawableChild(ButtonWidget.builder(Text.literal(name + "  ↖"), b -> setCorner(element, 0))
                .dimensions(x, y, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("↗"), b -> setCorner(element, 1))
                .dimensions(x + 72, y, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("↙"), b -> setCorner(element, 2))
                .dimensions(x, y + 24, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("↘"), b -> setCorner(element, 3))
                .dimensions(x + 72, y + 24, 68, 20).build());
    }

    private void setCorner(ElementConfig element, int corner) {
        int margin = 10;
        int estimatedWidth = Math.max(120, (int)(180 * element.scale));
        int estimatedHeight = Math.max(14, (int)(16 * element.scale));
        switch (corner) {
            case 0 -> { element.x = margin; element.y = margin; }
            case 1 -> { element.x = Math.max(margin, width - estimatedWidth - margin); element.y = margin; }
            case 2 -> { element.x = margin; element.y = Math.max(margin, height - estimatedHeight - 35); }
            case 3 -> { element.x = Math.max(margin, width - estimatedWidth - margin); element.y = Math.max(margin, height - estimatedHeight - 35); }
        }
        element.sanitize();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xC0101010);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Wybierz położenie każdego wskaźnika"), width / 2, 28, 0xAAAAAA);
        drawPreview(context, "Koordynaty: 123 64 -456", working.coords);
        drawPreview(context, "FPS: 144", working.fps);
        drawPreview(context, "Ping: 25ms", working.ping);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawPreview(DrawContext context, String text, ElementConfig element) {
        if (!element.enabled) return;
        context.drawTextWithShadow(textRenderer, text, element.x, element.y,
                element.color == 0 ? 0xFFFFFF : element.color);
    }

    @Override
    public boolean shouldPause() { return false; }
}
