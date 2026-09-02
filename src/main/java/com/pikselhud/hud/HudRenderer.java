package com.pikselhud.hud;

import com.pikselhud.config.ConfigManager;
import com.pikselhud.config.ElementConfig;
import com.pikselhud.config.HudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix3x2fStack;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) return;

        HudConfig cfg = ConfigManager.data.active;
        if (cfg.coords.enabled) {
            BlockPos pos = client.player.getBlockPos();
            drawElement(context, "Koordynaty: " + pos.getX() + " " + pos.getY() + " " + pos.getZ(), cfg.coords);
        }
        if (cfg.fps.enabled) {
            drawElement(context, "FPS: " + client.getCurrentFps(), cfg.fps);
        }
        if (cfg.ping.enabled) {
            drawElement(context, "Ping: " + getPing(client) + "ms", cfg.ping);
        }
    }

    private static int getPing(MinecraftClient client) {
        if (client.getNetworkHandler() == null || client.player == null) return 0;
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private static void drawElement(DrawContext context, String text, ElementConfig element) {
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(element.x, element.y);
        matrices.scale(element.scale, element.scale);

        Style style = Style.EMPTY
                .withBold(element.fontStyle == 1 || element.fontStyle == 3)
                .withItalic(element.fontStyle == 2 || element.fontStyle == 3);

        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(text).setStyle(style),
                0, 0, element.color | 0xFF000000, true
        );
        matrices.popMatrix();
    }
}
