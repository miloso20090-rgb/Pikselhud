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
    private ElementConfig dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudPositionScreen(Screen parent) {
        super(Text.literal("Pozycja HUD"));
        this.parent = parent;
        this.working = ConfigManager.data.active.copy();
        this.working.sanitize();
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Zapisz"), b -> {
            ConfigManager.data.active = working;
            ConfigManager.save();
            this.client.setScreen(parent);
        }).dimensions(width / 2 - 105, height - 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Anuluj"), b -> this.client.setScreen(parent))
                .dimensions(width / 2 + 5, height - 30, 100, 20).build());
    }

    private ElementConfig hitTest(double mouseX, double mouseY) {
        ElementConfig[] elements = {working.coords, working.fps, working.ping};
        for (ElementConfig e : elements) {
            if (!e.enabled) continue;
            float s = e.scale;
            int w = Math.max(55, (int)(120 * s));
            int h = Math.max(18, (int)(12 * s));
            if (mouseX >= e.x && mouseX <= e.x + w && mouseY >= e.y && mouseY <= e.y + h) return e;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = hitTest(mouseX, mouseY);
            if (dragging != null) {
                dragOffsetX = (int) mouseX - dragging.x;
                dragOffsetY = (int) mouseY - dragging.y;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null && button == 0) {
            dragging.x = (int) mouseX - dragOffsetX;
            dragging.y = (int) mouseY - dragOffsetY;
            dragging.sanitize();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging != null) {
            dragging = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xC0101010);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Przeciągnij wskaźniki myszą"), width / 2, 28, 0xAAAAAA);

        drawPreview(context, "Koordynaty: 123 64 -456", working.coords, 0xFFFFFF);
        drawPreview(context, "FPS: 144", working.fps, 0xFFFFFF);
        drawPreview(context, "Ping: 25ms", working.ping, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawPreview(DrawContext context, String text, ElementConfig element, int fallbackColor) {
        if (!element.enabled) return;
        context.drawTextWithShadow(textRenderer, text, element.x, element.y,
                element.color == 0 ? fallbackColor : element.color);
    }

    @Override
    public boolean shouldPause() { return false; }
}
