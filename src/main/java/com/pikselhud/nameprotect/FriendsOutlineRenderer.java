package com.pikselhud.nameprotect;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;

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
            VertexRendering.drawOutline(
                    matrices,
                    vertexConsumer,
                    VoxelShapes.cuboid(box),
                    0.0,
                    0.0,
                    0.0,
                    0xFF00FF00,
                    2.0f
            );
            matrices.pop();
        }
    }
}
