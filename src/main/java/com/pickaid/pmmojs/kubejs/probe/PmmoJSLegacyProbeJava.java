package com.pickaid.pmmojs.kubejs.probe;

import com.pickaid.pmmojs.kubejs.events.server.api.EnchantEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.FurnaceEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOTriggerEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.SalvageEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.XPEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.AntiCheeseEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.AutoValueEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.GlobalsEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.PerksEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.ServerEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.SkillsEventJS;
import com.pickaid.pmmojs.kubejs.events.server.penalty.EntityDamagePenaltyEventJS;
import com.pickaid.pmmojs.kubejs.events.server.penalty.ItemStackDamagePenaltyEventJS;
import com.pickaid.pmmojs.kubejs.events.server.PMMOSettingEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PredicateRegistryEventJS;
import com.pickaid.pmmojs.kubejs.handlers.server.DataManager;
import com.pickaid.pmmojs.contents.PerkRegistryFactory;
import com.pickaid.pmmojs.contents.perks.PerkTagJS;
import com.pickaid.pmmojs.contents.perks.PerkConditionContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStartContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStopContextJS;
import com.pickaid.pmmojs.contents.perks.PerkTickContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStatusContextJS;
import com.pickaid.pmmojs.contents.settings.BlockSettingsBuilder;
import com.pickaid.pmmojs.contents.settings.EntitySettingsBuilder;
import com.pickaid.pmmojs.contents.settings.ItemSettingsBuilder;
import com.pickaid.pmmojs.contents.settings.LocationSettingsBuilder;
import com.pickaid.pmmojs.contents.settings.GenericSettingsBuilder;
import com.pickaid.pmmojs.contents.settings.PMMOSettingsBuilder;
import com.pickaid.pmmojs.contents.settings.nbtbuilder.BlockNBTBuilder;
import com.pickaid.pmmojs.contents.settings.nbtbuilder.EntityNBTBuilder;
import com.pickaid.pmmojs.contents.settings.nbtbuilder.ItemNBTBuilder;
import com.pickaid.pmmojs.utils.PmmoHelper;
import com.pickaid.pmmojs.utils.SkillHelper;
import com.pickaid.pmmojs.utils.ClientSkillHelper;
import com.pickaid.pmmojs.utils.NbtPathBuilder;
import com.pickaid.pmmojs.utils.CustomReqHelper;

import java.util.Set;

final class PmmoJSLegacyProbeJava {
    private static final Set<Class<?>> PROVIDED_CLASSES = Set.of(
            // Runtime event classes
            PMMOTriggerEventJS.class,
            PMMOInternalEventJS.class,
            XPEventJS.class,
            EnchantEventJS.class,
            FurnaceEventJS.class,
            SalvageEventJS.class,
            EntityDamagePenaltyEventJS.class,
            ItemStackDamagePenaltyEventJS.class,

            // Config event classes
            ServerEventJS.class,
            SkillsEventJS.class,
            PerksEventJS.class,
            GlobalsEventJS.class,
            AutoValueEventJS.class,
            AntiCheeseEventJS.class,

            // Settings event
            PMMOSettingEventJS.class,

            // Settings builders
            ItemSettingsBuilder.class,
            BlockSettingsBuilder.class,
            EntitySettingsBuilder.class,
            LocationSettingsBuilder.class,
            GenericSettingsBuilder.class,
            PMMOSettingsBuilder.class,

            // NBT builders
            ItemNBTBuilder.class,
            BlockNBTBuilder.class,
            EntityNBTBuilder.class,

            // Perk classes
            PerkRegistryFactory.class,
            PerkTagJS.class,
            PerkConditionContextJS.class,
            PerkStartContextJS.class,
            PerkStopContextJS.class,
            PerkTickContextJS.class,
            PerkStatusContextJS.class,

            // Startup events
            PerksRegistryEventJS.class,
            PredicateRegistryEventJS.class,

            // Utilities
            PmmoHelper.class,
            SkillHelper.class,
            ClientSkillHelper.class,
            NbtPathBuilder.class,
            CustomReqHelper.class,

            // Server data management
            DataManager.class
    );

    private PmmoJSLegacyProbeJava() {
    }

    static Set<Class<?>> providedClasses() {
        return PROVIDED_CLASSES;
    }
}
