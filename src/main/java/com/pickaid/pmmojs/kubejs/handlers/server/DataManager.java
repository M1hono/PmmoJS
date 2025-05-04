package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.PMMOSettingEventJS;
import harmonised.pmmo.api.events.PMMORegistrationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PmmoJS.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataManager {
    @SubscribeEvent
    public static void onSettingsSyncEvent(PMMORegistrationEvent event) {
        PMMOKubeJSEvents.SETTINGS.post(new PMMOSettingEventJS());
    }
}