package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.PotionHandler;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionHandler.class)
public abstract class PotionHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/brewing/PlayerBrewedPotionEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgePotionBrew(PlayerBrewedPotionEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handlePotionBrew(event)) {
            ci.cancel();
        }
    }
}
