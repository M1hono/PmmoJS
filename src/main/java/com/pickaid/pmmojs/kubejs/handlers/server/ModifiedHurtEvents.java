package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.api.events.EntityDamagePenaltyEvent;
import com.pickaid.pmmojs.api.events.ItemStackDamagePenaltyEvent;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.penalty.EntityDamagePenaltyEventJS;
import com.pickaid.pmmojs.kubejs.events.server.penalty.ItemStackDamagePenaltyEventJS;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.util.Messenger;
import harmonised.pmmo.util.MsLoggy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModifiedHurtEvents {
    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        if (event.getSource().getEntity() != null) {
            Entity var2 = event.getSource().getEntity();
            if (var2 instanceof Player) {
                Player player = (Player)var2;
                LivingEntity target = event.getEntity();
                if (target == null) {
                    return;
                }

                if (target.equals(player)) {
                    return;
                }

                Core core = Core.get(player.level());
                MsLoggy.INFO.log(MsLoggy.LOG_CODE.EVENT, "Attack Type: " + EventType.DEAL_DAMAGE.name() + " | TargetType: " + target.getType().toString(), new Object[0]);
                if (!core.isActionPermitted(ReqType.WEAPON, player.getMainHandItem(), player)) {
                    ItemStackDamagePenaltyEvent itemStackDamagePenaltyEvent = new ItemStackDamagePenaltyEvent(event.getAmount(), player, player.getMainHandItem());
                    MinecraftForge.EVENT_BUS.post(itemStackDamagePenaltyEvent);
                    var itemStackDamagePenaltyEventJS = PMMOKubeJSEvents.ITEMSTACK_DAMAGE_PENALTY.post(new ItemStackDamagePenaltyEventJS(itemStackDamagePenaltyEvent));
                    if (itemStackDamagePenaltyEvent.isCanceled() || itemStackDamagePenaltyEventJS.interruptFalse()) {
                        event.setCanceled(true);
                    }
                    event.setAmount(itemStackDamagePenaltyEvent.getDamage());
                    Messenger.sendDenialMsg(ReqType.WEAPON, player, new Object[]{player.getMainHandItem().getDisplayName()});
                    return;
                }

                if (!core.isActionPermitted(ReqType.KILL, target, player)) {
                    EntityDamagePenaltyEvent entityDamagePenaltyEvent = new EntityDamagePenaltyEvent(event.getAmount(), player, target);
                    var entityDamagePenaltyEventJS = PMMOKubeJSEvents.ENTITY_DAMAGE_PENALTY.post(new EntityDamagePenaltyEventJS(entityDamagePenaltyEvent));
                    if (entityDamagePenaltyEvent.isCanceled() || entityDamagePenaltyEventJS.interruptFalse()) {
                        event.setCanceled(true);
                    }
                    event.setAmount(entityDamagePenaltyEvent.getDamage());
                    Messenger.sendDenialMsg(ReqType.KILL, player, new Object[]{target.getDisplayName()});
                    return;
                }

                boolean serverSide = !player.level().isClientSide;
                new CompoundTag();
                if (serverSide) {
                    CompoundTag eventHookOutput = core.getEventTriggerRegistry().executeEventListeners(EventType.DEAL_DAMAGE, event, new CompoundTag());
                    if (eventHookOutput.getBoolean("is_cancelled")) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
}
