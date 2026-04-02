package com.pickaid.pmmojs.mixin;

import com.pickaid.pmmojs.kubejs.handlers.server.PMMOInternalHandlerBridge;
import harmonised.pmmo.events.impl.MountHandler;
import net.minecraftforge.event.entity.EntityMountEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MountHandler.class)
public abstract class MountHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/entity/EntityMountEvent;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$bridgeMount(EntityMountEvent event, CallbackInfo ci) {
        if (PMMOInternalHandlerBridge.handleMount(event)) {
            ci.cancel();
        }
    }
}
