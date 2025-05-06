package com.pickaid.pmmojs.kubejs.events.server.confg;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.features.autovalues.AutoValueConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoValueEventJS extends EventJS {
    public static Map<EventType, Map<String, Long>> customItemXpAwards = new HashMap<>();
    public static Map<EventType, Map<String, Long>> customBlockXpAwards = new HashMap<>();
    public static Map<EventType, Map<String, Long>> customEntityXpAwards = new HashMap<>();

    public static Map<EventType, List<String>> removedItemXpSkills = new HashMap<>();
    public static Map<EventType, List<String>> removedBlockXpSkills = new HashMap<>();
    public static Map<EventType, List<String>> removedEntityXpSkills = new HashMap<>();

    public static Map<String, Long> customAxeOverride;
    public static Map<String, Long> customHoeOverride;
    public static Map<String, Long> customShovelOverride;
    public static Map<String, Long> customBrewablesOverride;
    public static Map<String, Long> customSmeltablesOverride;

    public static Map<ReqType, Map<String, Integer>> customItemReqs = new HashMap<>();
    public static Map<ReqType, Map<String, Integer>> customBlockReqs = new HashMap<>();

    public static Map<ReqType, List<String>> removedItemReqSkills = new HashMap<>();
    public static Map<ReqType, List<String>> removedBlockReqSkills = new HashMap<>();

    public static Map<String, Integer> customAxeToolOverride;
    public static Map<String, Integer> customShovelToolOverride;
    public static Map<String, Integer> customHoeToolOverride;
    public static Map<String, Integer> customSwordToolOverride;

    public static Map<ResourceLocation, Integer> customItemPenalties;

    public static Map<AutoValueConfig.UtensilTypes, Map<String, Double>> customUtensilAttributes = new HashMap<>();
    public static Map<AutoValueConfig.WearableTypes, Map<String, Double>> customWearableAttributes = new HashMap<>();
    public static Map<String, Double> customEntityAttributes;

    public static Double customRaritiesModifier;
    public static Double customHardnessModifier;
    public static Boolean autoValuesEnabled;

    public AutoValueEventJS() {
        for (EventType eventType : EventType.values()) {
            customItemXpAwards.put(eventType, new HashMap<>());
            customBlockXpAwards.put(eventType, new HashMap<>());
            customEntityXpAwards.put(eventType, new HashMap<>());

            removedItemXpSkills.put(eventType, new ArrayList<>());
            removedBlockXpSkills.put(eventType, new ArrayList<>());
            removedEntityXpSkills.put(eventType, new ArrayList<>());
        }

        for (ReqType reqType : ReqType.values()) {
            customItemReqs.put(reqType, new HashMap<>());
            customBlockReqs.put(reqType, new HashMap<>());

            removedItemReqSkills.put(reqType, new ArrayList<>());
            removedBlockReqSkills.put(reqType, new ArrayList<>());
        }

        for (AutoValueConfig.UtensilTypes utensilType : AutoValueConfig.UtensilTypes.values()) {
            customUtensilAttributes.put(utensilType, new HashMap<>());
        }

        for (AutoValueConfig.WearableTypes wearableType : AutoValueConfig.WearableTypes.values()) {
            customWearableAttributes.put(wearableType, new HashMap<>());
        }
    }

    @Info("""
            Set the global auto-values enabled flag.
            """)
    public AutoValueEventJS setAutoValuesEnabled(boolean enabled) {
        autoValuesEnabled = enabled;
        return this;
    }

    @Info("""
            Set the rarities modifier which affects how much xp rare blocks like ores give.
            """)
    public AutoValueEventJS setRaritiesModifier(Number modifier) {
        customRaritiesModifier = modifier.doubleValue();
        return this;
    }

    @Info("""
            Set the hardness modifier which affects how block hardness contributes to value calculations.
            """)
    public AutoValueEventJS setHardnessModifier(double modifier) {
        customHardnessModifier = modifier;
        return this;
    }

    @Info("""
            Add a custom item XP award for a specific event type and skill.
            """)
    public AutoValueEventJS addItemXpAward(EventType eventType, String skill, Number amount) {
        customItemXpAwards.get(eventType).put(skill, amount.longValue());
        return this;
    }

    @Info("""
            Remove an item XP award skill for a specific event type.
            """)
    public AutoValueEventJS removeItemXpSkill(EventType eventType, String skill) {
        removedItemXpSkills.get(eventType).add(skill);
        return this;
    }

    @Info("""
            Add a custom block XP award for a specific event type and skill.
            """)
    public AutoValueEventJS addBlockXpAward(EventType eventType, String skill, Number amount) {
        customBlockXpAwards.get(eventType).put(skill, amount.longValue());
        return this;
    }

    @Info("""
            Remove a block XP award skill for a specific event type.
            """)
    public AutoValueEventJS removeBlockXpSkill(EventType eventType, String skill) {
        removedBlockXpSkills.get(eventType).add(skill);
        return this;
    }

    @Info("""
            Add a custom entity XP award for a specific event type and skill.
            """)
    public AutoValueEventJS addEntityXpAward(EventType eventType, String skill, Number amount) {
        customEntityXpAwards.get(eventType).put(skill, amount.longValue());
        return this;
    }

    @Info("""
            Remove an entity XP award skill for a specific event type.
            """)
    public AutoValueEventJS removeEntityXpSkill(EventType eventType, String skill) {
        removedEntityXpSkills.get(eventType).add(skill);
        return this;
    }

    @Info("""
            Set the XP award override for axe breakable blocks.
            """)
    public AutoValueEventJS setAxeOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customAxeOverride.put(entry.getKey(), entry.getValue().longValue());
        }
        return this;
    }

    @Info("""
            Set the XP award override for hoe breakable blocks.
            """)
    public AutoValueEventJS setHoeOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customHoeOverride.put(entry.getKey(), entry.getValue().longValue());
        }
        return this;
    }

    @Info("""
            Set the XP award override for shovel breakable blocks.
            """)
    public AutoValueEventJS setShovelOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customShovelOverride.put(entry.getKey(), entry.getValue().longValue());
        }
        return this;
    }

    @Info("""
            Set the XP award override for brewable items.
            """)
    public AutoValueEventJS setBrewablesOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customBrewablesOverride.put(entry.getKey(), entry.getValue().longValue());
        }
        return this;
    }

    @Info("""
            Set the XP award override for smeltable items.
            """)
    public AutoValueEventJS setSmeltablesOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customSmeltablesOverride.put(entry.getKey(), entry.getValue().longValue());
        }
        return this;
    }

    @Info("""
            Add a custom item requirement for a specific requirement type and skill.
            """)
    public AutoValueEventJS addItemReq(ReqType reqType, String skill, Number level) {
        customItemReqs.get(reqType).put(skill, level.intValue());
        return this;
    }

    @Info("""
            Remove an item requirement skill for a specific requirement type.
            """)
    public AutoValueEventJS removeItemReqSkill(ReqType reqType, String skill) {
        removedItemReqSkills.get(reqType).add(skill);
        return this;
    }

    @Info("""
            Add a custom block requirement for a specific requirement type and skill.
            """)
    public AutoValueEventJS addBlockReq(ReqType reqType, String skill, int level) {
        customBlockReqs.get(reqType).put(skill, level);
        return this;
    }

    @Info("""
            Remove a block requirement skill for a specific requirement type.
            """)
    public AutoValueEventJS removeBlockReqSkill(ReqType reqType, String skill) {
        removedBlockReqSkills.get(reqType).add(skill);
        return this;
    }

    @Info("""
            Set the requirement override for axe tools.
            """)
    public AutoValueEventJS setAxeToolOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customAxeToolOverride.put(entry.getKey(), entry.getValue().intValue());
        }
        return this;
    }

    @Info("""
            Set the requirement override for shovel tools.
            """)
    public AutoValueEventJS setShovelToolOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customShovelToolOverride.put(entry.getKey(), entry.getValue().intValue());
        }
        return this;
    }

    @Info("""
            Set the requirement override for hoe tools.
            """)
    public AutoValueEventJS setHoeToolOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customHoeToolOverride.put(entry.getKey(), entry.getValue().intValue());
        }
        return this;
    }

    @Info("""
            Set the requirement override for sword tools.
            """)
    public AutoValueEventJS setSwordToolOverride(Map<String, Number> override) {
        for (Map.Entry<String, Number> entry : override.entrySet()) {
            customSwordToolOverride.put(entry.getKey(), entry.getValue().intValue());
        }
        return this;
    }

    @Info("""
            Set the item penalties for failing to meet requirements.
            """)
    public AutoValueEventJS setItemPenalties(Map<ResourceLocation, Number> penalties) {
        for (Map.Entry<ResourceLocation, Number> entry : penalties.entrySet()) {
            customItemPenalties.put(entry.getKey(), entry.getValue().intValue());
        }
        return this;
    }

    @Info("""
            Add a custom attribute for a specific utensil type.
            """)
    public AutoValueEventJS addUtensilAttribute(AutoValueConfig.UtensilTypes utensilType, String attributeKey, Number value) {
        customUtensilAttributes.get(utensilType).put(attributeKey, value.doubleValue());
        return this;
    }

    @Info("""
            Add a custom attribute for a specific wearable type.
            """)
    public AutoValueEventJS addWearableAttribute(AutoValueConfig.WearableTypes wearableType, String attributeKey, Number value) {
        customWearableAttributes.get(wearableType).put(attributeKey, value.doubleValue());
        return this;
    }

    @Info("""
            Set the entity attributes.
            """)
    public AutoValueEventJS setEntityAttributes(Map<String, Number> attributes) {
        for (Map.Entry<String, Number> entry : attributes.entrySet()) {
            customEntityAttributes.put(entry.getKey(), entry.getValue().doubleValue());
        }
        return this;
    }
}