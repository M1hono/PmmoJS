package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.SleepHandler;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SleepHandler.class)
public abstract class SleepHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/level/SleepFinishedTimeEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgeSleep(SleepFinishedTimeEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handleSleep(event)) {
            ci.cancel();
        }
    }
}
