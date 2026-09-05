package com.pikselhud.mixin;

import com.pikselhud.nameprotect.NameProtectManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At("HEAD"), argsOnly = true)
    private ChatHudLine pikselhud$transformChat(ChatHudLine message) {
        if (message == null || !NameProtectManager.isEnabled()) return message;
        Text transformed = NameProtectManager.transformChat(message.content());
        if (transformed == message.content()) return message;
        return new ChatHudLine(message.creationTick(), transformed, message.signature(), message.indicator());
    }
}
