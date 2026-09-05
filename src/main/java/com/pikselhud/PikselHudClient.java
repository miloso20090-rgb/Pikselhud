package com.pikselhud;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.gui.MainScreen;
import com.pikselhud.hud.HudRenderer;
import com.pikselhud.nameprotect.NameProtectManager;
import com.pikselhud.nameprotect.FriendsOutlineRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.util.Identifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PikselHudClient implements ClientModInitializer {
    public static final String MOD_ID = "pikselhud";
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        NameProtectManager.syncFromConfig(ConfigManager.data);
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pikselhud.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.pixelhub"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new MainScreen(null));
            }
        });

        HudElementRegistry.addLast(Identifier.of(MOD_ID, "hud"), (drawContext, tickCounter) -> HudRenderer.render(drawContext));
        WorldRenderEvents.AFTER_ENTITIES.register(FriendsOutlineRenderer::render);
    }
}
