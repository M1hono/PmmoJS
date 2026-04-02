package com.pickaid.pmmojs.kubejs;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.kubejs.events.server.*;
import com.pickaid.pmmojs.kubejs.events.server.api.EnchantEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.FurnaceEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalType;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOTriggerEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.SalvageEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.XPEventJS;
import com.pickaid.pmmojs.kubejs.events.server.confg.*;
import com.pickaid.pmmojs.kubejs.events.server.penalty.EntityDamagePenaltyEventJS;
import com.pickaid.pmmojs.kubejs.events.server.penalty.ItemStackDamagePenaltyEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.startup.PredicateRegistryEventJS;
import com.pickaid.pmmojs.utils.PmmoHelper;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.core.Core;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import java.util.Locale;
import java.util.function.Consumer;

import static com.pickaid.pmmojs.kubejs.PMMOKubeJSPlugin.PERKS;

public interface PMMOKubeJSEvents {
    EventGroup GROUP = EventGroup.of("PmmoJS");
    Extra SUPPORTS_TRIGGER = new Extra()
            .describeType(context -> context.javaType(EventType.class).or(TypeDescJS.STRING))
            .toString(extraId -> {
                EventType type = PmmoHelper.coerceEventType(extraId);
                return type == null ? null : type.getName();
            })
            .identity()
            .required()
            .validator(extraId -> PmmoHelper.coerceEventType(extraId) != null);
    Extra SUPPORTS_INTERNAL = new Extra()
            .describeType(context -> context.javaType(PMMOInternalType.class).or(TypeDescJS.STRING))
            .toString(extraId -> {
                PMMOInternalType type = PmmoHelper.coerceInternalType(extraId);
                return type == null ? null : type.getId();
            })
            .identity()
            .required()
            .validator(extraId -> PmmoHelper.coerceInternalType(extraId) != null);

    EventHandler REGISTER_PREDICATE = GROUP.startup("registerPredicate", () -> PredicateRegistryEventJS.class);
    EventHandler REGISTER_PERK = GROUP.startup("registerPerk", () -> PerksRegistryEventJS.class);

    EventHandler SETTINGS = GROUP.server("settings", () -> PMMOSettingEventJS.class);
    EventHandler GLOBALS_CONFIG = GROUP.server("globalsConfig", () -> GlobalsEventJS.class);
    EventHandler SKILL_CONFIG = GROUP.server("skillsConfig", () -> SkillsEventJS.class);
    EventHandler PERK_CONFIG = GROUP.server("perksConfig", () -> PerksEventJS.class);
    EventHandler SERVER_CONFIG = GROUP.server("serverConfig", () -> ServerEventJS.class);
    EventHandler AUTO_VALUE_CONFIG = GROUP.server("autoValueConfig", () -> AutoValueEventJS.class);
    EventHandler ANTI_CHEESE_CONFIG = GROUP.server("antiCheeseConfig", () -> AntiCheeseEventJS.class);

    EventHandler ENCHANT = GROUP.server("enchant", () -> EnchantEventJS.class);
    EventHandler FURNACE_BURN = GROUP.server("furnace", () -> FurnaceEventJS.class);
    EventHandler TRIGGER = GROUP.server("trigger", () -> PMMOTriggerEventJS.class).extra(SUPPORTS_TRIGGER).hasResult();
    EventHandler INTERNAL = GROUP.server("internal", () -> PMMOInternalEventJS.class).extra(SUPPORTS_INTERNAL).hasResult();
    EventHandler XP = GROUP.server("xp", () -> XPEventJS.class).hasResult();
    EventHandler XP_BY_SKILL = GROUP.server("xpBySkill", () -> XPEventJS.class).extra(Extra.REQUIRES_STRING).hasResult();
    EventHandler SALVAGE = GROUP.server("salvage", () -> SalvageEventJS.class).hasResult();
    EventHandler ITEMSTACK_DAMAGE_PENALTY = GROUP.server("itemstackDamagePenalty", () -> ItemStackDamagePenaltyEventJS.class).hasResult();
    EventHandler ENTITY_DAMAGE_PENALTY = GROUP.server("entityDamagePenalty", () -> EntityDamagePenaltyEventJS.class).hasResult();

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

    static void registerTriggerBridge() {
        for (EventType type : EventType.values()) {
            APIUtils.registerListener(PmmoHelper.id(PmmoJS.ID + ":kubejs_trigger/" + type.getName().toLowerCase(Locale.ROOT)), type, (forgeEvent, context) -> {
                return postTrigger(type, forgeEvent, context).getMutableContext();
            });
        }
    }

    static PMMOTriggerEventJS postTrigger(EventType type, Event forgeEvent, CompoundTag context) {
        CompoundTag tag = context == null ? new CompoundTag() : context;
        PMMOTriggerEventJS triggerEvent = new PMMOTriggerEventJS(type, forgeEvent, tag);
        var post = TRIGGER.post(triggerEvent, type.getName());
        if (post.interruptFalse()) {
            triggerEvent.setCancelled(true);
        }

        return triggerEvent;
    }

    static PMMOInternalEventJS postInternal(PMMOInternalType type, Event forgeEvent, Consumer<PMMOInternalEventJS> initializer) {
        PMMOInternalEventJS internalEvent = new PMMOInternalEventJS(type, forgeEvent);
        if (initializer != null) {
            initializer.accept(internalEvent);
        }

        var post = INTERNAL.post(internalEvent, type.getId());
        if (post.interruptFalse()) {
            internalEvent.setSkipPmmo(true);
        }

        internalEvent.applyActionCancellation();
        return internalEvent;
    }
}
