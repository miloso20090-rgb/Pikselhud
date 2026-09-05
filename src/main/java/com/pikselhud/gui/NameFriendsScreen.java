package com.pikselhud.gui;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.nameprotect.NameProtectMode;
import com.pikselhud.nameprotect.NameProtectManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NameFriendsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget replacementField;
    private TextFieldWidget friendsField;
    private NameProtectMode workingMode;

    public NameFriendsScreen(Screen parent) {
        super(Text.translatable("pikselhud.screen.namefriends.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        workingMode = NameProtectManager.getMode();

        replacementField = new TextFieldWidget(this.textRenderer, cx - 140, 55, 280, 20, Text.translatable("pikselhud.screen.namefriends.replacement"));
        replacementField.setMaxLength(32);
        replacementField.setText(NameProtectManager.getReplacement());
        this.addDrawableChild(replacementField);

        friendsField = new TextFieldWidget(this.textRenderer, cx - 140, 115, 280, 45, Text.translatable("pikselhud.screen.namefriends.friends"));
        friendsField.setMaxLength(512);
        friendsField.setText(NameProtectManager.getFriendsRaw());
        this.addDrawableChild(friendsField);

        addModeButton(cx - 140, 85, 65, NameProtectMode.OFF, "OFF");
        addModeButton(cx - 70, 85, 65, NameProtectMode.SELF, "SELF");
        addModeButton(cx, 85, 65, NameProtectMode.ALL, "ALL");
        addModeButton(cx + 70, 85, 70, NameProtectMode.FRIENDS, "FRIENDS");

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.namefriends.save"), b -> saveAndClose())
                .dimensions(cx - 140, this.height - 45, 135, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pikselhud.screen.namefriends.cancel"), b -> this.client.setScreen(parent))
                .dimensions(cx + 5, this.height - 45, 135, 20).build());
    }

    private void addModeButton(int x, int y, int w, NameProtectMode mode, String label) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal(label + (workingMode == mode ? " ✓" : "")), b -> {
            workingMode = mode;
            this.clearAndInit();
        }).dimensions(x, y, w, 20).build());
    }

    private void saveAndClose() {
        NameProtectManager.setMode(workingMode);
        NameProtectManager.setReplacement(replacementField.getText());
        NameProtectManager.setFriendsRaw(friendsField.getText());
        ConfigManager.data.nameProtect.mode = workingMode;
        ConfigManager.data.nameProtect.replacement = replacementField.getText();
        ConfigManager.data.nameProtect.friends = friendsField.getText();
        ConfigManager.save();
        if (this.client.inGameHud != null) this.client.inGameHud.getChatHud().reset();
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawText(this.textRenderer, Text.translatable("pikselhud.screen.namefriends.replacement_hint"), this.width / 2 - 140, 42, 0xAAAAAA, false);
        context.drawText(this.textRenderer, Text.translatable("pikselhud.screen.namefriends.friends_hint"), this.width / 2 - 140, 98, 0xAAAAAA, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
