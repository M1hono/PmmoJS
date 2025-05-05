package com.pickaid.pmmojs.contents.settings;

import com.pickaid.pmmojs.contents.settings.nbtbuilder.EntityNBTBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Info("Builder for entity-related PMMO settings")
public class EntitySettingsBuilder extends PMMOSettingsBuilder {
    public EntitySettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }

    @Info("""
            Sets the object as an override.
            You must enable this to change vanilla stuff.
            """)
    public EntitySettingsBuilder override(boolean override) {
        this.isOverride = override;
        return this;
    }

    @Info("Creates a builder for NBT requirements for entities")
    public EntityNBTBuilder nbtRequirement(ReqType reqType) {
        return new EntityNBTBuilder(this, objectType, objectId, reqType, isOverride);
    }

    @Info("Creates a builder for NBT-based XP values for entities")
    public EntityNBTBuilder nbtXp(EventType eventType) {
        return new EntityNBTBuilder(this, objectType, objectId, eventType, isOverride);
    }

    @Info("Creates a builder for NBT-based bonuses for entities")
    public EntityNBTBuilder nbtBonus(ModifierDataType modifierType) {
        return new EntityNBTBuilder(this, objectType, objectId, modifierType, isOverride);
    }

    @Info("Registers damage-specific XP awards")
    public EntitySettingsBuilder damageXp(boolean isDealt, String damageType, Map<String, ? extends Number> xpAwards) {
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