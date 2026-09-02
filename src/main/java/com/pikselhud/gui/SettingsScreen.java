package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.ElementConfig;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

public class SettingsScreen extends Screen {
    private static final int[] PALETTE = {
            0xFFFFFF, 0xFF5555, 0x55FF55, 0xFFFF55, 0x55FFFF, 0xFF55FF, 0x5555FF, 0x000000
    };
    private static final String[] FONT_NAMES = {"Normalna", "Pogrubiona", "Kursywa", "Pogr.+Kursywa"};

    private final Screen parent;
    private HudConfig working;
    private String selected = "coords";

    private ButtonWidget coordsToggle;
    private ButtonWidget fpsToggle;
    private ButtonWidget pingToggle;
    private ButtonWidget fontButton;
    private ButtonWidget colorButton;
    private ScaleSlider scaleSlider;

    private String draggingElement = null;
    private int dragOffsetX, dragOffsetY;

    public SettingsScreen(Screen parent) {
        super(Text.translatable("pikselhud.screen.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.working = ConfigManager.data.active.copy();
        this.working.sanitize();

        int panelX = 10;
        int y = 30;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Koordynaty"),
                b -> { selected = "coords"; refreshSelectedControls(); }).dimensions(panelX, y, 90, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("FPS"),
                b -> { selected = "fps"; refreshSelectedControls(); }).dimensions(panelX + 95, y, 60, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Ping"),
                b -> { selected = "ping"; refreshSelectedControls(); }).dimensions(panelX + 160, y, 60, 20).build());
        y += 30;

        coordsToggle = ButtonWidget.builder(Text.translatable("pikselhud.screen.settings.coords"),
                b -> { working.coords.enabled = !working.coords.enabled; updateToggleTexts(); })
                .dimensions(panelX, y, 210, 20).build();
        this.addDrawableChild(coordsToggle);
        y += 24;

        fpsToggle = ButtonWidget.builder(Text.translatable("pikselhud.screen.settings.fps"),
                b -> { working.fps.enabled = !working.fps.enabled; updateToggleTexts(); })
                .dimensions(panelX, y, 210, 20).build();
        this.addDrawableChild(fpsToggle);
        y += 24;

        pingToggle = ButtonWidget.builder(Text.translatable("pikselhud.screen.settings.ping"),
                b -> { working.ping.enabled = !working.ping.enabled; updateToggleTexts(); })
                .dimensions(panelX, y, 210, 20).build();
        this.addDrawableChild(pingToggle);
        y += 30;

        ElementConfig current = getSelected();
        scaleSlider = new ScaleSlider(panelX, y, 210, 20,
                Text.translatable("pikselhud.screen.settings.scale", String.format("%.1f", current.scale)),
                normalizeScale(current.scale));
        this.addDrawableChild(scaleSlider);
        y += 24;

        fontButton = ButtonWidget.builder(fontLabel(current.fontStyle), b -> {
            ElementConfig sel = getSelected();
            sel.fontStyle = (sel.fontStyle + 1) % 4;
            fontButton.setMessage(fontLabel(sel.fontStyle));
        }).dimensions(panelX, y, 210, 20).build();
        this.addDrawableChild(fontButton);
        y += 24;

        colorButton = ButtonWidget.builder(colorLabel(current.color), b -> {
            ElementConfig sel = getSelected();
            sel.color = nextColor(sel.color);
            colorButton.setMessage(colorLabel(sel.color));
        }).dimensions(panelX, y, 210, 20).build();
        this.addDrawableChild(colorButton);
        y += 30;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.settings.save"), b -> {
            ConfigManager.data.active = working;
            ConfigManager.save();
            this.client.setScreen(parent);
        }).dimensions(panelX, y, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Anuluj"), b -> {
            this.client.setScreen(parent);
        }).dimensions(panelX + 110, y, 100, 20).build());

        updateToggleTexts();
    }

    private void refreshSelectedControls() {
        if (scaleSlider == null || fontButton == null || colorButton == null) return;
        ElementConfig sel = getSelected();
        scaleSlider.setNormalizedValue(normalizeScale(sel.scale));
        scaleSlider.updateMessage();
        fontButton.setMessage(fontLabel(sel.fontStyle));
        colorButton.setMessage(colorLabel(sel.color));
    }

    private ElementConfig getSelected() {
        return switch (selected) {
            case "fps" -> working.fps;
            case "ping" -> working.ping;
            default -> working.coords;
        };
    }

    private void updateToggleTexts() {
        coordsToggle.setMessage(Text.literal("Koordynaty: " + (working.coords.enabled ? "WL" : "WYL")));
        fpsToggle.setMessage(Text.literal("FPS: " + (working.fps.enabled ? "WL" : "WYL")));
        pingToggle.setMessage(Text.literal("Ping: " + (working.ping.enabled ? "WL" : "WYL")));
    }

    private static Text fontLabel(int style) {
        return Text.literal("Czcionka: " + FONT_NAMES[style]);
    }

    private static Text colorLabel(int color) {
        return Text.literal("Kolor: #" + String.format("%06X", color));
    }

    private static int nextColor(int current) {
        for (int i = 0; i < PALETTE.length; i++) {
            if (PALETTE[i] == current) return PALETTE[(i + 1) % PALETTE.length];
        }
        return PALETTE[0];
    }

    private class ScaleSlider extends SliderWidget {
        ScaleSlider(int x, int y, int width, int height, Text text, double value) {
            super(x, y, width, height, text, value);
        }

        public void setNormalizedValue(double value) {
            setValue(value);
        }

        @Override
        public void updateMessage() {
            setMessage(Text.translatable("pikselhud.screen.settings.scale",
                    String.format("%.1f", denormalizeScale(this.value))));
        }

        @Override
        protected void applyValue() {
            getSelected().scale = denormalizeScale(this.value);
        }
    }

    private static double normalizeScale(float scale) {
        return MathHelper.clamp((scale - 0.2f) / (10f - 0.2f), 0.0, 1.0);
    }

    private static float denormalizeScale(double normalized) {
        return (float) (0.2 + normalized * (10.0 - 0.2));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        context.drawText(this.textRenderer, Text.translatable("pikselhud.screen.settings.hint"),
                10, this.height - 20, 0xAAAAAA, false);

        drawPreview(context, "coords", "Koordynaty: 232 52 2455", working.coords);
        drawPreview(context, "fps", "FPS: 240", working.fps);
        drawPreview(context, "ping", "Ping: 42ms", working.ping);
    }

    private void drawPreview(DrawContext context, String key, String sample, ElementConfig element) {
        int color = element.enabled ? (element.color | 0xFF000000) : 0x88999999;
        boolean isSelected = key.equals(selected);
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(element.x, element.y);
        matrices.scale(element.scale, element.scale);
        context.drawText(this.textRenderer, sample, 0, 0, color, true);
        matrices.popMatrix();

        if (isSelected) {
            int w = (int) (this.textRenderer.getWidth(sample) * element.scale);
            int h = (int) (this.textRenderer.fontHeight * element.scale);
            context.drawStrokedRectangle(element.x - 2, element.y - 2, w + 4, h + 4, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) {
            String hit = elementAt(mouseX, mouseY);
            if (hit != null) {
                selected = hit;
                ElementConfig el = getSelected();
                draggingElement = hit;
                dragOffsetX = (int) mouseX - el.x;
                dragOffsetY = (int) mouseY - el.y;
                refreshSelectedControls();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (draggingElement != null) {
            ElementConfig el = getSelected();
            el.x = (int) mouseX - dragOffsetX;
            el.y = (int) mouseY - dragOffsetY;
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingElement = null;
        return super.mouseReleased(click);
    }

    private String elementAt(double mouseX, double mouseY) {
        if (hits(mouseX, mouseY, "Koordynaty: 232 52 2455", working.coords)) return "coords";
        if (hits(mouseX, mouseY, "FPS: 240", working.fps)) return "fps";
        if (hits(mouseX, mouseY, "Ping: 42ms", working.ping)) return "ping";
        return null;
    }

    private boolean hits(double mouseX, double mouseY, String sample, ElementConfig element) {
        int w = (int) (this.textRenderer.getWidth(sample) * element.scale);
        int h = (int) (this.textRenderer.fontHeight * element.scale);
        return mouseX >= element.x && mouseX <= element.x + w
                && mouseY >= element.y && mouseY <= element.y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
