package com.pikselhud.nameprotect;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public final class FriendsOutlineRenderer {
    private FriendsOutlineRenderer() {}

    public static void render(WorldRenderContext context) {
        if (!NameProtectManager.isEnabled() || NameProtectManager.getMode() != NameProtectMode.FRIENDS) return;

        MatrixStack matrices = context.matrices();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        var camera = client.gameRenderer.getCamera();
        var cameraPos = camera.getCameraPos();
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayers.LINES);

        for (PlayerEntity player : client.world.getPlayers()) {
            if (!NameProtectManager.isFriend(player.getGameProfile().name())) continue;

            Box box = player.getBoundingBox().expand(0.025)
                    .offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            matrices.push();
            WorldRenderer.drawBox(
                    matrices,
                    vertexConsumer,
                    box.minX, box.minY, box.minZ,
                    box.maxX, box.maxY, box.maxZ,
                    0.0f, 1.0f, 0.0f, 1.0f
            );
            matrices.pop();
        }
    }
}
