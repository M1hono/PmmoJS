package com.pickaid.pmmojs.utils;

import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.MobModifier;
import harmonised.pmmo.config.codecs.SkillData;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.features.party.PartyUtils;
import harmonised.pmmo.features.veinmining.VeinMiningLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class PmmoHelper {
    private PmmoHelper() {
    }

    @Info("Create a valid resource location from a string id.")
    public static ResourceLocation id(String value) {
        String normalized = value.contains(":") ? value : "minecraft:" + value;
        ResourceLocation parsed = ResourceLocation.tryParse(normalized);
        if (parsed == null) {
            throw new IllegalArgumentException("Invalid resource location: " + value);
        }

        return parsed;
    }

    @HideFromJS
    public static EventType coerceEventType(Object value) {
        if (value instanceof EventType eventType) {
            return eventType;
        }

        if (value == null) {
            return null;
        }

        String typeId = String.valueOf(value).trim();
        if (typeId.isEmpty()) {
            return null;
        }

        EventType byName = EventType.byName(typeId.toLowerCase());
        if (byName != null) {
            return byName;
        }

        try {
            return EventType.valueOf(typeId.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @HideFromJS
    public static PMMOInternalType coerceInternalType(Object value) {
        if (value instanceof PMMOInternalType internalType) {
            return internalType;
        }

        if (value == null) {
            return null;
        }

        return PMMOInternalType.byId(String.valueOf(value));
    }

    @Info("Get all PMMO trigger ids that PmmoJS bridges into server scripts.")
    public static List<String> getTriggerTypeIds() {
        return Arrays.stream(EventType.values()).map(EventType::getName).toList();
    }

    @Info("Get all PMMO event type enum ids.")
    public static List<String> getEventTypeIds() {
        return Arrays.stream(EventType.values()).map(EventType::name).toList();
    }

    @Info("Get all PMMO internal hook ids that PmmoJS bridges into server scripts.")
    public static List<String> getInternalTypeIds() {
        return Arrays.stream(PMMOInternalType.values()).map(PMMOInternalType::getId).toList();
    }

    @Info("Get all PMMO requirement type enum ids.")
    public static List<String> getReqTypeIds() {
        return Arrays.stream(ReqType.values()).map(ReqType::name).toList();
    }

    @Info("Get PMMO requirement type ids that can apply to items.")
    public static List<String> getItemReqTypeIds() {
        return Arrays.stream(ReqType.values()).filter(reqType -> reqType.itemApplicable).map(ReqType::name).toList();
    }

    @Info("Get PMMO requirement type ids that can apply to blocks.")
    public static List<String> getBlockReqTypeIds() {
        return Arrays.stream(ReqType.values()).filter(reqType -> reqType.blockApplicable).map(ReqType::name).toList();
    }

    @Info("Get PMMO requirement type ids that can apply to entities.")
    public static List<String> getEntityReqTypeIds() {
        return Arrays.stream(ReqType.values()).filter(reqType -> reqType.entityApplicable).map(ReqType::name).toList();
    }

    @Info("Get all PMMO object type enum ids.")
    public static List<String> getObjectTypeIds() {
        return Arrays.stream(ObjectType.values()).map(ObjectType::name).toList();
    }

    @Info("Get a player's PMMO level for a skill.")
    public static int getSkillLevel(Player player, String skill) {
        return APIUtils.getLevel(skill, player);
    }

    @Info("Set a player's PMMO level for a skill.")
    public static void setSkillLevel(Player player, String skill, Number level) {
        APIUtils.setLevel(skill, player, level.intValue());
    }

    @Info("Add levels to a player's PMMO skill.")
    public static boolean addSkillLevel(Player player, String skill, Number amount) {
        return APIUtils.addLevel(skill, player, amount.intValue());
    }

    @Info("Get a player's PMMO XP for a skill.")
    public static long getSkillXp(Player player, String skill) {
        return APIUtils.getXp(skill, player);
    }

    @Info("Set a player's PMMO XP for a skill.")
    public static void setSkillXp(Player player, String skill, Number xp) {
        APIUtils.setXp(skill, player, xp.longValue());
    }

    @Info("Add PMMO XP to a player's skill.")
    public static boolean addSkillXp(Player player, String skill, Number amount) {
        return APIUtils.addXp(skill, player, amount.longValue());
    }

    @Info("Get all PMMO levels for a player.")
    public static Map<String, Integer> getAllSkillLevels(Player player) {
        return new LinkedHashMap<>(APIUtils.getAllLevels(player));
    }

    @Info("Get the raw PMMO XP map for a player.")
    public static Map<String, Long> getRawXpMap(Player player) {
        return new LinkedHashMap<>(APIUtils.getRawXpMap(player));
    }

    @Info("Get all configured PMMO skill definitions.")
    public static Map<String, SkillData> getSkills() {
        Map<String, SkillData> map = new LinkedHashMap<>();
        try {
            map.putAll(SkillsConfig.SKILLS.get());
        } catch (Exception ignored) {
        }

        return map;
    }

    @Info("Get a configured PMMO skill definition by name.")
    public static SkillData getSkillData(String skill) {
        return SkillsConfig.SKILLS.get().getOrDefault(skill, SkillData.Builder.getDefault());
    }

    @Info("Get the PMMO XP awards for an item action.")
    public static Map<String, Long> getItemXpAwards(ItemStack stack, EventType type, Player player) {
        return new LinkedHashMap<>(APIUtils.getXpAwardMap(stack, type, LogicalSide.SERVER, player));
    }

    @Info("Get the PMMO XP awards for a block position.")
    public static Map<String, Long> getBlockXpAwards(Level level, BlockPos pos, EventType type, Player player) {
        return new LinkedHashMap<>(APIUtils.getXpAwardMap(level, pos, type, player));
    }

    @Info("Get the PMMO XP awards for an entity action.")
    public static Map<String, Long> getEntityXpAwards(Entity entity, EventType type, Player player) {
        return new LinkedHashMap<>(APIUtils.getXpAwardMap(entity, type, LogicalSide.SERVER, player));
    }

    @Info("Get the PMMO XP awards for an object id and object type.")
    public static Map<String, Long> getObjectXpAwards(ObjectType objectType, String objectId, EventType type, Player player) {
        return new LinkedHashMap<>(APIUtils.getXpAwardMap(objectType, type, id(objectId), LogicalSide.SERVER, player));
    }

    @Info("Get the PMMO requirements for an item action.")
    public static Map<String, Integer> getItemRequirements(ItemStack stack, ReqType reqType) {
        return new LinkedHashMap<>(APIUtils.getRequirementMap(stack, reqType, LogicalSide.SERVER));
    }

    @Info("Get the PMMO requirements for a block position.")
    public static Map<String, Integer> getBlockRequirements(Level level, BlockPos pos, ReqType reqType) {
        return new LinkedHashMap<>(APIUtils.getRequirementMap(pos, level, reqType));
    }

    @Info("Get the PMMO requirements for an entity action.")
    public static Map<String, Integer> getEntityRequirements(Entity entity, ReqType reqType) {
        return new LinkedHashMap<>(APIUtils.getRequirementMap(entity, reqType, LogicalSide.SERVER));
    }

    @Info("Get the PMMO requirements for an object id and object type.")
    public static Map<String, Integer> getObjectRequirements(ObjectType objectType, String objectId, ReqType reqType) {
        return new LinkedHashMap<>(APIUtils.getRequirementMap(objectType, id(objectId), reqType, LogicalSide.SERVER));
    }

    @Info("Get enchanting requirements for an item stack.")
    public static Map<String, Integer> getEnchantRequirements(ItemStack stack) {
        return new LinkedHashMap<>(Core.get(LogicalSide.SERVER).getEnchantReqs(stack));
    }

    @Info("Get enchanting requirements for an enchantment id and level.")
    public static Map<String, Integer> getEnchantmentRequirements(String enchantmentId, Number level) {
        return new LinkedHashMap<>(Core.get(LogicalSide.SERVER).getEnchantmentReqs(id(enchantmentId), level.intValue()));
    }

    @Info("Get the merged PMMO modifier map that applies to a player.")
    public static Map<String, Double> getConsolidatedModifiers(Player player) {
        return new LinkedHashMap<>(Core.get(player.level()).getConsolidatedModifierMap(player));
    }

    @Info("Get object-level PMMO modifiers for an object id, type, and modifier kind.")
    public static Map<String, Double> getObjectModifiers(ObjectType objectType, String objectId, ModifierDataType dataType) {
        return new LinkedHashMap<>(Core.get(LogicalSide.SERVER).getObjectModifierMap(objectType, id(objectId), dataType, new CompoundTag()));
    }

    @Info("Check whether a player can use an item for a PMMO requirement type.")
    public static boolean canUseItem(Player player, ReqType reqType, ItemStack stack) {
        return Core.get(player.level()).isActionPermitted(reqType, stack, player);
    }

    @Info("Check whether a player can interact with a block position for a PMMO requirement type.")
    public static boolean canUseBlock(Player player, ReqType reqType, Level level, BlockPos pos) {
        return Core.get(level).isActionPermitted(reqType, pos, player);
    }

    @Info("Check whether a player can interact with an entity for a PMMO requirement type.")
    public static boolean canUseEntity(Player player, ReqType reqType, Entity entity) {
        return Core.get(entity.level()).isActionPermitted(reqType, entity, player);
    }

    @Info("Check whether a player meets a PMMO requirement map.")
    public static boolean meetsRequirements(Player player, Map<String, ? extends Number> requirements) {
        return Core.get(player.level()).doesPlayerMeetReq(player.getUUID(), toIntegerMap(requirements));
    }

    @Info("Register PMMO requirement data directly.")
    public static void registerRequirementData(ObjectType objectType, String objectId, ReqType reqType, Map<String, ? extends Number> requirements, boolean replace) {
        APIUtils.registerRequirement(objectType, id(objectId), reqType, toIntegerMap(requirements), replace);
    }

    @Info("Register PMMO XP award data directly.")
    public static void registerXpAwardData(ObjectType objectType, String objectId, EventType type, Map<String, ? extends Number> awards, boolean replace) {
        APIUtils.registerXpAward(objectType, id(objectId), type, toLongMap(awards), replace);
    }

    @Info("Register PMMO damage XP award data directly.")
    public static void registerDamageXpAwardData(ObjectType objectType, String objectId, boolean dealt, String damageType, Map<String, ? extends Number> awards, boolean replace) {
        APIUtils.registerDamageXpAward(objectType, id(objectId), dealt, damageType, toLongMap(awards), replace);
    }

    @Info("Register PMMO bonus data directly.")
    public static void registerBonusData(ObjectType objectType, String objectId, ModifierDataType dataType, Map<String, ? extends Number> bonuses, boolean replace) {
        APIUtils.registerBonus(objectType, id(objectId), dataType, toDoubleMap(bonuses), replace);
    }

    @Info("Register PMMO negative effect data directly.")
    public static void registerNegativeEffects(ObjectType objectType, String objectId, Map<String, ? extends Number> effects, boolean replace) {
        APIUtils.registerNegativeEffect(objectType, id(objectId), toResourceIntMap(effects), replace);
    }

    @Info("Register PMMO positive effect data directly.")
    public static void registerPositiveEffects(ObjectType objectType, String objectId, Map<String, ? extends Number> effects, boolean replace) {
        APIUtils.registerPositiveEffect(objectType, id(objectId), toResourceIntMap(effects), replace);
    }

    @Info("Register PMMO salvage data directly.")
    public static void registerSalvageData(String objectId, Map<String, APIUtils.SalvageBuilder> salvage, boolean replace) {
        APIUtils.registerSalvage(id(objectId), toResourceSalvageMap(salvage), replace);
    }

    @Info("Register PMMO vein-mining data directly. Use null for values you want to leave empty.")
    public static void registerVeinData(ObjectType objectType, String objectId, Number maxBlocks, Number spread, Number refill, boolean replace) {
        APIUtils.registerVeinData(
                objectType,
                id(objectId),
                optionalInt(maxBlocks),
                optionalDouble(spread),
                optionalInt(refill),
                replace
        );
    }

    @Info("Register PMMO mob modifier data directly.")
    public static void registerMobModifierData(ObjectType objectType, String objectId, Map<String, List<MobModifier>> modifiers, boolean replace) {
        APIUtils.registerMobModifier(objectType, id(objectId), toResourceMobModifierMap(modifiers), replace);
    }

    @Info("Register a PMMO item action predicate.")
    public static void registerActionPredicate(String objectId, ReqType reqType, BiPredicate<Player, ItemStack> predicate) {
        APIUtils.registerActionPredicate(id(objectId), reqType, predicate);
    }

    @Info("Register a PMMO block break predicate.")
    public static void registerBreakPredicate(String objectId, ReqType reqType, BiPredicate<Player, net.minecraft.world.level.block.entity.BlockEntity> predicate) {
        APIUtils.registerBreakPredicate(id(objectId), reqType, predicate);
    }

    @Info("Register a PMMO entity predicate.")
    public static void registerEntityPredicate(String objectId, ReqType reqType, BiPredicate<Player, Entity> predicate) {
        APIUtils.registerEntityPredicate(id(objectId), reqType, predicate);
    }

    @Info("Register custom PMMO trigger logic for an event type.")
    public static void registerTriggerListener(String listenerId, EventType type, BiFunction<? super Event, CompoundTag, CompoundTag> listener) {
        APIUtils.registerListener(id(listenerId), type, listener);
    }

    @Info("Register PMMO item requirement tooltip data.")
    public static void registerItemRequirementTooltip(String objectId, ReqType reqType, Function<ItemStack, Map<String, ? extends Number>> provider) {
        APIUtils.registerItemRequirementTooltipData(id(objectId), reqType, stack -> toIntegerMap(provider.apply(stack)));
    }

    @Info("Register PMMO block requirement tooltip data.")
    public static void registerBlockRequirementTooltip(String objectId, ReqType reqType, Function<net.minecraft.world.level.block.entity.BlockEntity, Map<String, ? extends Number>> provider) {
        APIUtils.registerBlockRequirementTooltipData(id(objectId), reqType, blockEntity -> toIntegerMap(provider.apply(blockEntity)));
    }

    @Info("Register PMMO entity requirement tooltip data.")
    public static void registerEntityRequirementTooltip(String objectId, ReqType reqType, Function<Entity, Map<String, ? extends Number>> provider) {
        APIUtils.registerEntityRequirementTooltipData(id(objectId), reqType, entity -> toIntegerMap(provider.apply(entity)));
    }

    @Info("Register PMMO item XP tooltip data.")
    public static void registerItemXpTooltip(String objectId, EventType type, Function<ItemStack, Map<String, ? extends Number>> provider) {
        APIUtils.registerItemXpGainTooltipData(id(objectId), type, stack -> toLongMap(provider.apply(stack)));
    }

    @Info("Register PMMO block XP tooltip data.")
    public static void registerBlockXpTooltip(String objectId, EventType type, Function<net.minecraft.world.level.block.entity.BlockEntity, Map<String, ? extends Number>> provider) {
        APIUtils.registerBlockXpGainTooltipData(id(objectId), type, blockEntity -> toLongMap(provider.apply(blockEntity)));
    }

    @Info("Register PMMO entity XP tooltip data.")
    public static void registerEntityXpTooltip(String objectId, EventType type, Function<Entity, Map<String, ? extends Number>> provider) {
        APIUtils.registerEntityXpGainTooltipData(id(objectId), type, entity -> toLongMap(provider.apply(entity)));
    }

    @Info("Register PMMO item bonus tooltip data.")
    public static void registerItemBonusTooltip(String objectId, ModifierDataType dataType, Function<ItemStack, Map<String, ? extends Number>> provider) {
        APIUtils.registerItemBonusData(id(objectId), dataType, stack -> toDoubleMap(provider.apply(stack)));
    }

    @Info("Award PMMO XP directly to one player.")
    public static void awardXpToPlayer(ServerPlayer player, Map<String, ? extends Number> awards) {
        Core.get(player.level()).awardXP(List.of(player), toLongMap(awards));
    }

    @Info("Award PMMO XP directly to the player's nearby party.")
    public static void awardXpToPartyInRange(ServerPlayer player, Map<String, ? extends Number> awards) {
        Core.get(player.level()).awardXP(PartyUtils.getPartyMembersInRange(player), toLongMap(awards));
    }

    @Info("Award PMMO XP directly to the player's full party.")
    public static void awardXpToParty(ServerPlayer player, Map<String, ? extends Number> awards) {
        Core.get(player.level()).awardXP(PartyUtils.getPartyMembers(player), toLongMap(awards));
    }

    @Info("Get the player's nearby PMMO party members.")
    public static List<ServerPlayer> getPartyMembersInRange(ServerPlayer player) {
        return new ArrayList<>(PartyUtils.getPartyMembersInRange(player));
    }

    @Info("Get the player's PMMO party members.")
    public static List<ServerPlayer> getPartyMembers(ServerPlayer player) {
        return new ArrayList<>(PartyUtils.getPartyMembers(player));
    }

    @Info("Invite a player into a PMMO party.")
    public static void inviteToParty(ServerPlayer owner, Player target) {
        PartyUtils.inviteToParty(owner, target);
    }

    @Info("Remove a PMMO party invite.")
    public static void removePartyInvite(ServerPlayer owner, Player target) {
        PartyUtils.uninviteToParty(owner, target);
    }

    @Info("Accept a PMMO party invite.")
    public static void acceptPartyInvite(ServerPlayer player, String inviterId) {
        PartyUtils.acceptInvite(player, java.util.UUID.fromString(inviterId));
    }

    @Info("Decline a PMMO party invite.")
    public static boolean declinePartyInvite(String inviterId) {
        return PartyUtils.declineInvite(java.util.UUID.fromString(inviterId));
    }

    @Info("Check whether a player is currently in a PMMO party.")
    public static boolean isInParty(ServerPlayer player) {
        return PartyUtils.isInParty(player);
    }

    @Info("Apply PMMO vein-mining logic to a block position.")
    public static void applyVeinMining(ServerPlayer player, BlockPos pos) {
        VeinMiningLogic.applyVein(player, pos);
    }

    @Info("Regenerate PMMO vein-mining charge for a player.")
    public static void regenerateVeinCharge(ServerPlayer player) {
        VeinMiningLogic.regenerateVein(player);
    }

    @Info("Get the player's current PMMO vein-mining charge.")
    public static int getCurrentVeinCharge(Player player) {
        return VeinMiningLogic.getCurrentCharge(player);
    }

    @Info("Get the player's maximum PMMO vein-mining charge from all items.")
    public static int getMaxVeinCharge(Player player) {
        return VeinMiningLogic.getMaxChargeFromAllItems(player);
    }

    @Info("Open the PMMO salvage flow for a player.")
    public static void triggerSalvage(ServerPlayer player) {
        Core.get(player.level()).getSalvage(player);
    }

    @Info("Set the PMMO marked position for a player.")
    public static void setMarkedPos(Player player, BlockPos pos) {
        Core.get(player.level()).setMarkedPos(player.getUUID(), pos);
    }

    @Info("Get the PMMO marked position for a player.")
    public static BlockPos getMarkedPos(Player player) {
        return Core.get(player.level()).getMarkedPos(player.getUUID());
    }

    @Info("Get PMMO block consume data for a block.")
    public static int getBlockConsume(Block block) {
        return Core.get(LogicalSide.SERVER).getBlockConsume(block);
    }

    @Info("Create a PMMO mob modifier entry.")
    public static MobModifier createMobModifier(String attributeId, Number amount, AttributeModifier.Operation operation) {
        return new MobModifier(id(attributeId), amount.doubleValue(), operation);
    }

    @HideFromJS
    public static Map<String, Integer> toIntegerMap(Map<String, ? extends Number> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        for (Map.Entry<String, ? extends Number> entry : source.entrySet()) {
            Number value = entry.getValue();
            if (value != null) {
                map.put(entry.getKey(), value.intValue());
            }
        }

        return map;
    }

    @HideFromJS
    public static Map<String, Long> toLongMap(Map<String, ? extends Number> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> map = new LinkedHashMap<>();
        for (Map.Entry<String, ? extends Number> entry : source.entrySet()) {
            Number value = entry.getValue();
            if (value != null) {
                map.put(entry.getKey(), value.longValue());
            }
        }

        return map;
    }

    @HideFromJS
    public static Map<String, Double> toDoubleMap(Map<String, ? extends Number> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> map = new LinkedHashMap<>();
        for (Map.Entry<String, ? extends Number> entry : source.entrySet()) {
            Number value = entry.getValue();
            if (value != null) {
                map.put(entry.getKey(), value.doubleValue());
            }
        }

        return map;
    }

    @HideFromJS
    public static Map<ResourceLocation, Integer> toResourceIntMap(Map<String, ? extends Number> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<ResourceLocation, Integer> map = new LinkedHashMap<>();
        for (Map.Entry<String, ? extends Number> entry : source.entrySet()) {
            Number value = entry.getValue();
            if (value != null) {
                map.put(id(entry.getKey()), value.intValue());
            }
        }

        return map;
    }

    @HideFromJS
    public static Map<ResourceLocation, APIUtils.SalvageBuilder> toResourceSalvageMap(Map<String, APIUtils.SalvageBuilder> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<ResourceLocation, APIUtils.SalvageBuilder> map = new LinkedHashMap<>();
        for (Map.Entry<String, APIUtils.SalvageBuilder> entry : source.entrySet()) {
            if (entry.getValue() != null) {
                map.put(id(entry.getKey()), entry.getValue());
            }
        }

        return map;
    }

    @HideFromJS
    public static Map<ResourceLocation, List<MobModifier>> toResourceMobModifierMap(Map<String, List<MobModifier>> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<ResourceLocation, List<MobModifier>> map = new LinkedHashMap<>();
        for (Map.Entry<String, List<MobModifier>> entry : source.entrySet()) {
            if (entry.getValue() != null) {
                map.put(id(entry.getKey()), new ArrayList<>(entry.getValue()));
            }
        }

        return map;
    }

    @HideFromJS
    public static Optional<Integer> optionalInt(Number value) {
        return value == null ? Optional.empty() : Optional.of(value.intValue());
    }

    @HideFromJS
    public static Optional<Double> optionalDouble(Number value) {
        return value == null ? Optional.empty() : Optional.of(value.doubleValue());
    }
}
