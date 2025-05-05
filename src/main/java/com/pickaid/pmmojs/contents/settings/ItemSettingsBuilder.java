package com.pickaid.pmmojs.contents.settings;

import com.pickaid.pmmojs.contents.settings.nbtbuilder.ItemNBTBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.APIUtils.SalvageBuilder;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Info("Builder for item-related PMMO settings")
public class ItemSettingsBuilder extends PMMOSettingsBuilder {
    private final Map<ResourceLocation, SalvageBuilder> salvageData = new HashMap<>();
    private Optional<Number> veinChargeCap = Optional.empty();
    private Optional<Number> veinChargeRate = Optional.empty();
    private Optional<Number> veinConsumeAmount = Optional.empty();

    public ItemSettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }

    @Info("Creates a builder for NBT requirements for items")
    public ItemNBTBuilder nbtRequirement(ReqType reqType) {
        return new ItemNBTBuilder(this, objectType, objectId, reqType, isOverride);
    }

    @Info("Creates a builder for NBT-based XP values for items")
    public ItemNBTBuilder nbtXp(EventType eventType) {
        return new ItemNBTBuilder(this, objectType, objectId, eventType, isOverride);
    }

    @Info("Creates a builder for NBT-based bonuses for items")
    public ItemNBTBuilder nbtBonus(ModifierDataType modifierType) {
        return new ItemNBTBuilder(this, objectType, objectId, modifierType, isOverride);
    }

    @Info("""
            Sets the object as an override.
            You must enable this to change vanilla stuff.
            """)
    public ItemSettingsBuilder override(boolean override) {
        this.isOverride = override;
        return this;
    }

    @Info("Creates a new salvage configuration for the specified result item")
    public SalvageConfigBuilder salvage(Item resultItem) {
        return new SalvageConfigBuilder(this, resultItem.kjs$getIdLocation());
    }

    @Info("Sets the vein miner charge capacity")
    public ItemSettingsBuilder veinChargeCap(Number chargeCap) {
        this.veinChargeCap = Optional.of(chargeCap);
        registerVeinData();
        return this;
    }

    @Info("Sets the vein miner charge rate")
    public ItemSettingsBuilder veinChargeRate(Number chargeRate) {
        this.veinChargeRate = Optional.of(chargeRate);
        registerVeinData();
        return this;
    }

    @Info("Sets the vein miner consume amount")
    public ItemSettingsBuilder veinConsumeAmount(Number consumeAmount) {
        this.veinConsumeAmount = Optional.of(consumeAmount);
        registerVeinData();
        return this;
    }

    @HideFromJS
    private void registerVeinData() {
        Optional<Integer> intChargeCap = veinChargeCap.map(num -> num.intValue());
        Optional<Double> doubleChargeRate = veinChargeRate.map(num -> num.doubleValue());
        Optional<Integer> intConsumeAmount = veinConsumeAmount.map(num -> num.intValue());

        APIUtils.registerVeinData(objectType, objectId, intChargeCap, doubleChargeRate, intConsumeAmount, isOverride);
    }

    @HideFromJS
    ItemSettingsBuilder registerSalvage() {
        if (!salvageData.isEmpty()) {
            APIUtils.registerSalvage(objectId, salvageData, isOverride);
        }
        return this;
    }

    @Info("Registers positive effect bonuses")
    public ItemSettingsBuilder positiveEffects(Map<MobEffect, ? extends Number> effects) {
        Map<ResourceLocation, Integer> intEffects = new HashMap<>();
        for (Map.Entry<MobEffect, ? extends Number> entry : effects.entrySet()) {
            intEffects.put(ForgeRegistries.MOB_EFFECTS.getKey(entry.getKey()), entry.getValue().intValue());
        }

        APIUtils.registerPositiveEffect(objectType, objectId, intEffects, isOverride);
        return this;
    }

    @Info("Registers negative effect penalties")
    public ItemSettingsBuilder negativeEffects(Map<MobEffect, ? extends Number> effects) {
        Map<ResourceLocation, Integer> intEffects = new HashMap<>();
        for (Map.Entry<MobEffect, ? extends Number> entry : effects.entrySet()) {
            intEffects.put(ForgeRegistries.MOB_EFFECTS.getKey(entry.getKey()), entry.getValue().intValue());
        }

        APIUtils.registerNegativeEffect(objectType, objectId, intEffects, isOverride);
        return this;
    }

    @Info("Sets a specific positive effect")
    public ItemSettingsBuilder setPositiveEffect(MobEffect effect, Number level) {
        Map<MobEffect, Integer> effects = new HashMap<>();
        effects.put(effect, level.intValue());
        return positiveEffects(effects);
    }

    @Info("Sets a specific negative effect")
    public ItemSettingsBuilder setNegativeEffect(MobEffect effect, Number level) {
        Map<MobEffect, Integer> effects = new HashMap<>();
        effects.put(effect, level.intValue());
        return negativeEffects(effects);
    }

    @Info("Registers damage-specific XP awards")
    public ItemSettingsBuilder damageXp(boolean isDealt, String damageType, Map<String, ? extends Number> xpAwards) {
        Map<String, Long> longXpAwards = new HashMap<>();
        for (Map.Entry<String, ? extends Number> entry : xpAwards.entrySet()) {
            longXpAwards.put(entry.getKey(), entry.getValue().longValue());
        }

        APIUtils.registerDamageXpAward(objectType, objectId, isDealt, damageType, longXpAwards, isOverride);
        return this;
    }

    @Info("Registers XP for dealing specific damage type")
    public ItemSettingsBuilder dealDamageXp(String damageType, Map<String, ? extends Number> xpAwards) {
        return damageXp(true, damageType, xpAwards);
    }

    @Info("Registers XP for receiving specific damage type")
    public ItemSettingsBuilder receiveDamageXp(String damageType, Map<String, ? extends Number> xpAwards) {
        return damageXp(false, damageType, xpAwards);
    }

    @Info("Sets a specific skill XP award for dealing damage")
    public ItemSettingsBuilder setDealDamageXp(String damageType, String skill, Number amount) {
        Map<String, Long> xps = new HashMap<>();
        xps.put(skill, amount.longValue());
        return dealDamageXp(damageType, xps);
    }

    @Info("Sets a specific skill XP award for receiving damage")
    public ItemSettingsBuilder setReceiveDamageXp(String damageType, String skill, Number amount) {
        Map<String, Long> xps = new HashMap<>();
        xps.put(skill, amount.longValue());
        return receiveDamageXp(damageType, xps);
    }

    @Info("Registers XP gain bonuses for the specified modifier type")
    public ItemSettingsBuilder xpBonus(ModifierDataType modifierType, Map<String, ? extends Number> bonuses) {
        Map<String, Double> doubleBonuses = new HashMap<>();
        for (Map.Entry<String, ? extends Number> entry : bonuses.entrySet()) {
            doubleBonuses.put(entry.getKey(), entry.getValue().doubleValue());
        }

        APIUtils.registerBonus(objectType, objectId, modifierType, doubleBonuses, isOverride);
        return this;
    }

    @Info("Sets a specific skill XP bonus for the specified modifier type")
    public ItemSettingsBuilder setXpBonus(ModifierDataType modifierType, String skill, Number multiplier) {
        Map<String, Double> bonuses = new HashMap<>();
        bonuses.put(skill, multiplier.doubleValue());
        return xpBonus(modifierType, bonuses);
    }

    @Info("Builder for configuring salvage settings")
    public class SalvageConfigBuilder {
        private final ItemSettingsBuilder parent;
        private final ResourceLocation resultItem;
        private final SalvageBuilder salvageBuilder;

        private Map<String, Double> chancePerLevel = new HashMap<>();
        private Map<String, Integer> levelReq = new HashMap<>();
        private Map<String, Long> xpAward = new HashMap<>();
        private int salvageMax = 1;
        private double baseChance = 0.0;
        private double maxChance = 1.0;

        private SalvageConfigBuilder(ItemSettingsBuilder parent, ResourceLocation resultItem) {
            this.parent = parent;
            this.resultItem = resultItem;

            this.salvageBuilder = SalvageBuilder.start()
                    .setChancePerLevel(chancePerLevel)
                    .setLevelReq(levelReq)
                    .setXpAward(xpAward)
                    .setSalvageMax(salvageMax)
                    .setBaseChance(baseChance)
                    .setMaxChance(maxChance);

            salvageData.put(resultItem, salvageBuilder);
        }

        @Info("Sets the chance per level for the salvage operation")
        public SalvageConfigBuilder chancePerLevel(Map<String, ? extends Number> chancePerLevel) {
            Map<String, Double> doubleChancePerLevel = new HashMap<>();
            for (Map.Entry<String, ? extends Number> entry : chancePerLevel.entrySet()) {
                doubleChancePerLevel.put(entry.getKey(), entry.getValue().doubleValue());
            }

            this.chancePerLevel = doubleChancePerLevel;
            salvageBuilder.setChancePerLevel(doubleChancePerLevel);
            return this;
        }

        @Info("Sets a specific skill's chance per level")
        public SalvageConfigBuilder setChancePerLevel(String skill, Number chance) {
            Map<String, Double> updated = new HashMap<>(chancePerLevel);
            updated.put(skill, chance.doubleValue());
            this.chancePerLevel = updated;
            salvageBuilder.setChancePerLevel(updated);
            return this;
        }

        @Info("Sets the level requirements for the salvage operation")
        public SalvageConfigBuilder levelRequirement(Map<String, ? extends Number> levelReq) {
            Map<String, Integer> intLevelReq = new HashMap<>();
            for (Map.Entry<String, ? extends Number> entry : levelReq.entrySet()) {
                intLevelReq.put(entry.getKey(), entry.getValue().intValue());
            }

            this.levelReq = intLevelReq;
            salvageBuilder.setLevelReq(intLevelReq);
            return this;
        }

        @Info("Sets a specific skill's level requirement")
        public SalvageConfigBuilder setLevelRequirement(String skill, Number level) {
            Map<String, Integer> updated = new HashMap<>(levelReq);
            updated.put(skill, level.intValue());
            this.levelReq = updated;
            salvageBuilder.setLevelReq(updated);
            return this;
        }

        @Info("Sets the XP awards for the salvage operation")
        public SalvageConfigBuilder xpAward(Map<String, ? extends Number> xpAward) {
            Map<String, Long> longXpAward = new HashMap<>();
            for (Map.Entry<String, ? extends Number> entry : xpAward.entrySet()) {
                longXpAward.put(entry.getKey(), entry.getValue().longValue());
            }

            this.xpAward = longXpAward;
            salvageBuilder.setXpAward(longXpAward);
            return this;
        }

        @Info("Sets a specific skill's XP award")
        public SalvageConfigBuilder setXpAward(String skill, Number amount) {
            Map<String, Long> updated = new HashMap<>(xpAward);
            updated.put(skill, amount.longValue());
            this.xpAward = updated;
            salvageBuilder.setXpAward(updated);
            return this;
        }

        @Info("Sets the maximum number of items that can be salvaged")
        public SalvageConfigBuilder salvageMax(Number max) {
            this.salvageMax = max.intValue();
            salvageBuilder.setSalvageMax(max.intValue());
            return this;
        }

        @Info("Sets the base chance for the salvage operation")
        public SalvageConfigBuilder baseChance(Number chance) {
            this.baseChance = chance.doubleValue();
            salvageBuilder.setBaseChance(chance.doubleValue());
            return this;
        }

        @Info("Sets the maximum chance for the salvage operation")
        public SalvageConfigBuilder maxChance(Number chance) {
            this.maxChance = chance.doubleValue();
            salvageBuilder.setMaxChance(chance.doubleValue());
            return this;
        }

        @Info("Completes the salvage configuration and returns to the main settings builder")
        public ItemSettingsBuilder done() {
            return parent.registerSalvage();
        }

        @Info("Adds another salvage result item to configure")
        public SalvageConfigBuilder and(Item resultItem) {
            return parent.salvage(resultItem);
        }
    }
}