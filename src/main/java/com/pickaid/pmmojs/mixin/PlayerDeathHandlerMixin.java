package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.PlayerDeathHandler;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerDeathHandler.class)
public abstract class PlayerDeathHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/entity/living/LivingDeathEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgePlayerDeath(LivingDeathEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handlePlayerDeath(event)) {
            ci.cancel();
        }
    }
}
