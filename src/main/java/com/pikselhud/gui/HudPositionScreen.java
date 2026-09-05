package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.ElementConfig;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
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
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) {
            ElementConfig hit = findElement(mouseX, mouseY);
            if (hit != null) {
                dragging = hit;
                dragOffsetX = (int) mouseX - hit.x;
                dragOffsetY = (int) mouseY - hit.y;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null && click.button() == 0) {
            dragging.x = (int) click.x() - dragOffsetX;
            dragging.y = (int) click.y() - dragOffsetY;
            dragging.sanitize();
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && dragging != null) {
            dragging.sanitize();
            dragging = null;
            return true;
        }
        return super.mouseReleased(click);
    }

    private ElementConfig findElement(double mouseX, double mouseY) {
        ElementConfig[] elements = {working.coords, working.fps, working.ping};
        String[] previews = {"Koordynaty: 123 64 -456", "FPS: 144", "Ping: 25ms"};
        for (int i = elements.length - 1; i >= 0; i--) {
            ElementConfig element = elements[i];
            if (!element.enabled) continue;
            int width = Math.max(30, (int) (textRenderer.getWidth(previews[i]) * element.scale));
            int height = Math.max(14, (int) (textRenderer.fontHeight * element.scale));
            if (mouseX >= element.x && mouseX <= element.x + width
                    && mouseY >= element.y && mouseY <= element.y + height) {
                return element;
            }
        }
        return null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xC0101010);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Przeciągnij wskaźniki myszką, aby ustawić je w dowolnym miejscu"),
                width / 2, 28, 0xAAAAAA);
        drawPreview(context, "Koordynaty: 123 64 -456", working.coords);
        drawPreview(context, "FPS: 144", working.fps);
        drawPreview(context, "Ping: 25ms", working.ping);

        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("LPM: przeciągnij   |   Zapisz: Enter"), width / 2, height - 35, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawPreview(DrawContext context, String text, ElementConfig element) {
        if (!element.enabled) return;
        context.drawTextWithShadow(textRenderer, text, element.x, element.y,
                element.color == 0 ? 0xFFFFFF : element.color);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.getKeycode() == 257 || input.getKeycode() == 335) {
            ConfigManager.data.active = working;
            ConfigManager.save();
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean shouldPause() { return false; }
}
