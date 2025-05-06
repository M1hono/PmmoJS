package com.pickaid.pmmojs.contents;

import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.readers.TomlConfigHelper;
import harmonised.pmmo.features.autovalues.AutoValueConfig;

import java.util.Map;

public interface IAutoValueConfigAccessor {
    Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getItemXpAwards();
    Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getBlockXpAwards();
    Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> getEntityXpAwards();

    Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> getItemReqs();
    Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> getBlockReqs();

    TomlConfigHelper.ConfigObject<Map<String, Integer>> getAxeToolOverride();
    TomlConfigHelper.ConfigObject<Map<String, Integer>> getShovelToolOverride();
    TomlConfigHelper.ConfigObject<Map<String, Integer>> getHoeToolOverride();
    TomlConfigHelper.ConfigObject<Map<String, Integer>> getSwordToolOverride();

    Map<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> getUtensilAttributes();
    Map<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> getWearableAttributes();
}