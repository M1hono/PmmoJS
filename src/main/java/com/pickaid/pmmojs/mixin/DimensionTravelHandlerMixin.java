package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.DimensionTravelHandler;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DimensionTravelHandler.class)
public abstract class DimensionTravelHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/entity/EntityTravelToDimensionEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgeDimensionTravel(EntityTravelToDimensionEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handleDimensionTravel(event)) {
            ci.cancel();
        }
    }
}
