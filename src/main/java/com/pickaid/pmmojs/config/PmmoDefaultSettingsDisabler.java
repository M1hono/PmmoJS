package com.pickaid.pmmojs.config;

import com.pickaid.pmmojs.PmmoJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.codecs.SkillData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PmmoDefaultSettingsDisabler {
    private PmmoDefaultSettingsDisabler() {
    }

    public static void applyDefaultSettingsResets() {
        PmmoDefaultSettingsPolicy policy = PmmoJSCommonConfig.defaultSettingsPolicy();
        if (policy.disableRequirements()) {
            int items = resetRequirements(ObjectType.ITEM);
            int blocks = resetRequirements(ObjectType.BLOCK);
            int entities = resetRequirements(ObjectType.ENTITY);
            PmmoJS.LOGGER.info("PmmoJS common config disabled default PMMO requirements for {} items, {} blocks, and {} entity types", items, blocks, entities);
        }

        if (policy.disableXpAwards()) {
            int items = resetXpAwards(ObjectType.ITEM);
            int blocks = resetXpAwards(ObjectType.BLOCK);
            int entities = resetXpAwards(ObjectType.ENTITY);
            PmmoJS.LOGGER.info("PmmoJS common config disabled default PMMO XP-award data for {} items, {} blocks, and {} entity types", items, blocks, entities);
        }

        if (policy.disableItemExtras()) {
            int items = resetItemExtras();
            PmmoJS.LOGGER.info("PmmoJS common config disabled default PMMO item extras for {} items", items);
        }

        if (policy.disableBlockVeinData()) {
            int blocks = resetBlockVeinData();
            PmmoJS.LOGGER.info("PmmoJS common config disabled default PMMO block vein data for {} blocks", blocks);
        }
    }

    public static void applySkillDefaults(Map<String, SkillData> skills) {
        if (PmmoJSCommonConfig.defaultSettingsPolicy().disableSkills()) {
            int removed = skills.size();
            skills.clear();
            PmmoJS.LOGGER.info("PmmoJS common config removed {} default PMMO skills before custom skill edits were applied", removed);
        }
    }

    public static void applyPerkDefaults(Map<EventType, List<CompoundTag>> perksMap) {
        if (!PmmoJSCommonConfig.defaultSettingsPolicy().disablePerks()) {
            return;
        }

        int removed = perksMap.values().stream().mapToInt(List::size).sum();
        for (EventType eventType : EventType.values()) {
            perksMap.put(eventType, new ArrayList<>());
        }
        PmmoJS.LOGGER.info("PmmoJS common config cleared {} default PMMO perk entries before custom perk edits were applied", removed);
    }

    public static int resetRequirements(ObjectType objectType) {
        int count = 0;
        for (ResourceLocation id : idsFor(objectType)) {
            for (ReqType reqType : ReqType.values()) {
                if (isReqApplicable(objectType, reqType)) {
                    APIUtils.registerRequirement(objectType, id, reqType, emptyIntMap(), true);
                }
            }
            count++;
        }
        return count;
    }

    public static int resetXpAwards(ObjectType objectType) {
        int count = 0;
        for (ResourceLocation id : idsFor(objectType)) {
            for (EventType eventType : EventType.values()) {
                if (isEventApplicable(objectType, eventType)) {
                    APIUtils.registerXpAward(objectType, id, eventType, emptyLongMap(), true);
                }
            }
            count++;
        }
        return count;
    }

    public static int resetItemExtras() {
        int count = 0;
        for (ResourceLocation id : BuiltInRegistries.ITEM.keySet()) {
            for (ModifierDataType modifierType : ModifierDataType.values()) {
                APIUtils.registerBonus(ObjectType.ITEM, id, modifierType, emptyDoubleMap(), true);
            }

            APIUtils.registerPositiveEffect(ObjectType.ITEM, id, emptyResourceIntMap(), true);
            APIUtils.registerNegativeEffect(ObjectType.ITEM, id, emptyResourceIntMap(), true);
            APIUtils.registerSalvage(id, emptySalvageMap(), true);
            APIUtils.registerVeinData(ObjectType.ITEM, id, Optional.empty(), Optional.empty(), Optional.empty(), true);
            count++;
        }
        return count;
    }

    public static int resetBlockVeinData() {
        int count = 0;
        for (ResourceLocation id : BuiltInRegistries.BLOCK.keySet()) {
            APIUtils.registerVeinData(ObjectType.BLOCK, id, Optional.empty(), Optional.empty(), Optional.empty(), true);
            count++;
        }
        return count;
    }

    private static Iterable<ResourceLocation> idsFor(ObjectType objectType) {
        return switch (objectType) {
            case ITEM -> BuiltInRegistries.ITEM.keySet();
            case BLOCK -> BuiltInRegistries.BLOCK.keySet();
            case ENTITY -> BuiltInRegistries.ENTITY_TYPE.keySet();
            default -> List.of();
        };
    }

    private static boolean isReqApplicable(ObjectType objectType, ReqType reqType) {
        return switch (objectType) {
            case ITEM -> reqType.itemApplicable;
            case BLOCK -> reqType.blockApplicable;
            case ENTITY -> reqType.entityApplicable;
            default -> false;
        };
    }

    private static boolean isEventApplicable(ObjectType objectType, EventType eventType) {
        return switch (objectType) {
            case ITEM -> eventType.itemApplicable;
            case BLOCK -> eventType.blockApplicable;
            case ENTITY -> eventType.entityApplicable;
            default -> false;
        };
    }

    private static Map<String, Integer> emptyIntMap() {
        return new LinkedHashMap<>();
    }

    private static Map<String, Long> emptyLongMap() {
        return new LinkedHashMap<>();
    }

    private static Map<String, Double> emptyDoubleMap() {
        return new LinkedHashMap<>();
    }

    private static Map<ResourceLocation, Integer> emptyResourceIntMap() {
        return new LinkedHashMap<>();
    }

    private static Map<ResourceLocation, APIUtils.SalvageBuilder> emptySalvageMap() {
        return new LinkedHashMap<>();
    }
}
