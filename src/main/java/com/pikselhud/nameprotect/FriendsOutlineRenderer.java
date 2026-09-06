package com.pikselhud.nameprotect;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public final class FriendsOutlineRenderer {
    private FriendsOutlineRenderer() {}

    public static void render(WorldRenderContext context) {
        if (!NameProtectManager.isEnabled() || NameProtectManager.getMode() != NameProtectMode.FRIENDS) return;
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null || context.world() == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        var camera = context.camera();
        var cameraPos = camera.getPos();
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayers.LINES);

        for (PlayerEntity player : context.world().getPlayers()) {
            if (!NameProtectManager.isFriend(player.getGameProfile().name())) continue;
            if (player == client.player && NameProtectManager.getMode() != NameProtectMode.FRIENDS) continue;

            Box box = player.getBoundingBox().expand(0.025)
                    .offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            matrices.push();
            VertexRendering.drawBox(matrices, vertexConsumer, box, 0.0f, 1.0f, 0.0f, 1.0f);
            matrices.pop();
        }
    }
}
