package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.config.PmmoDefaultSettingsDisabler;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.*;
import com.pickaid.pmmojs.kubejs.events.server.confg.*;
import com.pickaid.pmmojs.mixin.AutoValueConfigAccessor;
import com.pickaid.pmmojs.mixin.ConfigObjectAccessor;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.api.events.PMMORegistrationEvent;
import harmonised.pmmo.config.Config;
import harmonised.pmmo.config.GlobalsConfig;
import harmonised.pmmo.config.PerksConfig;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.SkillData;
import harmonised.pmmo.config.readers.TomlConfigHelper;
import harmonised.pmmo.features.anticheese.AntiCheeseConfig;
import harmonised.pmmo.features.anticheese.CheeseTracker;
import harmonised.pmmo.features.autovalues.AutoValueConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = PmmoJS.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataManager {
    public static boolean loaded = false;

    @SubscribeEvent
    public static void onSettingsSyncEvent(PMMORegistrationEvent event) {
        PmmoDefaultSettingsDisabler.applyDefaultSettingsResets();
        PMMOKubeJSEvents.SETTINGS.post(new PMMOSettingEventJS());
    }

    @SubscribeEvent
    public static void onServerLoaded(ServerStartedEvent event) {
        loaded = true;
        updateConfigs();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        loaded = false;
    }

    @SubscribeEvent
    public static void updateConfigs(TagsUpdatedEvent event) {
        if (loaded && event.shouldUpdateStaticData()) {
            updateConfigs();
        }
    }

    public static void updateConfigs() {
        if (!loaded) return;
        updateGlobalsConfig();
        updateSkillsConfig();
        updatePerksConfig();
        updateServerConfig();
        updateAutoValuesConfig();
        updateAntiCheeseConfig();
    }

    private static void updateSkillsConfig() {
        PMMOKubeJSEvents.SKILL_CONFIG.post(new SkillsEventJS());

        Map<String, SkillData> updatedSkills = copyMap(SkillsConfig.SKILLS.get());
        PmmoDefaultSettingsDisabler.applySkillDefaults(updatedSkills);
        SkillsEventJS.removedSkills.forEach(updatedSkills::remove);
        updatedSkills.putAll(SkillsEventJS.customSkills);
        setConfigObject(SkillsConfig.SKILLS, updatedSkills);
    }

    private static void updatePerksConfig() {
        PMMOKubeJSEvents.PERK_CONFIG.post(new PerksEventJS());

        Map<EventType, List<CompoundTag>> perksMap = copyPerkSettings(PerksConfig.PERK_SETTINGS.get());
        PmmoDefaultSettingsDisabler.applyPerkDefaults(perksMap);

        for (EventType eventType : PerksEventJS.clearedPerks) {
            perksMap.put(eventType, new ArrayList<>());
        }

        for (String perkType : PerksEventJS.removedPerkTypes) {
            for (EventType eventType : EventType.values()) {
                List<CompoundTag> perksList = perksMap.getOrDefault(eventType, Collections.emptyList());
                perksList.removeIf(tag -> {
                    boolean matches = tag.getString("perk").equals(perkType);
                    if (matches) {
                        ConsoleJS.SERVER.info("Removing perk type: " + perkType +
                                " for skill: " + tag.getString("skill") +
                                " from event: " + eventType.name());
                    }
                    return matches;
                });
            }
        }

        for (EventType eventType : EventType.values()) {
            List<CompoundTag> currentPerks = perksMap.getOrDefault(eventType, Collections.emptyList());
            currentPerks.removeIf(tag -> {
                for (CompoundTag removedTag : PerksEventJS.removedPerks) {
                    if (arePerksEqual(tag, removedTag)) {
                        ConsoleJS.SERVER.info("Removing perk: " + removedTag.getString("perk") +
                                " for skill: " + removedTag.getString("skill") +
                                " from event: " + eventType.name());
                        return true;
                    }
                }
                return false;
            });
        }

        for (Map.Entry<EventType, List<CompoundTag>> entry : PerksEventJS.customPerks.entrySet()) {
            EventType eventType = entry.getKey();
            List<CompoundTag> perksToAdd = entry.getValue();
            if (!perksToAdd.isEmpty()) {
                List<CompoundTag> existingPerks = perksMap.computeIfAbsent(eventType, key -> new ArrayList<>());
                for (CompoundTag newPerk : perksToAdd) {
                    existingPerks.add(newPerk.copy());
                    ConsoleJS.SERVER.info("Adding perk: " + newPerk.getString("perk") +
                            " for skill: " + newPerk.getString("skill") +
                            " to event: " + eventType.name());
                }
            }
        }

        setConfigObject(PerksConfig.PERK_SETTINGS, perksMap);
    }

    private static boolean arePerksEqual(CompoundTag tag1, CompoundTag tag2) {
        return tag1.getString("perk").equals(tag2.getString("perk")) &&
                tag1.getString("skill").equals(tag2.getString("skill"));
    }

    private static void updateServerConfig() {
        PMMOKubeJSEvents.SERVER_CONFIG.post(new ServerEventJS());
        if (ServerEventJS.creativeReach != null) {
            Config.CREATIVE_REACH.set(ServerEventJS.creativeReach);
        }

        if (ServerEventJS.salvageBlock != null) {
            Config.SALVAGE_BLOCK.set(ServerEventJS.salvageBlock);
        }

        if (ServerEventJS.treasureEnabled != null) {
            Config.TREASURE_ENABLED.set(ServerEventJS.treasureEnabled);
        }

        if (ServerEventJS.brewingTracked != null) {
            Config.BREWING_TRACKED.set(ServerEventJS.brewingTracked);
        }

        if (ServerEventJS.maxLevel != null) {
            Config.MAX_LEVEL.set(ServerEventJS.maxLevel);
        }

        if (ServerEventJS.lossOnDeath != null) {
            Config.LOSS_ON_DEATH.set(ServerEventJS.lossOnDeath);
        }

        if (ServerEventJS.loseLevelsOnDeath != null) {
            Config.LOSE_LEVELS_ON_DEATH.set(ServerEventJS.loseLevelsOnDeath);
        }

        if (ServerEventJS.loseOnlyExcess != null) {
            Config.LOSE_ONLY_EXCESS.set(ServerEventJS.loseOnlyExcess);
        }

        if (ServerEventJS.useExponentialFormula != null) {
            Config.USE_EXPONENTIAL_FORMULA.set(ServerEventJS.useExponentialFormula);
        }

        if (ServerEventJS.globalModifier != null) {
            Config.GLOBAL_MODIFIER.set(ServerEventJS.globalModifier);
        }

        if (!ServerEventJS.skillModifiers.isEmpty()) {
            mergeMapConfig(Config.SKILL_MODIFIERS, ServerEventJS.skillModifiers, Collections.emptyList());
        }

        if (ServerEventJS.linearBaseXp != null) {
            Config.LINEAR_BASE_XP.set(ServerEventJS.linearBaseXp);
        }

        if (ServerEventJS.linearPerLevel != null) {
            Config.LINEAR_PER_LEVEL.set(ServerEventJS.linearPerLevel);
        }

        if (ServerEventJS.exponentialBaseXp != null) {
            Config.EXPONENTIAL_BASE_XP.set(ServerEventJS.exponentialBaseXp);
        }

        if (ServerEventJS.exponentialPowerBase != null) {
            Config.EXPONENTIAL_POWER_BASE.set(ServerEventJS.exponentialPowerBase);
        }

        if (ServerEventJS.exponentialLevelMod != null) {
            Config.EXPONENTIAL_LEVEL_MOD.set(ServerEventJS.exponentialLevelMod);
        }

        for (Map.Entry<ReqType, Boolean> entry : ServerEventJS.reqEnabled.entrySet()) {
            Config.reqEnabled(entry.getKey()).set(entry.getValue());
        }

        if (ServerEventJS.reusePenalty != null) {
            Config.REUSE_PENALTY.set(ServerEventJS.reusePenalty);
        }

        if (ServerEventJS.summatedMaps != null) {
            Config.SUMMATED_MAPS.set(ServerEventJS.summatedMaps);
        }

        if (!ServerEventJS.dealDamageXp.isEmpty()) {
            mergeNestedMapConfig(Config.DEAL_DAMAGE_XP, ServerEventJS.dealDamageXp);
        }

        if (!ServerEventJS.receiveDamageXp.isEmpty()) {
            mergeNestedMapConfig(Config.RECEIVE_DAMAGE_XP, ServerEventJS.receiveDamageXp);
        }

        if (!ServerEventJS.jumpXp.isEmpty()) {
            mergeMapConfig(Config.JUMP_XP, ServerEventJS.jumpXp, Collections.emptyList());
        }

        if (!ServerEventJS.sprintJumpXp.isEmpty()) {
            mergeMapConfig(Config.SPRINT_JUMP_XP, ServerEventJS.sprintJumpXp, Collections.emptyList());
        }

        if (!ServerEventJS.crouchJumpXp.isEmpty()) {
            mergeMapConfig(Config.CROUCH_JUMP_XP, ServerEventJS.crouchJumpXp, Collections.emptyList());
        }

        if (!ServerEventJS.breathChangeXp.isEmpty()) {
            mergeMapConfig(Config.BREATH_CHANGE_XP, ServerEventJS.breathChangeXp, Collections.emptyList());
        }

        if (!ServerEventJS.healthChangeXp.isEmpty()) {
            mergeMapConfig(Config.HEALTH_CHANGE_XP, ServerEventJS.healthChangeXp, Collections.emptyList());
        }

        if (!ServerEventJS.healthIncreaseXp.isEmpty()) {
            mergeMapConfig(Config.HEALTH_INCREASE_XP, ServerEventJS.healthIncreaseXp, Collections.emptyList());
        }

        if (!ServerEventJS.healthDecreaseXp.isEmpty()) {
            mergeMapConfig(Config.HEALTH_DECREASE_XP, ServerEventJS.healthDecreaseXp, Collections.emptyList());
        }

        if (!ServerEventJS.sprintingXp.isEmpty()) {
            mergeMapConfig(Config.SPRINTING_XP, ServerEventJS.sprintingXp, Collections.emptyList());
        }

        if (!ServerEventJS.submergedXp.isEmpty()) {
            mergeMapConfig(Config.SUBMERGED_XP, ServerEventJS.submergedXp, Collections.emptyList());
        }

        if (!ServerEventJS.swimmingXp.isEmpty()) {
            mergeMapConfig(Config.SWIMMING_XP, ServerEventJS.swimmingXp, Collections.emptyList());
        }

        if (!ServerEventJS.divingXp.isEmpty()) {
            mergeMapConfig(Config.DIVING_XP, ServerEventJS.divingXp, Collections.emptyList());
        }

        if (!ServerEventJS.surfacingXp.isEmpty()) {
            mergeMapConfig(Config.SURFACING_XP, ServerEventJS.surfacingXp, Collections.emptyList());
        }

        if (!ServerEventJS.swimSprintingXp.isEmpty()) {
            mergeMapConfig(Config.SWIM_SPRINTING_XP, ServerEventJS.swimSprintingXp, Collections.emptyList());
        }

        if (ServerEventJS.partyRange != null) {
            Config.PARTY_RANGE.set(ServerEventJS.partyRange);
        }

        if (!ServerEventJS.partyBonus.isEmpty()) {
            mergeMapConfig(Config.PARTY_BONUS, ServerEventJS.partyBonus, Collections.emptyList());
        }

        if (ServerEventJS.mobScalingEnabled != null) {
            Config.MOB_SCALING_ENABLED.set(ServerEventJS.mobScalingEnabled);
        }

        if (ServerEventJS.mobUseExponentialFormula != null) {
            Config.MOB_USE_EXPONENTIAL_FORMULA.set(ServerEventJS.mobUseExponentialFormula);
        }

        if (ServerEventJS.mobScalingAoe != null) {
            Config.MOB_SCALING_AOE.set(ServerEventJS.mobScalingAoe);
        }

        if (ServerEventJS.mobScalingBaseLevel != null) {
            Config.MOB_SCALING_BASE_LEVEL.set(ServerEventJS.mobScalingBaseLevel);
        }

        if (ServerEventJS.mobLinearPerLevel != null) {
            Config.MOB_LINEAR_PER_LEVEL.set(ServerEventJS.mobLinearPerLevel);
        }

        if (ServerEventJS.mobExponentialPowerBase != null) {
            Config.MOB_EXPONENTIAL_POWER_BASE.set(ServerEventJS.mobExponentialPowerBase);
        }

        if (ServerEventJS.mobExponentialLevelMod != null) {
            Config.MOB_EXPONENTIAL_LEVEL_MOD.set(ServerEventJS.mobExponentialLevelMod);
        }

        if (ServerEventJS.bossScalingRatio != null) {
            Config.BOSS_SCALING_RATIO.set(ServerEventJS.bossScalingRatio);
        }

        if (!ServerEventJS.mobScaling.isEmpty()) {
            mergeNestedMapConfig(Config.MOB_SCALING, ServerEventJS.mobScaling);
        }

        if (ServerEventJS.veinEnabled != null) {
            Config.VEIN_ENABLED.set(ServerEventJS.veinEnabled);
        }

        if (ServerEventJS.requireSetting != null) {
            Config.REQUIRE_SETTING.set(ServerEventJS.requireSetting);
        }

        if (ServerEventJS.defaultConsume != null) {
            Config.DEFAULT_CONSUME.set(ServerEventJS.defaultConsume);
        }

        if (ServerEventJS.veinChargeModifier != null) {
            Config.VEIN_CHARGE_MODIFIER.set(ServerEventJS.veinChargeModifier);
        }

        if (!ServerEventJS.veinBlacklist.isEmpty()) {
            List<String> veinBlacklist = new ArrayList<>(Config.VEIN_BLACKLIST.get());
            veinBlacklist.addAll(ServerEventJS.veinBlacklist);
            Config.VEIN_BLACKLIST.set(veinBlacklist);
        }

        if (ServerEventJS.baseChargeRate != null) {
            Config.BASE_CHARGE_RATE.set(ServerEventJS.baseChargeRate);
        }

        if (ServerEventJS.baseChargeCap != null) {
            Config.BASE_CHARGE_CAP.set(ServerEventJS.baseChargeCap);
        }
    }
    private static void updateAutoValuesConfig() {
        PMMOKubeJSEvents.AUTO_VALUE_CONFIG.post(new AutoValueEventJS());
        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> itemXpAwards = AutoValueConfigAccessor.getItemXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : itemXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customItemXpAwards.getOrDefault(eventType, Collections.emptyMap()),
                    AutoValueEventJS.removedItemXpSkills.getOrDefault(eventType, Collections.emptyList()));
        }

        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> blockXpAwards = AutoValueConfigAccessor.getBlockXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : blockXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customBlockXpAwards.getOrDefault(eventType, Collections.emptyMap()),
                    AutoValueEventJS.removedBlockXpSkills.getOrDefault(eventType, Collections.emptyList()));
        }

        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entityXpAwards = AutoValueConfigAccessor.getEntityXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : entityXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customEntityXpAwards.getOrDefault(eventType, Collections.emptyMap()),
                    AutoValueEventJS.removedEntityXpSkills.getOrDefault(eventType, Collections.emptyList()));
        }

        if (!AutoValueEventJS.customAxeOverride.isEmpty()) {
            setConfigObject(AutoValueConfig.AXE_OVERRIDE, copyMap(AutoValueEventJS.customAxeOverride));
        }

        if (!AutoValueEventJS.customHoeOverride.isEmpty()) {
            setConfigObject(AutoValueConfig.HOE_OVERRIDE, copyMap(AutoValueEventJS.customHoeOverride));
        }

        if (!AutoValueEventJS.customShovelOverride.isEmpty()) {
            setConfigObject(AutoValueConfig.SHOVEL_OVERRIDE, copyMap(AutoValueEventJS.customShovelOverride));
        }

        if (!AutoValueEventJS.customBrewablesOverride.isEmpty()) {
            setConfigObject(AutoValueConfig.BREWABLES_OVERRIDE, copyMap(AutoValueEventJS.customBrewablesOverride));
        }

        if (!AutoValueEventJS.customSmeltablesOverride.isEmpty()) {
            setConfigObject(AutoValueConfig.SMELTABLES_OVERRIDE, copyMap(AutoValueEventJS.customSmeltablesOverride));
        }

        Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> itemReqs = AutoValueConfigAccessor.getItemReqs();
        for (Map.Entry<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> entry : itemReqs.entrySet()) {
            ReqType reqType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customItemReqs.getOrDefault(reqType, Collections.emptyMap()),
                    AutoValueEventJS.removedItemReqSkills.getOrDefault(reqType, Collections.emptyList()));
        }

        Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> blockReqs = AutoValueConfigAccessor.getBlockReqs();
        for (Map.Entry<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> entry : blockReqs.entrySet()) {
            ReqType reqType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customBlockReqs.getOrDefault(reqType, Collections.emptyMap()),
                    AutoValueEventJS.removedBlockReqSkills.getOrDefault(reqType, Collections.emptyList()));
        }

        if (!AutoValueEventJS.customAxeToolOverride.isEmpty()) {
            setConfigObject(AutoValueConfigAccessor.getAxeToolOverride(), copyMap(AutoValueEventJS.customAxeToolOverride));
        }

        if (!AutoValueEventJS.customShovelToolOverride.isEmpty()) {
            setConfigObject(AutoValueConfigAccessor.getShovelToolOverride(), copyMap(AutoValueEventJS.customShovelToolOverride));
        }

        if (!AutoValueEventJS.customHoeToolOverride.isEmpty()) {
            setConfigObject(AutoValueConfigAccessor.getHoeToolOverride(), copyMap(AutoValueEventJS.customHoeToolOverride));
        }

        if (!AutoValueEventJS.customSwordToolOverride.isEmpty()) {
            setConfigObject(AutoValueConfigAccessor.getSwordToolOverride(), copyMap(AutoValueEventJS.customSwordToolOverride));
        }

        if (!AutoValueEventJS.customItemPenalties.isEmpty()) {
            setConfigObject(AutoValueConfig.ITEM_PENALTIES, copyMap(AutoValueEventJS.customItemPenalties));
        }

        Map<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> utensilAttributes = AutoValueConfigAccessor.getUtensilAttributes();
        for (Map.Entry<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> entry : utensilAttributes.entrySet()) {
            AutoValueConfig.UtensilTypes utensilType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customUtensilAttributes.getOrDefault(utensilType, Collections.emptyMap()),
                    Collections.emptyList());
        }

        Map<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> wearableAttributes = AutoValueConfigAccessor.getWearableAttributes();
        for (Map.Entry<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> entry : wearableAttributes.entrySet()) {
            AutoValueConfig.WearableTypes wearableType = entry.getKey();
            mergeMapConfig(entry.getValue(),
                    AutoValueEventJS.customWearableAttributes.getOrDefault(wearableType, Collections.emptyMap()),
                    Collections.emptyList());
        }

        if (!AutoValueEventJS.customEntityAttributes.isEmpty()) {
            setConfigObject(AutoValueConfig.ENTITY_ATTRIBUTES, copyMap(AutoValueEventJS.customEntityAttributes));
        }

        if (AutoValueEventJS.customRaritiesModifier != null) {
            AutoValueConfig.RARITIES_MODIFIER.set(AutoValueEventJS.customRaritiesModifier);
        }

        if (AutoValueEventJS.customHardnessModifier != null) {
            AutoValueConfig.HARDNESS_MODIFIER.set(AutoValueEventJS.customHardnessModifier);
        }

        if (AutoValueEventJS.autoValuesEnabled != null) {
            AutoValueConfig.ENABLE_AUTO_VALUES.set(AutoValueEventJS.autoValuesEnabled);
        }
    }

    private static void updateGlobalsConfig() {
        PMMOKubeJSEvents.GLOBALS_CONFIG.post(new GlobalsEventJS());
        mergeMapConfig(GlobalsConfig.PATHS, GlobalsEventJS.customPaths, GlobalsEventJS.removedPaths.keySet());
        mergeMapConfig(GlobalsConfig.CONSTANTS, GlobalsEventJS.customConstants, GlobalsEventJS.removedConstants.keySet());
    }

    private static void updateAntiCheeseConfig() {
        PMMOKubeJSEvents.ANTI_CHEESE_CONFIG.post(new AntiCheeseEventJS());

        if (AntiCheeseEventJS.afkCanSubtract != null) {
            AntiCheeseConfig.AFK_CAN_SUBTRACT.set(AntiCheeseEventJS.afkCanSubtract);
        }

        mergeMapConfig(AntiCheeseConfig.SETTINGS_AFK,
                AntiCheeseEventJS.customAfkSettings,
                keysMarkedForRemoval(AntiCheeseEventJS.removedAfkSettings));

        mergeMapConfig(AntiCheeseConfig.SETTINGS_DIMINISHING,
                AntiCheeseEventJS.customDiminishingSettings,
                keysMarkedForRemoval(AntiCheeseEventJS.removedDiminishingSettings));

        mergeMapConfig(AntiCheeseConfig.SETTINGS_NORMALIZED,
                AntiCheeseEventJS.customNormalizationSettings,
                keysMarkedForRemoval(AntiCheeseEventJS.removedNormalizationSettings));
    }

    private static <K, V> LinkedHashMap<K, V> copyMap(Map<K, V> source) {
        return source == null ? new LinkedHashMap<>() : new LinkedHashMap<>(source);
    }

    private static Map<EventType, List<CompoundTag>> copyPerkSettings(Map<EventType, List<CompoundTag>> source) {
        Map<EventType, List<CompoundTag>> copy = new LinkedHashMap<>();
        source.forEach((eventType, perks) -> {
            List<CompoundTag> perkCopies = new ArrayList<>();
            for (CompoundTag perk : perks) {
                perkCopies.add(perk.copy());
            }
            copy.put(eventType, perkCopies);
        });
        return copy;
    }

    private static <K, V> void mergeMapConfig(TomlConfigHelper.ConfigObject<Map<K, V>> config,
                                              Map<K, V> additions,
                                              Collection<K> removals) {
        Map<K, V> updatedValues = copyMap(config.get());
        if (removals != null) {
            removals.forEach(updatedValues::remove);
        }
        if (additions != null && !additions.isEmpty()) {
            updatedValues.putAll(additions);
        }
        setConfigObject(config, updatedValues);
    }

    private static <K, N, V> void mergeNestedMapConfig(TomlConfigHelper.ConfigObject<Map<K, Map<N, V>>> config,
                                                       Map<K, Map<N, V>> additions) {
        Map<K, Map<N, V>> updatedValues = new LinkedHashMap<>();
        config.get().forEach((key, value) -> updatedValues.put(key, copyMap(value)));
        additions.forEach((key, value) -> updatedValues
                .computeIfAbsent(key, ignored -> new LinkedHashMap<>())
                .putAll(value));
        setConfigObject(config, updatedValues);
    }

    private static <K> List<K> keysMarkedForRemoval(Map<K, Boolean> removalMap) {
        List<K> keys = new ArrayList<>();
        removalMap.forEach((key, remove) -> {
            if (Boolean.TRUE.equals(remove)) {
                keys.add(key);
            }
        });
        return keys;
    }

    @SuppressWarnings("unchecked")
    private static <T> void setConfigObject(TomlConfigHelper.ConfigObject<T> config, T updatedValue) {
        ConfigObjectAccessor<T> accessor = (ConfigObjectAccessor<T>) config;
        Object encodedValue = accessor.pmmojs$getCodec()
                .encodeStart(TomlConfigHelper.TomlConfigOps.INSTANCE, updatedValue)
                .resultOrPartial(message -> ConsoleJS.SERVER.error("Failed to encode PMMO config object: " + message))
                .orElseThrow(() -> new IllegalStateException("Failed to encode PMMO config object"));
        accessor.pmmojs$getValue().set(encodedValue);
        accessor.pmmojs$setCachedObject(encodedValue);
        accessor.pmmojs$setParsedObject(updatedValue);
    }
}
