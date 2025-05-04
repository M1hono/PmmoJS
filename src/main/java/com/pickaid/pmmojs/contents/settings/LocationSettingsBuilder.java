package com.pickaid.pmmojs.contents.settings;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.config.codecs.MobModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Info("Builder for location-related PMMO settings (biomes and dimensions)")
public class LocationSettingsBuilder extends PMMOSettingsBuilder {
    public LocationSettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }

    @Info("Registers positive effect bonuses")
    public LocationSettingsBuilder positiveEffects(Map<ResourceLocation, ? extends Number> effects) {
        Map<ResourceLocation, Integer> intEffects = new HashMap<>();
        for (Map.Entry<ResourceLocation, ? extends Number> entry : effects.entrySet()) {
            intEffects.put(entry.getKey(), entry.getValue().intValue());
        }

        APIUtils.registerPositiveEffect(objectType, objectId, intEffects, isOverride);
        return this;
    }

    @Info("Registers negative effect penalties")
    public LocationSettingsBuilder negativeEffects(Map<ResourceLocation, ? extends Number> effects) {
        Map<ResourceLocation, Integer> intEffects = new HashMap<>();
        for (Map.Entry<ResourceLocation, ? extends Number> entry : effects.entrySet()) {
            intEffects.put(entry.getKey(), entry.getValue().intValue());
        }

        APIUtils.registerNegativeEffect(objectType, objectId, intEffects, isOverride);
        return this;
    }

    @Info("Sets a specific positive effect")
    public LocationSettingsBuilder setPositiveEffect(String effectId, Number level) {
        Map<ResourceLocation, Integer> effects = new HashMap<>();
        effects.put(new ResourceLocation(effectId), level.intValue());
        return positiveEffects(effects);
    }

    @Info("Sets a specific negative effect")
    public LocationSettingsBuilder setNegativeEffect(String effectId, Number level) {
        Map<ResourceLocation, Integer> effects = new HashMap<>();
        effects.put(new ResourceLocation(effectId), level.intValue());
        return negativeEffects(effects);
    }

    @Info("Registers XP gain bonuses for the specified modifier type")
    public LocationSettingsBuilder xpBonus(ModifierDataType modifierType, Map<String, ? extends Number> bonuses) {
        Map<String, Double> doubleBonuses = new HashMap<>();
        for (Map.Entry<String, ? extends Number> entry : bonuses.entrySet()) {
            doubleBonuses.put(entry.getKey(), entry.getValue().doubleValue());
        }

        APIUtils.registerBonus(objectType, objectId, modifierType, doubleBonuses, isOverride);
        return this;
    }

    @Info("Sets a specific skill XP bonus for the specified modifier type")
    public LocationSettingsBuilder setXpBonus(ModifierDataType modifierType, String skill, Number multiplier) {
        Map<String, Double> bonuses = new HashMap<>();
        bonuses.put(skill, multiplier.doubleValue());
        return xpBonus(modifierType, bonuses);
    }

    @Info("Creates a new mob modifier configuration for the specified entity")
    public MobModifierBuilder mobModifier(String entityId) {
        return new MobModifierBuilder(this, new ResourceLocation(entityId));
    }

    @Info("Builder for configuring mob modifiers")
    public class MobModifierBuilder {
        private final LocationSettingsBuilder parent;
        private final ResourceLocation entityId;
        private final List<MobModifier> modifiers = new ArrayList<>();
        private final Map<ResourceLocation, List<MobModifier>> entityModifiers = new HashMap<>();

        private MobModifierBuilder(LocationSettingsBuilder parent, ResourceLocation entityId) {
            this.parent = parent;
            this.entityId = entityId;
            this.entityModifiers.put(entityId, modifiers);
        }

        @Info("Adds a mob attribute modifier with the specified attribute, amount, and operation")
        public MobModifierBuilder attribute(String attributeId, Number amount, AttributeModifier.Operation operation) {
            MobModifier modifier = new MobModifier(new ResourceLocation(attributeId), amount.doubleValue(), operation);
            modifiers.add(modifier);
            return this;
        }

        @Info("Adds a health attribute modifier with the specified multiplier")
        public MobModifierBuilder health(Number multiplier) {
            return attribute("minecraft:generic.max_health", multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        @Info("Adds a damage attribute modifier with the specified multiplier")
        public MobModifierBuilder damage(Number multiplier) {
            return attribute("minecraft:generic.attack_damage", multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        @Info("Adds a speed attribute modifier with the specified multiplier")
        public MobModifierBuilder speed(Number multiplier) {
            return attribute("minecraft:generic.movement_speed", multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        }

        @Info("Completes the mob modifier configuration and returns to the main settings builder")
        public LocationSettingsBuilder done() {
            if (!entityModifiers.isEmpty()) {
                APIUtils.registerMobModifier(parent.objectType, parent.objectId, entityModifiers, parent.isOverride);
            }
            return parent;
        }

        @Info("Adds another entity to configure mob modifiers for")
        public MobModifierBuilder and(String entityId) {
            return parent.mobModifier(entityId);
        }
    }
}