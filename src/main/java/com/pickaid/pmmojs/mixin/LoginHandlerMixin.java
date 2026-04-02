package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.LoginHandler;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoginHandler.class)
public abstract class LoginHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgeLogin(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handleLogin(event)) {
            ci.cancel();
        }
    }
}
