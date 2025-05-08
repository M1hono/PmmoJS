package com.pickaid.pmmojs.kubejs.handlers.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.kubejs.PMMOKubeJSEvents;
import com.pickaid.pmmojs.kubejs.events.server.*;
import com.pickaid.pmmojs.kubejs.events.server.confg.*;
import com.pickaid.pmmojs.mixin.AutoValueConfigAccessor;
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
        updateGlobalsConfig();
        updateSkillsConfig();
        updatePerksConfig();
        updateServerConfig();
        updateAutoValuesConfig();
        updateAntiCheeseConfig();
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
            Map<String, Double> skillMods = Config.SKILL_MODIFIERS.get();
            skillMods.putAll(ServerEventJS.skillModifiers);
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
            Map<String, Map<String, Long>> dealDamageXp = Config.DEAL_DAMAGE_XP.get();
            for (Map.Entry<String, Map<String, Long>> entry : ServerEventJS.dealDamageXp.entrySet()) {
                dealDamageXp.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
        }

        if (!ServerEventJS.receiveDamageXp.isEmpty()) {
            Map<String, Map<String, Long>> receiveDamageXp = Config.RECEIVE_DAMAGE_XP.get();
            for (Map.Entry<String, Map<String, Long>> entry : ServerEventJS.receiveDamageXp.entrySet()) {
                receiveDamageXp.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
        }

        if (!ServerEventJS.jumpXp.isEmpty()) {
            Map<String, Double> jumpXp = Config.JUMP_XP.get();
            jumpXp.putAll(ServerEventJS.jumpXp);
        }

        if (!ServerEventJS.sprintJumpXp.isEmpty()) {
            Map<String, Double> sprintJumpXp = Config.SPRINT_JUMP_XP.get();
            sprintJumpXp.putAll(ServerEventJS.sprintJumpXp);
        }

        if (!ServerEventJS.crouchJumpXp.isEmpty()) {
            Map<String, Double> crouchJumpXp = Config.CROUCH_JUMP_XP.get();
            crouchJumpXp.putAll(ServerEventJS.crouchJumpXp);
        }

        if (!ServerEventJS.breathChangeXp.isEmpty()) {
            Map<String, Double> breathChangeXp = Config.BREATH_CHANGE_XP.get();
            breathChangeXp.putAll(ServerEventJS.breathChangeXp);
        }

        if (!ServerEventJS.healthChangeXp.isEmpty()) {
            Map<String, Double> healthChangeXp = Config.HEALTH_CHANGE_XP.get();
            healthChangeXp.putAll(ServerEventJS.healthChangeXp);
        }

        if (!ServerEventJS.healthIncreaseXp.isEmpty()) {
            Map<String, Double> healthIncreaseXp = Config.HEALTH_INCREASE_XP.get();
            healthIncreaseXp.putAll(ServerEventJS.healthIncreaseXp);
        }

        if (!ServerEventJS.healthDecreaseXp.isEmpty()) {
            Map<String, Double> healthDecreaseXp = Config.HEALTH_DECREASE_XP.get();
            healthDecreaseXp.putAll(ServerEventJS.healthDecreaseXp);
        }

        if (!ServerEventJS.sprintingXp.isEmpty()) {
            Map<String, Double> sprintingXp = Config.SPRINTING_XP.get();
            sprintingXp.putAll(ServerEventJS.sprintingXp);
        }

        if (!ServerEventJS.submergedXp.isEmpty()) {
            Map<String, Double> submergedXp = Config.SUBMERGED_XP.get();
            submergedXp.putAll(ServerEventJS.submergedXp);
        }

        if (!ServerEventJS.swimmingXp.isEmpty()) {
            Map<String, Double> swimmingXp = Config.SWIMMING_XP.get();
            swimmingXp.putAll(ServerEventJS.swimmingXp);
        }

        if (!ServerEventJS.divingXp.isEmpty()) {
            Map<String, Double> divingXp = Config.DIVING_XP.get();
            divingXp.putAll(ServerEventJS.divingXp);
        }

        if (!ServerEventJS.surfacingXp.isEmpty()) {
            Map<String, Double> surfacingXp = Config.SURFACING_XP.get();
            surfacingXp.putAll(ServerEventJS.surfacingXp);
        }

        if (!ServerEventJS.swimSprintingXp.isEmpty()) {
            Map<String, Double> swimSprintingXp = Config.SWIM_SPRINTING_XP.get();
            swimSprintingXp.putAll(ServerEventJS.swimSprintingXp);
        }

        if (ServerEventJS.partyRange != null) {
            Config.PARTY_RANGE.set(ServerEventJS.partyRange);
        }

        if (!ServerEventJS.partyBonus.isEmpty()) {
            Map<String, Double> partyBonus = Config.PARTY_BONUS.get();
            partyBonus.putAll(ServerEventJS.partyBonus);
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
            Map<ResourceLocation, Map<String, Double>> mobScaling = Config.MOB_SCALING.get();
            for (Map.Entry<ResourceLocation, Map<String, Double>> entry : ServerEventJS.mobScaling.entrySet()) {
                mobScaling.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue());
            }
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

        Map<String, String> paths = GlobalsConfig.PATHS.get();
        for (String key : GlobalsEventJS.removedPaths.keySet()) {
            paths.remove(key);
        }
        if (!GlobalsEventJS.customPaths.isEmpty()) {
            if (paths instanceof HashMap) {
                paths.putAll(GlobalsEventJS.customPaths);
            } else {
                Map<String, String> newValues = new HashMap<>(paths);
                newValues.putAll(GlobalsEventJS.customPaths);
                if (GlobalsConfig.PATHS instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, String> finalMap = (Map<String, String>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        Map<String, String> constants = GlobalsConfig.CONSTANTS.get();
        for (String key : GlobalsEventJS.removedConstants.keySet()) {
            constants.remove(key);
        }

        if (!GlobalsEventJS.customConstants.isEmpty()) {
            if (constants instanceof HashMap) {
                constants.putAll(GlobalsEventJS.customConstants);
            } else {
                Map<String, String> newValues = new HashMap<>(constants);
                newValues.putAll(GlobalsEventJS.customConstants);
                if (GlobalsConfig.CONSTANTS instanceof Map<?,?> map) {
                    map.clear();
                    Map<String, String> finalMap = (Map<String, String>) map;
                    finalMap.putAll(newValues);
                }
            }
        }
    }

    private static void updateAntiCheeseConfig() {
        PMMOKubeJSEvents.ANTI_CHEESE_CONFIG.post(new AntiCheeseEventJS());

        if (AntiCheeseEventJS.afkCanSubtract != null) {
            AntiCheeseConfig.AFK_CAN_SUBTRACT.set(AntiCheeseEventJS.afkCanSubtract);
        }

        Map<EventType, CheeseTracker.Setting> afkSettings = AntiCheeseConfig.SETTINGS_AFK.get();
        for (EventType eventType : AntiCheeseEventJS.removedAfkSettings.keySet()) {
            if (AntiCheeseEventJS.removedAfkSettings.get(eventType)) {
                afkSettings.remove(eventType);
            }
        }

        if (!AntiCheeseEventJS.customAfkSettings.isEmpty()) {
            if (afkSettings instanceof HashMap) {
                afkSettings.putAll(AntiCheeseEventJS.customAfkSettings);
            } else {
                Map<EventType, CheeseTracker.Setting> newValues = new HashMap<>(afkSettings);
                newValues.putAll(AntiCheeseEventJS.customAfkSettings);
                if (AntiCheeseConfig.SETTINGS_AFK instanceof Map<?,?> map) {
                    map.clear();
                    Map<EventType, CheeseTracker.Setting> finalMap = (Map<EventType, CheeseTracker.Setting>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        Map<EventType, CheeseTracker.Setting> diminishingSettings = AntiCheeseConfig.SETTINGS_DIMINISHING.get();
        for (EventType eventType : AntiCheeseEventJS.removedDiminishingSettings.keySet()) {
            if (AntiCheeseEventJS.removedDiminishingSettings.get(eventType)) {
                diminishingSettings.remove(eventType);
            }
        }

        if (!AntiCheeseEventJS.customDiminishingSettings.isEmpty()) {
            if (diminishingSettings instanceof HashMap) {
                diminishingSettings.putAll(AntiCheeseEventJS.customDiminishingSettings);
            } else {
                Map<EventType, CheeseTracker.Setting> newValues = new HashMap<>(diminishingSettings);
                newValues.putAll(AntiCheeseEventJS.customDiminishingSettings);
                if (AntiCheeseConfig.SETTINGS_DIMINISHING instanceof Map<?,?> map) {
                    map.clear();
                    Map<EventType, CheeseTracker.Setting> finalMap = (Map<EventType, CheeseTracker.Setting>) map;
                    finalMap.putAll(newValues);
                }
            }
        }

        Map<EventType, CheeseTracker.Setting> normalizationSettings = AntiCheeseConfig.SETTINGS_NORMALIZED.get();
        for (EventType eventType : AntiCheeseEventJS.removedNormalizationSettings.keySet()) {
            if (AntiCheeseEventJS.removedNormalizationSettings.get(eventType)) {
                normalizationSettings.remove(eventType);
            }
        }

        if (!AntiCheeseEventJS.customNormalizationSettings.isEmpty()) {
            if (normalizationSettings instanceof HashMap) {
                normalizationSettings.putAll(AntiCheeseEventJS.customNormalizationSettings);
            } else {
                Map<EventType, CheeseTracker.Setting> newValues = new HashMap<>(normalizationSettings);
                newValues.putAll(AntiCheeseEventJS.customNormalizationSettings);
                if (AntiCheeseConfig.SETTINGS_NORMALIZED instanceof Map<?,?> map) {
                    map.clear();
                    Map<EventType, CheeseTracker.Setting> finalMap = (Map<EventType, CheeseTracker.Setting>) map;
                    finalMap.putAll(newValues);
                }
            }
        }
    }
}