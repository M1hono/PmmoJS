package com.pickaid.pmmojs.contents.settings;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ObjectType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Info("Builder for entity-related PMMO settings")
public class EntitySettingsBuilder extends PMMOSettingsBuilder {
    public EntitySettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }

    @Info("Registers damage-specific XP awards")
    public EntitySettingsBuilder damageXp(boolean isDealt, String damageType, Map<String, ? extends Number> xpAwards) {
        // Convert Number values to Long
        Map<String, Long> longXpAwards = new HashMap<>();
        for (Map.Entry<String, ? extends Number> entry : xpAwards.entrySet()) {
            longXpAwards.put(entry.getKey(), entry.getValue().longValue());
        }

        APIUtils.registerDamageXpAward(objectType, objectId, isDealt, damageType, longXpAwards, isOverride);
        return this;
    }

    @Info("Registers XP for dealing specific damage type")
    public EntitySettingsBuilder dealDamageXp(String damageType, Map<String, ? extends Number> xpAwards) {
        return damageXp(true, damageType, xpAwards);
    }

    @Info("Registers XP for receiving specific damage type")
    public EntitySettingsBuilder receiveDamageXp(String damageType, Map<String, ? extends Number> xpAwards) {
        return damageXp(false, damageType, xpAwards);
    }

    @Info("Sets a specific skill XP award for dealing damage")
    public EntitySettingsBuilder setDealDamageXp(String damageType, String skill, Number amount) {
        Map<String, Long> xps = new HashMap<>();
        xps.put(skill, amount.longValue());
        return dealDamageXp(damageType, xps);
    }

    @Info("Sets a specific skill XP award for receiving damage")
    public EntitySettingsBuilder setReceiveDamageXp(String damageType, String skill, Number amount) {
        Map<String, Long> xps = new HashMap<>();
        xps.put(skill, amount.longValue());
        return receiveDamageXp(damageType, xps);
    }
}