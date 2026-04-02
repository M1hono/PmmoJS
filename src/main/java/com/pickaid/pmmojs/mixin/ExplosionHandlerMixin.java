package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.ExplosionHandler;
import net.minecraftforge.event.level.ExplosionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosionHandler.class)
public abstract class ExplosionHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/level/ExplosionEvent$Detonate;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgeExplosion(ExplosionEvent.Detonate event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handleExplosion(event)) {
            ci.cancel();
        }
    }
}
