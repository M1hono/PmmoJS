package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.api.EnchantEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.FurnaceEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.SalvageEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.XPEventJS;
import harmonised.pmmo.api.events.EnchantEvent;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import harmonised.pmmo.api.events.SalvageEvent;
import harmonised.pmmo.api.events.XpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NormalHandler {
    @SubscribeEvent
    public static void onEnchant(EnchantEvent event) {
        PMMOKubeJSEvents.ENCHANT.post(new EnchantEventJS(event));
    }

    @SubscribeEvent
    public static void onFurnaceBurn(FurnaceBurnEvent event) {
        PMMOKubeJSEvents.FURNACE_BURN.post(new FurnaceEventJS(event));
    }

    @SubscribeEvent
    public static void onXp(XpEvent event) {
        var cancelled = PMMOKubeJSEvents.XP.post(new XPEventJS(event)).interruptFalse();
        cancelled = PMMOKubeJSEvents.XP_BY_SKILL.post(new XPEventJS(event), event.skill).interruptFalse() || cancelled;
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSalvage(SalvageEvent event) {
        var salvageEvent = PMMOKubeJSEvents.SALVAGE.post(new SalvageEventJS(event));
        if (salvageEvent.interruptFalse()) {
            event.setCanceled(true);
        }
    }
}
