package com.pickaid.pmmojs;

import com.pickaid.pmmojs.config.PmmoJSCommonConfig;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.confg.SkillsEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PredicateRegistryEventJS;
import com.pickaid.pmmojs.kubejs.probe.PmmoJSLegacyProbeCompat;
import com.pickaid.pmmojs.kubejs.probe.PmmoJSProbeCompat;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.config.ModConfig;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PmmoJSCommonConfig.SPEC);
        FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = ctx.getModEventBus();
        modEventBus.addListener(this::setUp);

        if (ModList.get().isLoaded("probejs")) {
            MinecraftForge.EVENT_BUS.addListener(PmmoJS::installProbeCompatAfterReload);
        }
        if (ModList.get().isLoaded("probejs_legacy")) {
            PmmoJSLegacyProbeCompat.install();
        }
    }

    public void setUp(FMLCommonSetupEvent event) {
        PMMOKubeJSEvents.REGISTER_PREDICATE.post(new PredicateRegistryEventJS());
        PMMOKubeJSEvents.REGISTER_PERK.post(new PerksRegistryEventJS());
        event.enqueueWork(() -> {
            PMMOKubeJSEvents.registerPerk();
            PMMOKubeJSEvents.registerTriggerBridge();
        });
    }

    private static void installProbeCompatAfterReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            PmmoJSProbeCompat.reinstallAfterServerReload();
        }
    }
}
