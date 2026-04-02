package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.PistonHandler;
import net.minecraftforge.event.level.PistonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/level/PistonEvent$Pre;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgePiston(PistonEvent.Pre event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handlePiston(event)) {
            ci.cancel();
        }
    }
}
