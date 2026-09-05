package com.pikselhud.mixin;

import com.pikselhud.nameprotect.NameProtectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void pikselhud$maskOwnTabSkin(CallbackInfoReturnable<PlayerSkinTexture> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PlayerListEntry entry = (PlayerListEntry) (Object) this;
        if (entry.getProfile() != null
                && entry.getProfile().id().equals(client.player.getUuid())
                && NameProtectManager.shouldReplaceSelfSkin()) {
            cir.setReturnValue(DefaultSkinHelper.getSteve());
        }
    }
}
