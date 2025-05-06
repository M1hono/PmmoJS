package com.pickaid.pmmojs.mixin;

import harmonised.pmmo.events.impl.DamageDealtHandler;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageDealtHandler.class)
public abstract class DamageDealtHandlerMixin {
    @Inject(method = "handle(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void pmmoJS$onDamageDealt(LivingAttackEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
