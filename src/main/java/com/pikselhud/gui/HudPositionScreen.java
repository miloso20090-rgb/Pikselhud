package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.ElementConfig;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            ElementConfig hit = findElement(mouseX, mouseY);
            if (hit != null) {
                dragging = hit;
                dragOffsetX = (int) mouseX - hit.x;
                dragOffsetY = (int) mouseY - hit.y;
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
            dragging.sanitize();
            dragging = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            ConfigManager.data.active = working;
            ConfigManager.save();
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
