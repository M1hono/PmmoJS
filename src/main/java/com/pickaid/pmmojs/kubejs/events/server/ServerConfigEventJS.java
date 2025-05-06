package com.pickaid.pmmojs.kubejs.events.server;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfigEventJS extends EventJS {
    // General settings
    public static Double creativeReach;
    public static String salvageBlock;
    public static Boolean treasureEnabled;
    public static Boolean brewingTracked;

    // Level settings
    public static Integer maxLevel;
    public static Double lossOnDeath;
    public static Boolean loseLevelsOnDeath;
    public static Boolean loseOnlyExcess;
    public static Boolean useExponentialFormula;
    public static Double globalModifier;
    public static Map<String, Double> skillModifiers = new HashMap<>();

    // Linear level settings
    public static Long linearBaseXp;
    public static Double linearPerLevel;

    // Exponential level settings
    public static Integer exponentialBaseXp;
    public static Double exponentialPowerBase;
    public static Double exponentialLevelMod;

    // Requirement settings
    public static Map<ReqType, Boolean> reqEnabled = new HashMap<>();

    // XP Gain settings
    public static Double reusePenalty;
    public static Boolean summatedMaps;

    // Damage XP settings
    public static Map<String, Map<String, Long>> dealDamageXp = new HashMap<>();
    public static Map<String, Map<String, Long>> receiveDamageXp = new HashMap<>();

    // Movement XP settings
    public static Map<String, Double> jumpXp = new HashMap<>();
    public static Map<String, Double> sprintJumpXp = new HashMap<>();
    public static Map<String, Double> crouchJumpXp = new HashMap<>();

    // Player action XP settings
    public static Map<String, Double> breathChangeXp = new HashMap<>();
    public static Map<String, Double> healthChangeXp = new HashMap<>();
    public static Map<String, Double> healthIncreaseXp = new HashMap<>();
    public static Map<String, Double> healthDecreaseXp = new HashMap<>();
    public static Map<String, Double> sprintingXp = new HashMap<>();
    public static Map<String, Double> submergedXp = new HashMap<>();
    public static Map<String, Double> swimmingXp = new HashMap<>();
    public static Map<String, Double> divingXp = new HashMap<>();
    public static Map<String, Double> surfacingXp = new HashMap<>();
    public static Map<String, Double> swimSprintingXp = new HashMap<>();

    // Party settings
    public static Integer partyRange;
    public static Map<String, Double> partyBonus = new HashMap<>();

    // Mob scaling settings
    public static Boolean mobScalingEnabled;
    public static Boolean mobUseExponentialFormula;
    public static Integer mobScalingAoe;
    public static Integer mobScalingBaseLevel;
    public static Double mobLinearPerLevel;
    public static Double mobExponentialPowerBase;
    public static Double mobExponentialLevelMod;
    public static Double bossScalingRatio;
    public static Map<ResourceLocation, Map<String, Double>> mobScaling = new HashMap<>();

    // Vein miner settings
    public static Boolean veinEnabled;
    public static Boolean requireSetting;
    public static Integer defaultConsume;
    public static Double veinChargeModifier;
    public static List<String> veinBlacklist = new ArrayList<>();
    public static Double baseChargeRate;
    public static Integer baseChargeCap;

    @Info("Sets the creative mode reach distance")
    public ServerConfigEventJS setCreativeReach(double value) {
        creativeReach = value;
        return this;
    }

    @Info("Sets the block ID used for salvaging")
    public ServerConfigEventJS setSalvageBlock(String blockId) {
        salvageBlock = blockId;
        return this;
    }

    @Info("Enables or disables PMMO treasure loot conditions")
    public ServerConfigEventJS setTreasureEnabled(boolean enabled) {
        treasureEnabled = enabled;
        return this;
    }

    @Info("Enables or disables brewing tracking")
    public ServerConfigEventJS setBrewingTracked(boolean tracked) {
        brewingTracked = tracked;
        return this;
    }

    // Level settings
    @Info("Sets the maximum level for all skills")
    public ServerConfigEventJS setMaxLevel(Number level) {
        maxLevel = level.intValue();
        return this;
    }

    @Info("Sets the percentage of XP lost on death (0.0 - 1.0)")
    public ServerConfigEventJS setLossOnDeath(Number loss) {
        lossOnDeath = loss.doubleValue();
        return this;
    }

    @Info("Sets whether loss of XP can cause level loss")
    public ServerConfigEventJS setLoseLevelsOnDeath(boolean lose) {
        loseLevelsOnDeath = lose;
        return this;
    }

    @Info("Sets whether only excess XP is lost on death")
    public ServerConfigEventJS setLoseOnlyExcess(boolean onlyExcess) {
        loseOnlyExcess = onlyExcess;
        return this;
    }

    @Info("Sets whether to use exponential or linear level formula")
    public ServerConfigEventJS setUseExponentialFormula(boolean exponential) {
        useExponentialFormula = exponential;
        return this;
    }

    @Info("Sets a global XP gain modifier")
    public ServerConfigEventJS setGlobalModifier(double modifier) {
        globalModifier = modifier;
        return this;
    }

    @Info("Sets an XP modifier for a specific skill")
    public ServerConfigEventJS addSkillModifier(String skill, double modifier) {
        skillModifiers.put(skill, modifier);
        return this;
    }

    // Linear level settings
    @Info("Sets the base XP for linear level formula")
    public ServerConfigEventJS setLinearBaseXp(long baseXp) {
        linearBaseXp = baseXp;
        return this;
    }

    @Info("Sets the per level XP increase for linear level formula")
    public ServerConfigEventJS setLinearPerLevel(double perLevel) {
        linearPerLevel = perLevel;
        return this;
    }

    // Exponential level settings
    @Info("Sets the base XP for exponential level formula")
    public ServerConfigEventJS setExponentialBaseXp(int baseXp) {
        exponentialBaseXp = baseXp;
        return this;
    }

    @Info("Sets the power base for exponential level formula")
    public ServerConfigEventJS setExponentialPowerBase(double powerBase) {
        exponentialPowerBase = powerBase;
        return this;
    }

    @Info("Sets the level modifier for exponential level formula")
    public ServerConfigEventJS setExponentialLevelMod(double levelMod) {
        exponentialLevelMod = levelMod;
        return this;
    }

    // Requirement settings
    @Info("Enables or disables requirements for specific action types")
    public ServerConfigEventJS setReqEnabled(ReqType reqType, boolean enabled) {
        reqEnabled.put(reqType, enabled);
        return this;
    }

    // XP Gain settings
    @Info("Sets the reuse penalty for breaking placed blocks")
    public ServerConfigEventJS setReusePenalty(double penalty) {
        reusePenalty = penalty;
        return this;
    }

    @Info("Sets whether XP from perks and configs should be added together")
    public ServerConfigEventJS setSummatedMaps(boolean summated) {
        summatedMaps = summated;
        return this;
    }

    // Damage XP settings
    @Info("Adds an XP gain setting for dealing damage")
    public ServerConfigEventJS addDealDamageXp(String damageType, String skill, Number amount) {
        dealDamageXp.computeIfAbsent(damageType, k -> new HashMap<>()).put(skill, amount.longValue());
        return this;
    }

    @Info("Adds an XP gain setting for receiving damage")
    public ServerConfigEventJS addReceiveDamageXp(String damageType, String skill, Number amount) {
        receiveDamageXp.computeIfAbsent(damageType, k -> new HashMap<>()).put(skill, amount.longValue());
        return this;
    }

    // Movement XP settings
    @Info("Adds an XP gain setting for jumping")
    public ServerConfigEventJS addJumpXp(String skill, Number amount) {
        jumpXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for sprint jumping")
    public ServerConfigEventJS addSprintJumpXp(String skill, Number amount) {
        sprintJumpXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for crouch jumping")
    public ServerConfigEventJS addCrouchJumpXp(String skill, Number amount) {
        crouchJumpXp.put(skill, amount.doubleValue());
        return this;
    }

    // Player action XP settings
    @Info("Adds an XP gain setting for breath changes")
    public ServerConfigEventJS addBreathChangeXp(String skill, Number amount) {
        breathChangeXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for health changes")
    public ServerConfigEventJS addHealthChangeXp(String skill, Number amount) {
        healthChangeXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for health increases")
    public ServerConfigEventJS addHealthIncreaseXp(String skill, Number amount) {
        healthIncreaseXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for health decreases")
    public ServerConfigEventJS addHealthDecreaseXp(String skill, Number amount) {
        healthDecreaseXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for sprinting")
    public ServerConfigEventJS addSprintingXp(String skill, Number amount) {
        sprintingXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for being submerged")
    public ServerConfigEventJS addSubmergedXp(String skill, Number amount) {
        submergedXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for swimming")
    public ServerConfigEventJS addSwimmingXp(String skill, Number amount) {
        swimmingXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for diving")
    public ServerConfigEventJS addDivingXp(String skill, Number amount) {
        divingXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for surfacing")
    public ServerConfigEventJS addSurfacingXp(String skill, Number amount) {
        surfacingXp.put(skill, amount.doubleValue());
        return this;
    }

    @Info("Adds an XP gain setting for sprint swimming")
    public ServerConfigEventJS addSwimSprintingXp(String skill, Number amount) {
        swimSprintingXp.put(skill, amount.doubleValue());
        return this;
    }

    // Party settings
    @Info("Sets the party range for XP sharing")
    public ServerConfigEventJS setPartyRange(Number range) {
        partyRange = range.intValue();
        return this;
    }

    @Info("Adds a party bonus for a specific skill")
    public ServerConfigEventJS addPartyBonus(String skill, Number bonus) {
        partyBonus.put(skill, bonus.doubleValue());
        return this;
    }

    // Mob scaling settings
    @Info("Enables or disables mob scaling")
    public ServerConfigEventJS setMobScalingEnabled(boolean enabled) {
        mobScalingEnabled = enabled;
        return this;
    }

    @Info("Sets whether mob scaling uses exponential formula")
    public ServerConfigEventJS setMobUseExponentialFormula(boolean exponential) {
        mobUseExponentialFormula = exponential;
        return this;
    }

    @Info("Sets the mob scaling area of effect")
    public ServerConfigEventJS setMobScalingAoe(Number aoe) {
        mobScalingAoe = aoe.intValue();
        return this;
    }

    @Info("Sets the base level for mob scaling")
    public ServerConfigEventJS setMobScalingBaseLevel(Number baseLevel) {
        mobScalingBaseLevel = baseLevel.intValue();
        return this;
    }

    @Info("Sets the linear per level value for mob scaling")
    public ServerConfigEventJS setMobLinearPerLevel(Number perLevel) {
        mobLinearPerLevel = perLevel.doubleValue();
        return this;
    }

    @Info("Sets the exponential power base for mob scaling")
    public ServerConfigEventJS setMobExponentialPowerBase(Number powerBase) {
        mobExponentialPowerBase = powerBase.doubleValue();
        return this;
    }

    @Info("Sets the exponential level modifier for mob scaling")
    public ServerConfigEventJS setMobExponentialLevelMod(Number levelMod) {
        mobExponentialLevelMod = levelMod.doubleValue();
        return this;
    }

    @Info("Sets the boss scaling ratio")
    public ServerConfigEventJS setBossScalingRatio(Number ratio) {
        bossScalingRatio = ratio.doubleValue();
        return this;
    }

    @Info("Adds a mob scaling setting for an attribute")
    public ServerConfigEventJS addMobScaling(String attributeId, String skill, Number amount) {
        ResourceLocation resourceLocation = new ResourceLocation(attributeId);
        mobScaling.computeIfAbsent(resourceLocation, k -> new HashMap<>()).put(skill, amount.doubleValue());
        return this;
    }

    // Vein miner settings
    @Info("Enables or disables vein mining")
    public ServerConfigEventJS setVeinEnabled(boolean enabled) {
        veinEnabled = enabled;
        return this;
    }

    @Info("Sets whether vein mining requires explicit settings")
    public ServerConfigEventJS setRequireSetting(boolean require) {
        requireSetting = require;
        return this;
    }

    @Info("Sets the default consume value for vein mining")
    public ServerConfigEventJS setDefaultConsume(Number consume) {
        defaultConsume = consume.intValue();
        return this;
    }

    @Info("Sets the vein charge modifier")
    public ServerConfigEventJS setVeinChargeModifier(Number modifier) {
        veinChargeModifier = modifier.doubleValue();
        return this;
    }

    @Info("Adds a tool to the vein blacklist")
    public ServerConfigEventJS addVeinBlacklist(Item toolId) {
        veinBlacklist.add(toolId.kjs$getIdLocation().toString());
        return this;
    }

    @Info("Sets the base charge rate for vein mining")
    public ServerConfigEventJS setBaseChargeRate(Number rate) {
        baseChargeRate = rate.doubleValue();
        return this;
    }

    @Info("Sets the base charge capacity for vein mining")
    public ServerConfigEventJS setBaseChargeCap(Number cap) {
        baseChargeCap = cap.intValue();
        return this;
    }
}