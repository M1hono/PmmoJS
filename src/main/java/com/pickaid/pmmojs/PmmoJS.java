package com.pickaid.pmmojs;

import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.server.SkillsEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(PmmoJS.ID)
public class PmmoJS {
    public static final String ID = "pmmojs";
    @HideFromJS
    public static final Map<String, Map<String, Integer>> REQ_MAP = new HashMap<>();
    public static final Logger LOGGER = LogManager.getLogger();

    public PmmoJS () {
        PMMOKubeJSEvents.SKILL_CONFIG.post(new SkillsEventJS());
        FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = ctx.getModEventBus();
        modEventBus.addListener(this::setUp);
    }

    public void setUp(FMLCommonSetupEvent event) {
        PMMOKubeJSEvents.REGISTER_PREDICATE.post(new PerksRegistryEventJS());
        PMMOKubeJSEvents.REGISTER_PERK.post(new PerksRegistryEventJS());
        event.enqueueWork(PMMOKubeJSEvents::registerPerk);
    }
}
