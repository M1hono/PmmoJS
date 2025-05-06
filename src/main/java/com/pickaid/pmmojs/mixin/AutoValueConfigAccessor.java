package com.pickaid.pmmojs.mixin;

import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.readers.TomlConfigHelper;
import harmonised.pmmo.features.autovalues.AutoValueConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AutoValueConfig.class)
public interface AutoValueConfigAccessor {
    @Accessor(value = "ITEM_XP_AWARDS", remap = false)
    static Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getItemXpAwards() { throw new AssertionError(); }

    @Accessor(value = "BLOCK_XP_AWARDS", remap = false)
    static Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getBlockXpAwards() { throw new AssertionError(); }

    @Accessor(value = "ENTITY_XP_AWARDS", remap = false)
    static Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getEntityXpAwards() { throw new AssertionError(); }

    @Accessor(value = "ITEM_REQS", remap = false)
    static Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> getItemReqs() { throw new AssertionError(); }

    @Accessor(value = "BLOCK_REQS", remap = false)
    static Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> getBlockReqs() { throw new AssertionError(); }

    @Accessor(value = "AXE_TOOL_OVERRIDE", remap = false)
    static TomlConfigHelper.ConfigObject<Map<String, Integer>> getAxeToolOverride() { throw new AssertionError(); }

    @Accessor(value = "SHOVEL_TOOL_OVERRIDE", remap = false)
    static TomlConfigHelper.ConfigObject<Map<String, Integer>> getShovelToolOverride() { throw new AssertionError(); }

    @Accessor(value = "HOE_TOOL_OVERRIDE", remap = false)
    static TomlConfigHelper.ConfigObject<Map<String, Integer>> getHoeToolOverride() { throw new AssertionError(); }

    @Accessor(value = "SWORD_TOOL_OVERRIDE", remap = false)
    static TomlConfigHelper.ConfigObject<Map<String, Integer>> getSwordToolOverride() { throw new AssertionError(); }

    @Accessor(value = "UTENSIL_ATTRIBUTES", remap = false)
    static Map<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> getUtensilAttributes() { throw new AssertionError(); }

    @Accessor(value = "WEARABLE_ATTRIBUTES", remap = false)
    static Map<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> getWearableAttributes() { throw new AssertionError(); }
}