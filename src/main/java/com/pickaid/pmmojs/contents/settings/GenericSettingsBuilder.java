package com.pickaid.pmmojs.contents.settings;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Info("Generic builder for other PMMO settings")
public class GenericSettingsBuilder extends PMMOSettingsBuilder {
    public GenericSettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }

    @Info("Sets bonus modifiers for this object")
    public GenericSettingsBuilder bonus(ModifierDataType type, Map<String, Double> bonuses) {
        if (objectType == ObjectType.ITEM || objectType == ObjectType.BIOME ||
                objectType == ObjectType.DIMENSION || objectType == ObjectType.PLAYER) {
            APIUtils.registerBonus(objectType, objectId, type, bonuses, isOverride);
        }
        return this;
    }

    @Info("Sets a specific skill bonus modifier")
    public GenericSettingsBuilder setBonus(ModifierDataType type, String skill, double value) {
        Map<String, Double> bonuses = new HashMap<>();
        bonuses.put(skill, value);
        return bonus(type, bonuses);
    }
}