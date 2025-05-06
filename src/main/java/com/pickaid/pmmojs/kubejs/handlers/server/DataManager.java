package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.*;
import com.pickaid.pmmojs.mixin.AutoValueConfigAccessor;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.api.events.PMMORegistrationEvent;
import harmonised.pmmo.config.Config;
import harmonised.pmmo.config.PerksConfig;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.SkillData;
import harmonised.pmmo.config.readers.TomlConfigHelper;
import harmonised.pmmo.features.autovalues.AutoValueConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = PmmoJS.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataManager {
    public static boolean loaded = false;
    private static boolean configsUpdated = false;

    @SubscribeEvent
    public static void onSettingsSyncEvent(PMMORegistrationEvent event) {
        PMMOKubeJSEvents.SETTINGS.post(new PMMOSettingEventJS());
    }

    @SubscribeEvent
    public static void onServerLoaded(ServerStartedEvent event) {
        if (!loaded) {
            loaded = true;
        }
    }

    @SubscribeEvent
    public static void updateConfigs(TagsUpdatedEvent event) {
        if (!configsUpdated && loaded) {
            updateConfigs();
            configsUpdated = true;
        } else if (configsUpdated && loaded) {
            configsUpdated = false;
        }
    }

    public static void updateConfigs() {
        if (!loaded) return;
        updateSkillsConfig();
        updatePerksConfig();
        updateServerConfig();
        updateAutoValuesConfig();
    }

    private static void updateSkillsConfig() {
        Object skillConfig = SkillsConfig.SKILLS.get();
        if (skillConfig instanceof Map<?,?>) {
            Map<String, SkillData> skillsMap = (Map<String, SkillData>) skillConfig;
            PMMOKubeJSEvents.SKILL_CONFIG.post(new SkillsEventJS());
            skillsMap.putAll(SkillsEventJS.customSkills);
            for (String skillId : SkillsEventJS.removedSkills) {
                skillsMap.remove(skillId);
            }
        }
    }

    private static void updatePerksConfig() {
        Map<EventType, List<CompoundTag>> perksConfig = PerksConfig.PERK_SETTINGS.get();
        if (perksConfig instanceof Map) {
            PMMOKubeJSEvents.PERK_CONFIG.post(new PerksEventJS());
            Map<EventType, List<CompoundTag>> perksMap = perksConfig;

            for (EventType eventType : PerksEventJS.clearedPerks) {
                if (perksMap.containsKey(eventType)) {
                    perksMap.get(eventType).clear();
                }
            }

            for (String perkType : PerksEventJS.removedPerkTypes) {
                for (EventType eventType : EventType.values()) {
                    if (perksMap.containsKey(eventType)) {
                        List<CompoundTag> perksList = perksMap.get(eventType);
                        Iterator<CompoundTag> iterator = perksList.iterator();
                        while (iterator.hasNext()) {
                            CompoundTag tag = iterator.next();
                            if (tag.getString("perk").equals(perkType)) {
                                iterator.remove();
                                ConsoleJS.SERVER.info("Removing perk type: " + perkType +
                                        " for skill: " + tag.getString("skill") +
                                        " from event: " + eventType.name());
                            }
                        }
                    }
                }
            }

            for (EventType eventType : EventType.values()) {
                if (perksMap.containsKey(eventType)) {
                    List<CompoundTag> currentPerks = perksMap.get(eventType);
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
            }

            for (Map.Entry<EventType, List<CompoundTag>> entry : PerksEventJS.customPerks.entrySet()) {
                EventType eventType = entry.getKey();
                List<CompoundTag> perksToAdd = entry.getValue();
                if (!perksToAdd.isEmpty()) {
                    if (!perksMap.containsKey(eventType)) {
                        perksMap.put(eventType, new ArrayList<>());
                    }
                    List<CompoundTag> existingPerks = perksMap.get(eventType);

                    for (CompoundTag newPerk : perksToAdd) {
                        existingPerks.add(newPerk.copy());
                        ConsoleJS.SERVER.info("Adding perk: " + newPerk.getString("perk") +
                                " for skill: " + newPerk.getString("skill") +
                                " to event: " + eventType.name());
                    }
                }
            }
        }
    }

    private static boolean arePerksEqual(CompoundTag tag1, CompoundTag tag2) {
        return tag1.getString("perk").equals(tag2.getString("perk")) &&
                tag1.getString("skill").equals(tag2.getString("skill"));
    }

    private static void updateServerConfig() {
        PMMOKubeJSEvents.SERVER_CONFIG.post(new ServerConfigEventJS());
        // General settings
        if (ServerConfigEventJS.creativeReach != null) {
            Config.CREATIVE_REACH.set(ServerConfigEventJS.creativeReach);
        }

        if (ServerConfigEventJS.salvageBlock != null) {
            Config.SALVAGE_BLOCK.set(ServerConfigEventJS.salvageBlock);
        }

        if (ServerConfigEventJS.treasureEnabled != null) {
            Config.TREASURE_ENABLED.set(ServerConfigEventJS.treasureEnabled);
        }

        if (ServerConfigEventJS.brewingTracked != null) {
            Config.BREWING_TRACKED.set(ServerConfigEventJS.brewingTracked);
        }

        // Level settings
        if (ServerConfigEventJS.maxLevel != null) {
            Config.MAX_LEVEL.set(ServerConfigEventJS.maxLevel);
        }

        if (ServerConfigEventJS.lossOnDeath != null) {
            Config.LOSS_ON_DEATH.set(ServerConfigEventJS.lossOnDeath);
        }

        if (ServerConfigEventJS.loseLevelsOnDeath != null) {
            Config.LOSE_LEVELS_ON_DEATH.set(ServerConfigEventJS.loseLevelsOnDeath);
        }

        if (ServerConfigEventJS.loseOnlyExcess != null) {
            Config.LOSE_ONLY_EXCESS.set(ServerConfigEventJS.loseOnlyExcess);
        }

        if (ServerConfigEventJS.useExponentialFormula != null) {
            Config.USE_EXPONENTIAL_FORMULA.set(ServerConfigEventJS.useExponentialFormula);
        }

        if (ServerConfigEventJS.globalModifier != null) {
            Config.GLOBAL_MODIFIER.set(ServerConfigEventJS.globalModifier);
        }

        if (!ServerConfigEventJS.skillModifiers.isEmpty()) {
            Map<String, Double> skillMods = Config.SKILL_MODIFIERS.get();
            skillMods.putAll(ServerConfigEventJS.skillModifiers);
        }

        // Linear level settings
        if (ServerConfigEventJS.linearBaseXp != null) {
            Config.LINEAR_BASE_XP.set(ServerConfigEventJS.linearBaseXp);
        }

        if (ServerConfigEventJS.linearPerLevel != null) {
            Config.LINEAR_PER_LEVEL.set(ServerConfigEventJS.linearPerLevel);
        }

        // Exponential level settings
        if (ServerConfigEventJS.exponentialBaseXp != null) {
            Config.EXPONENTIAL_BASE_XP.set(ServerConfigEventJS.exponentialBaseXp);
        }

        if (ServerConfigEventJS.exponentialPowerBase != null) {
            Config.EXPONENTIAL_POWER_BASE.set(ServerConfigEventJS.exponentialPowerBase);
        }

        if (ServerConfigEventJS.exponentialLevelMod != null) {
            Config.EXPONENTIAL_LEVEL_MOD.set(ServerConfigEventJS.exponentialLevelMod);
        }

        // Requirement settings
        for (Map.Entry<ReqType, Boolean> entry : ServerConfigEventJS.reqEnabled.entrySet()) {
            Config.reqEnabled(entry.getKey()).set(entry.getValue());
        }

        // XP Gain settings
        if (ServerConfigEventJS.reusePenalty != null) {
            Config.REUSE_PENALTY.set(ServerConfigEventJS.reusePenalty);
        }

        if (ServerConfigEventJS.summatedMaps != null) {
            Config.SUMMATED_MAPS.set(ServerConfigEventJS.summatedMaps);
        }

        // Damage XP settings
        if (!ServerConfigEventJS.dealDamageXp.isEmpty()) {
            Map<String, Map<String, Long>> dealDamageXp = Config.DEAL_DAMAGE_XP.get();
            for (Map.Entry<String, Map<String, Long>> entry : ServerConfigEventJS.dealDamageXp.entrySet()) {
                dealDamageXp.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
        }

        if (!ServerConfigEventJS.receiveDamageXp.isEmpty()) {
            Map<String, Map<String, Long>> receiveDamageXp = Config.RECEIVE_DAMAGE_XP.get();
            for (Map.Entry<String, Map<String, Long>> entry : ServerConfigEventJS.receiveDamageXp.entrySet()) {
                receiveDamageXp.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
        }

        // Movement XP settings
        if (!ServerConfigEventJS.jumpXp.isEmpty()) {
            Map<String, Double> jumpXp = Config.JUMP_XP.get();
            jumpXp.putAll(ServerConfigEventJS.jumpXp);
        }

        if (!ServerConfigEventJS.sprintJumpXp.isEmpty()) {
            Map<String, Double> sprintJumpXp = Config.SPRINT_JUMP_XP.get();
            sprintJumpXp.putAll(ServerConfigEventJS.sprintJumpXp);
        }

        if (!ServerConfigEventJS.crouchJumpXp.isEmpty()) {
            Map<String, Double> crouchJumpXp = Config.CROUCH_JUMP_XP.get();
            crouchJumpXp.putAll(ServerConfigEventJS.crouchJumpXp);
        }

        // Player action XP settings
        if (!ServerConfigEventJS.breathChangeXp.isEmpty()) {
            Map<String, Double> breathChangeXp = Config.BREATH_CHANGE_XP.get();
            breathChangeXp.putAll(ServerConfigEventJS.breathChangeXp);
        }

        if (!ServerConfigEventJS.healthChangeXp.isEmpty()) {
            Map<String, Double> healthChangeXp = Config.HEALTH_CHANGE_XP.get();
            healthChangeXp.putAll(ServerConfigEventJS.healthChangeXp);
        }

        if (!ServerConfigEventJS.healthIncreaseXp.isEmpty()) {
            Map<String, Double> healthIncreaseXp = Config.HEALTH_INCREASE_XP.get();
            healthIncreaseXp.putAll(ServerConfigEventJS.healthIncreaseXp);
        }

        if (!ServerConfigEventJS.healthDecreaseXp.isEmpty()) {
            Map<String, Double> healthDecreaseXp = Config.HEALTH_DECREASE_XP.get();
            healthDecreaseXp.putAll(ServerConfigEventJS.healthDecreaseXp);
        }

        if (!ServerConfigEventJS.sprintingXp.isEmpty()) {
            Map<String, Double> sprintingXp = Config.SPRINTING_XP.get();
            sprintingXp.putAll(ServerConfigEventJS.sprintingXp);
        }

        if (!ServerConfigEventJS.submergedXp.isEmpty()) {
            Map<String, Double> submergedXp = Config.SUBMERGED_XP.get();
            submergedXp.putAll(ServerConfigEventJS.submergedXp);
        }

        if (!ServerConfigEventJS.swimmingXp.isEmpty()) {
            Map<String, Double> swimmingXp = Config.SWIMMING_XP.get();
            swimmingXp.putAll(ServerConfigEventJS.swimmingXp);
        }

        if (!ServerConfigEventJS.divingXp.isEmpty()) {
            Map<String, Double> divingXp = Config.DIVING_XP.get();
            divingXp.putAll(ServerConfigEventJS.divingXp);
        }

        if (!ServerConfigEventJS.surfacingXp.isEmpty()) {
            Map<String, Double> surfacingXp = Config.SURFACING_XP.get();
            surfacingXp.putAll(ServerConfigEventJS.surfacingXp);
        }

        if (!ServerConfigEventJS.swimSprintingXp.isEmpty()) {
            Map<String, Double> swimSprintingXp = Config.SWIM_SPRINTING_XP.get();
            swimSprintingXp.putAll(ServerConfigEventJS.swimSprintingXp);
        }

        // Party settings
        if (ServerConfigEventJS.partyRange != null) {
            Config.PARTY_RANGE.set(ServerConfigEventJS.partyRange);
        }

        if (!ServerConfigEventJS.partyBonus.isEmpty()) {
            Map<String, Double> partyBonus = Config.PARTY_BONUS.get();
            partyBonus.putAll(ServerConfigEventJS.partyBonus);
        }

        // Mob scaling settings
        if (ServerConfigEventJS.mobScalingEnabled != null) {
            Config.MOB_SCALING_ENABLED.set(ServerConfigEventJS.mobScalingEnabled);
        }

        if (ServerConfigEventJS.mobUseExponentialFormula != null) {
            Config.MOB_USE_EXPONENTIAL_FORMULA.set(ServerConfigEventJS.mobUseExponentialFormula);
        }

        if (ServerConfigEventJS.mobScalingAoe != null) {
            Config.MOB_SCALING_AOE.set(ServerConfigEventJS.mobScalingAoe);
        }

        if (ServerConfigEventJS.mobScalingBaseLevel != null) {
            Config.MOB_SCALING_BASE_LEVEL.set(ServerConfigEventJS.mobScalingBaseLevel);
        }

        if (ServerConfigEventJS.mobLinearPerLevel != null) {
            Config.MOB_LINEAR_PER_LEVEL.set(ServerConfigEventJS.mobLinearPerLevel);
        }

        if (ServerConfigEventJS.mobExponentialPowerBase != null) {
            Config.MOB_EXPONENTIAL_POWER_BASE.set(ServerConfigEventJS.mobExponentialPowerBase);
        }

        if (ServerConfigEventJS.mobExponentialLevelMod != null) {
            Config.MOB_EXPONENTIAL_LEVEL_MOD.set(ServerConfigEventJS.mobExponentialLevelMod);
        }

        if (ServerConfigEventJS.bossScalingRatio != null) {
            Config.BOSS_SCALING_RATIO.set(ServerConfigEventJS.bossScalingRatio);
        }

        if (!ServerConfigEventJS.mobScaling.isEmpty()) {
            Map<ResourceLocation, Map<String, Double>> mobScaling = Config.MOB_SCALING.get();
            for (Map.Entry<ResourceLocation, Map<String, Double>> entry : ServerConfigEventJS.mobScaling.entrySet()) {
                mobScaling.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
        }

        // Vein miner settings
        if (ServerConfigEventJS.veinEnabled != null) {
            Config.VEIN_ENABLED.set(ServerConfigEventJS.veinEnabled);
        }

        if (ServerConfigEventJS.requireSetting != null) {
            Config.REQUIRE_SETTING.set(ServerConfigEventJS.requireSetting);
        }

        if (ServerConfigEventJS.defaultConsume != null) {
            Config.DEFAULT_CONSUME.set(ServerConfigEventJS.defaultConsume);
        }

        if (ServerConfigEventJS.veinChargeModifier != null) {
            Config.VEIN_CHARGE_MODIFIER.set(ServerConfigEventJS.veinChargeModifier);
        }

        if (!ServerConfigEventJS.veinBlacklist.isEmpty()) {
            List<String> veinBlacklist = new ArrayList<>(Config.VEIN_BLACKLIST.get());
            veinBlacklist.addAll(ServerConfigEventJS.veinBlacklist);
            Config.VEIN_BLACKLIST.set(veinBlacklist);
        }

        if (ServerConfigEventJS.baseChargeRate != null) {
            Config.BASE_CHARGE_RATE.set(ServerConfigEventJS.baseChargeRate);
        }

        if (ServerConfigEventJS.baseChargeCap != null) {
            Config.BASE_CHARGE_CAP.set(ServerConfigEventJS.baseChargeCap);
        }
    }
    private static void updateAutoValuesConfig() {
        PMMOKubeJSEvents.AUTO_VALUE_CONFIG.post(new AutoValueEventJS());

        // Update Item XP Awards
        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> itemXpAwards = AutoValueConfigAccessor.getItemXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : itemXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            Map<String, Long> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customItemXpAwards.containsKey(eventType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customItemXpAwards.get(eventType));
                } else {
                    Map<String, Long> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customItemXpAwards.get(eventType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Long> finalMap = (Map<String, Long>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }

            for (String skill : AutoValueEventJS.removedItemXpSkills.getOrDefault(eventType, Collections.emptyList())) {
                defaultValues.remove(skill);
            }
        }

        // Update Block XP Awards
        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> blockXpAwards = AutoValueConfigAccessor.getBlockXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : blockXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            Map<String, Long> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customBlockXpAwards.containsKey(eventType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customBlockXpAwards.get(eventType));
                } else {
                    Map<String, Long> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customBlockXpAwards.get(eventType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Long> finalMap = (Map<String, Long>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }

            for (String skill : AutoValueEventJS.removedBlockXpSkills.getOrDefault(eventType, Collections.emptyList())) {
                defaultValues.remove(skill);
            }
        }

        // Update Entity XP Awards
        Map<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entityXpAwards = AutoValueConfigAccessor.getEntityXpAwards();
        for (Map.Entry<EventType, TomlConfigHelper.ConfigObject<Map<String, Long>>> entry : entityXpAwards.entrySet()) {
            EventType eventType = entry.getKey();
            Map<String, Long> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customEntityXpAwards.containsKey(eventType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customEntityXpAwards.get(eventType));
                } else {
                    Map<String, Long> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customEntityXpAwards.get(eventType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Long> finalMap = (Map<String, Long>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }

            for (String skill : AutoValueEventJS.removedEntityXpSkills.getOrDefault(eventType, Collections.emptyList())) {
                defaultValues.remove(skill);
            }
        }

        // Special Overrides (these are already public)
        if (AutoValueEventJS.customAxeOverride != null) {
            Map<String, Long> axeValues = AutoValueConfig.AXE_OVERRIDE.get();
            if (axeValues instanceof HashMap) {
                axeValues.clear();
                axeValues.putAll(AutoValueEventJS.customAxeOverride);
            } else {
                Map<String, Long> newValues = new HashMap<>(AutoValueEventJS.customAxeOverride);
                if (AutoValueConfig.AXE_OVERRIDE instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Long> finalMap = (Map<String, Long>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customHoeOverride != null) {
            Map<String, Long> hoeValues = AutoValueConfig.HOE_OVERRIDE.get();
            if (hoeValues instanceof HashMap) {
                hoeValues.clear();
                hoeValues.putAll(AutoValueEventJS.customHoeOverride);
            } else {
                Map<String, Long> newValues = new HashMap<>(AutoValueEventJS.customHoeOverride);
                if (AutoValueConfig.HOE_OVERRIDE instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Long> finalMap = (Map<String, Long>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customShovelOverride != null) {
            Map<String, Long> shovelValues = AutoValueConfig.SHOVEL_OVERRIDE.get();
            if (shovelValues instanceof HashMap) {
                shovelValues.clear();
                shovelValues.putAll(AutoValueEventJS.customShovelOverride);
            } else {
                Map<String, Long> newValues = new HashMap<>(AutoValueEventJS.customShovelOverride);
                if (AutoValueConfig.SHOVEL_OVERRIDE instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Long> finalMap = (Map<String, Long>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customBrewablesOverride != null) {
            Map<String, Long> brewValues = AutoValueConfig.BREWABLES_OVERRIDE.get();
            if (brewValues instanceof HashMap) {
                brewValues.clear();
                brewValues.putAll(AutoValueEventJS.customBrewablesOverride);
            } else {
                Map<String, Long> newValues = new HashMap<>(AutoValueEventJS.customBrewablesOverride);
                if (AutoValueConfig.BREWABLES_OVERRIDE instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Long> finalMap = (Map<String, Long>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customSmeltablesOverride != null) {
            Map<String, Long> smeltValues = AutoValueConfig.SMELTABLES_OVERRIDE.get();
            if (smeltValues instanceof HashMap) {
                smeltValues.clear();
                smeltValues.putAll(AutoValueEventJS.customSmeltablesOverride);
            } else {
                Map<String, Long> newValues = new HashMap<>(AutoValueEventJS.customSmeltablesOverride);
                if (AutoValueConfig.SMELTABLES_OVERRIDE instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Long> finalMap = (Map<String, Long>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        // Update Item Requirements
        Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> itemReqs = AutoValueConfigAccessor.getItemReqs();
        for (Map.Entry<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> entry : itemReqs.entrySet()) {
            ReqType reqType = entry.getKey();
            Map<String, Integer> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customItemReqs.containsKey(reqType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customItemReqs.get(reqType));
                } else {
                    Map<String, Integer> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customItemReqs.get(reqType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Integer> finalMap = (Map<String, Integer>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }

            for (String skill : AutoValueEventJS.removedItemReqSkills.getOrDefault(reqType, Collections.emptyList())) {
                defaultValues.remove(skill);
            }
        }

        // Update Block Requirements
        Map<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> blockReqs = AutoValueConfigAccessor.getBlockReqs();
        for (Map.Entry<ReqType, TomlConfigHelper.ConfigObject<Map<String, Integer>>> entry : blockReqs.entrySet()) {
            ReqType reqType = entry.getKey();
            Map<String, Integer> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customBlockReqs.containsKey(reqType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customBlockReqs.get(reqType));
                } else {
                    Map<String, Integer> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customBlockReqs.get(reqType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Integer> finalMap = (Map<String, Integer>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }

            for (String skill : AutoValueEventJS.removedBlockReqSkills.getOrDefault(reqType, Collections.emptyList())) {
                defaultValues.remove(skill);
            }
        }

        // Update Tool Overrides
        if (AutoValueEventJS.customAxeToolOverride != null) {
            Map<String, Integer> axeValues = AutoValueConfigAccessor.getAxeToolOverride().get();
            if (axeValues instanceof HashMap) {
                axeValues.clear();
                axeValues.putAll(AutoValueEventJS.customAxeToolOverride);
            } else {
                Map<String, Integer> newValues = new HashMap<>(AutoValueEventJS.customAxeToolOverride);
                if (AutoValueConfigAccessor.getAxeToolOverride() instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Integer> finalMap = (Map<String, Integer>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customShovelToolOverride != null) {
            Map<String, Integer> shovelValues = AutoValueConfigAccessor.getShovelToolOverride().get();
            if (shovelValues instanceof HashMap) {
                shovelValues.clear();
                shovelValues.putAll(AutoValueEventJS.customShovelToolOverride);
            } else {
                Map<String, Integer> newValues = new HashMap<>(AutoValueEventJS.customShovelToolOverride);
                if (AutoValueConfigAccessor.getShovelToolOverride() instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Integer> finalMap = (Map<String, Integer>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customHoeToolOverride != null) {
            Map<String, Integer> hoeValues = AutoValueConfigAccessor.getHoeToolOverride().get();
            if (hoeValues instanceof HashMap) {
                hoeValues.clear();
                hoeValues.putAll(AutoValueEventJS.customHoeToolOverride);
            } else {
                Map<String, Integer> newValues = new HashMap<>(AutoValueEventJS.customHoeToolOverride);
                if (AutoValueConfigAccessor.getHoeToolOverride() instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Integer> finalMap = (Map<String, Integer>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        if (AutoValueEventJS.customSwordToolOverride != null) {
            Map<String, Integer> swordValues = AutoValueConfigAccessor.getSwordToolOverride().get();
            if (swordValues instanceof HashMap) {
                swordValues.clear();
                swordValues.putAll(AutoValueEventJS.customSwordToolOverride);
            } else {
                Map<String, Integer> newValues = new HashMap<>(AutoValueEventJS.customSwordToolOverride);
                if (AutoValueConfigAccessor.getSwordToolOverride() instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Integer> finalMap = (Map<String, Integer>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        // Update Item Penalties (already public)
        if (AutoValueEventJS.customItemPenalties != null) {
            Map<ResourceLocation, Integer> penalties = AutoValueConfig.ITEM_PENALTIES.get();
            if (penalties instanceof HashMap) {
                penalties.clear();
                penalties.putAll(AutoValueEventJS.customItemPenalties);
            } else {
                Map<ResourceLocation, Integer> newValues = new HashMap<>(AutoValueEventJS.customItemPenalties);
                if (AutoValueConfig.ITEM_PENALTIES instanceof Map<?,?> map) {
                    map.clear();
                    Map<ResourceLocation, Integer> finalMap = (Map<ResourceLocation, Integer>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        // Update Attribute Configuration
        Map<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> utensilAttributes = AutoValueConfigAccessor.getUtensilAttributes();
        for (Map.Entry<AutoValueConfig.UtensilTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> entry : utensilAttributes.entrySet()) {
            AutoValueConfig.UtensilTypes utensilType = entry.getKey();
            Map<String, Double> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customUtensilAttributes.containsKey(utensilType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customUtensilAttributes.get(utensilType));
                } else {
                    Map<String, Double> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customUtensilAttributes.get(utensilType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Double> finalMap = (Map<String, Double>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }
        }

        Map<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> wearableAttributes = AutoValueConfigAccessor.getWearableAttributes();
        for (Map.Entry<AutoValueConfig.WearableTypes, TomlConfigHelper.ConfigObject<Map<String, Double>>> entry : wearableAttributes.entrySet()) {
            AutoValueConfig.WearableTypes wearableType = entry.getKey();
            Map<String, Double> defaultValues = entry.getValue().get();

            if (AutoValueEventJS.customWearableAttributes.containsKey(wearableType)) {
                if (defaultValues instanceof HashMap) {
                    defaultValues.putAll(AutoValueEventJS.customWearableAttributes.get(wearableType));
                } else {
                    Map<String, Double> newValues = new HashMap<>(defaultValues);
                    newValues.putAll(AutoValueEventJS.customWearableAttributes.get(wearableType));
                    if (entry.getValue() instanceof Map<?,?> map) {
                        map.clear();
                        Map<String, Double> finalMap = (Map<String, Double>) map;
                        finalMap.putAll(newValues);
                    }
                }
            }
        }

        // Update Entity Attributes (already public)
        if (AutoValueEventJS.customEntityAttributes != null) {
            Map<String, Double> entityAttrs = AutoValueConfig.ENTITY_ATTRIBUTES.get();
            if (entityAttrs instanceof HashMap) {
                entityAttrs.clear();
                entityAttrs.putAll(AutoValueEventJS.customEntityAttributes);
            } else {
                Map<String, Double> newValues = new HashMap<>(AutoValueEventJS.customEntityAttributes);
                if (AutoValueConfig.ENTITY_ATTRIBUTES instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, Double> finalMap = (Map<String, Double>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        // Update global modifiers (already public)
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
}