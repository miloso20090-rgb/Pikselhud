package com.pikselhud.mixin;

import com.pikselhud.nameprotect.NameProtectManager;
import com.pikselhud.nameprotect.NameProtectMode;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState", at = @At("RETURN"))
    private void pikselhud$protectPlayerRenderState(PlayerEntity player, PlayerEntityRenderState state, float tickProgress, CallbackInfo ci) {
        if (player == null || state == null) return;
        String realName = player.getGameProfile().name();

        if (NameProtectManager.targets(realName)) {
            Text replacement = NameProtectManager.isFriend(realName)
                    ? NameProtectManager.friendNameText(realName)
                    : NameProtectManager.replacementText(realName);
            state.playerName = replacement;
        } else if (NameProtectManager.isFriend(realName)) {
            state.playerName = NameProtectManager.friendNameText(realName);
            state.outlineColor = 0x00FF00;
        }

        if (NameProtectManager.getMode() == NameProtectMode.SELF && player.isMainPlayer()) {
            state.skinTextures = DefaultSkinHelper.getSteve();
        }
    }
}
