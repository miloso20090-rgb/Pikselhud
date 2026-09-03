package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PresetsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;

    public PresetsScreen(Screen parent) {
        super(Text.translatable("pikselhud.screen.presets.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = 30;

        nameField = new TextFieldWidget(this.textRenderer, centerX - 100, top, 150, 20, Text.literal("Nazwa presetu"));
        nameField.setMaxLength(24);
        this.addDrawableChild(nameField);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.presets.save_current"), b -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                ConfigManager.data.presets.put(name, ConfigManager.data.active.copy());
                ConfigManager.save();
                nameField.setText("");
                this.client.setScreen(new PresetsScreen(parent));
            }
        }).dimensions(centerX + 55, top, 90, 20).build());

        int y = top + 34;
        List<String> presetNames = new ArrayList<>(ConfigManager.data.presets.keySet());
        for (String name : presetNames) {
            HudConfig preset = ConfigManager.data.presets.get(name);

            this.addDrawableChild(ButtonWidget.builder(Text.literal(name), b -> {})
                    .dimensions(centerX - 155, y, 140, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.presets.apply"), b -> {
                ConfigManager.data.active = preset.copy();
                ConfigManager.save();
            }).dimensions(centerX - 10, y, 80, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.presets.delete"), b -> {
                ConfigManager.data.presets.remove(name);
                ConfigManager.save();
                this.client.setScreen(new PresetsScreen(parent));
            }).dimensions(centerX + 75, y, 80, 20).build());

            y += 24;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Wroc"), b -> this.client.setScreen(parent))
                .dimensions(centerX - 155, this.height - 55, 310, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.presets.reload"), b -> {
            ConfigManager.load();
            this.client.setScreen(new PresetsScreen(parent));
        }).dimensions(centerX - 155, this.height - 30, 310, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        if (ConfigManager.data.presets.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Brak zapisanych presetow"), this.width / 2, 70, 0x999999);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
