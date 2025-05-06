package com.pickaid.pmmojs.kubejs;

import com.pickaid.pmmojs.kubejs.events.server.*;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PredicateRegistryEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import harmonised.pmmo.core.Core;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import static com.pickaid.pmmojs.kubejs.PMMOKubeJSPlugin.PERKS;

public interface PMMOKubeJSEvents {
    EventGroup GROUP = EventGroup.of("PmmoJS");

    EventHandler REGISTER_PREDICATE = GROUP.startup("registerPredicate", () -> PredicateRegistryEventJS.class);
    EventHandler REGISTER_PERK = GROUP.startup("registerPerk", () -> PerksRegistryEventJS.class);

    EventHandler SETTINGS = GROUP.server("settings", () -> PMMOSettingEventJS.class);
    EventHandler SKILL_CONFIG = GROUP.server("skillsConfig", () -> SkillsEventJS.class);
    EventHandler PERK_CONFIG = GROUP.server("perksConfig", () -> PerksEventJS.class);
    EventHandler SERVER_CONFIG = GROUP.server("serverConfig", () -> ServerConfigEventJS.class);
    EventHandler AUTO_VALUE_CONFIG = GROUP.server("autoValueConfig", () -> AutoValueEventJS.class);

    EventHandler ENCHANT = GROUP.server("enchant", () -> EnchantEventJS.class);
    EventHandler FURNACE_BURN = GROUP.server("furnace", () -> FurnaceEventJS.class);
    EventHandler XP = GROUP.server("xp", () -> XPEventJS.class).hasResult();

    static void registerPerk() {
        for (ResourceLocation perkId : PERKS.keySet()) {
            if (PERKS.get(perkId).side().equals(PerksRegistryEventJS.Side.CLIENT)) {
                Core.get(LogicalSide.CLIENT).getPerkRegistry().registerPerk(perkId, PERKS.get(perkId).perk());
            } else if (PERKS.get(perkId).side().equals(PerksRegistryEventJS.Side.SERVER)) {
                Core.get(LogicalSide.SERVER).getPerkRegistry().registerPerk(perkId, PERKS.get(perkId).perk());
                Core.get(LogicalSide.CLIENT).getPerkRegistry().registerClientClone(perkId, PERKS.get(perkId).perk());
            } else if (PERKS.get(perkId).side().equals(PerksRegistryEventJS.Side.BOTH)) {
                Core.get(LogicalSide.CLIENT).getPerkRegistry().registerPerk(perkId, PERKS.get(perkId).perk());
                Core.get(LogicalSide.SERVER).getPerkRegistry().registerPerk(perkId, PERKS.get(perkId).perk());
            }
        }
    }
}
