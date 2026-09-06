package com.pikselhud.mixin;

import com.pikselhud.nameprotect.NameProtectManager;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void pikselhud$replacePlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (entry == null) return;
        String realName = entry.getProfile().name();
        if (NameProtectManager.targets(realName)) {
            if (NameProtectManager.isFriend(realName)) {
                cir.setReturnValue(NameProtectManager.friendNameText(realName));
            } else {
                cir.setReturnValue(NameProtectManager.replacementText(entry));
            }
        } else if (NameProtectManager.isFriend(realName)) {
            cir.setReturnValue(NameProtectManager.friendNameText(realName));
        }
    }
}
