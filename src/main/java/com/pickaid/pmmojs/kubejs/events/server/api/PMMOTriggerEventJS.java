package com.pickaid.pmmojs.kubejs.events.server.api;

import com.pickaid.pmmojs.utils.PmmoHelper;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.core.CoreUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PMMOTriggerEventJS extends EventJS {
    private final EventType type;
    private final Event forgeEvent;
    private final CompoundTag context;
    private final Player player;

    public PMMOTriggerEventJS(EventType type, Event forgeEvent, CompoundTag context) {
        this.type = type;
        this.forgeEvent = forgeEvent;
        this.context = context == null ? new CompoundTag() : context;
        this.player = resolvePlayer(forgeEvent);
    }

    @Info("Get the PMMO trigger type enum. Compare against EventType.*")
    public EventType getType() {
        return type;
    }

    @Info("Get the PMMO trigger type id string.")
    public String getTypeId() {
        return type.getName();
    }

    @Info("Check whether this trigger matches a specific PMMO event type enum.")
    public boolean isType(EventType type) {
        return this.type == type;
    }

    @Info("Check whether this trigger matches a specific PMMO event type id.")
    public boolean hasTypeId(String typeId) {
        EventType candidate = PmmoHelper.coerceEventType(typeId);
        return candidate == type;
    }

    @Info("Get the underlying Forge event instance that PMMO passed into its trigger registry.")
    public Event getForgeEvent() {
        return forgeEvent;
    }

    @Info("Get the underlying Forge event class name.")
    public String getForgeEventClassName() {
        return forgeEvent == null ? null : forgeEvent.getClass().getName();
    }

    @Info("Check whether PMMO supplied a player for this trigger.")
    public boolean hasPlayer() {
        return player != null;
    }

    @Info("Get the player involved in this trigger, or null when the PMMO trigger has no player.")
    public Player getPlayer() {
        return player;
    }

    @Info("Get the server player involved in this trigger, or null when it is not a server player.")
    public ServerPlayer getServerPlayer() {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    @Info("Get the mutable PMMO trigger context tag.")
    public CompoundTag getContext() {
        return context;
    }

    @Info("Get all keys currently present on the PMMO trigger context.")
    public Set<String> getContextKeys() {
        return context.getAllKeys();
    }

    @Info("Check whether the PMMO trigger context contains a key.")
    public boolean hasContextKey(String key) {
        return context.contains(key);
    }

    @Info("Remove a key from the PMMO trigger context.")
    public void removeContextKey(String key) {
        context.remove(key);
    }

    @Info("Check whether PMMO marked this trigger as cancelled.")
    public boolean isCancelled() {
        return context.getBoolean(APIUtils.IS_CANCELLED);
    }

    @Info("Set whether PMMO should treat this trigger as cancelled.")
    public void setCancelled(boolean cancelled) {
        context.putBoolean(APIUtils.IS_CANCELLED, cancelled);
    }

    @Info("Get a boolean from the PMMO trigger context.")
    public boolean getContextBoolean(String key) {
        return context.getBoolean(key);
    }

    @Info("Get a string from the PMMO trigger context.")
    public String getContextString(String key) {
        return context.getString(key);
    }

    @Info("Get an int from the PMMO trigger context.")
    public int getContextInt(String key) {
        return context.getInt(key);
    }

    @Info("Get a long from the PMMO trigger context.")
    public long getContextLong(String key) {
        return context.getLong(key);
    }

    @Info("Get a double from the PMMO trigger context.")
    public double getContextDouble(String key) {
        return context.getDouble(key);
    }

    @Info("Get a nested compound tag from the PMMO trigger context.")
    public CompoundTag getContextCompound(String key) {
        return context.getCompound(key);
    }

    @Info("Put a boolean into the PMMO trigger context.")
    public void putContextBoolean(String key, boolean value) {
        context.putBoolean(key, value);
    }

    @Info("Put a string into the PMMO trigger context.")
    public void putContextString(String key, String value) {
        context.putString(key, value);
    }

    @Info("Put an int into the PMMO trigger context.")
    public void putContextInt(String key, Number value) {
        context.putInt(key, value.intValue());
    }

    @Info("Put a long into the PMMO trigger context.")
    public void putContextLong(String key, Number value) {
        context.putLong(key, value.longValue());
    }

    @Info("Put a double into the PMMO trigger context.")
    public void putContextDouble(String key, Number value) {
        context.putDouble(key, value.doubleValue());
    }

    @Info("Put a nested compound tag into the PMMO trigger context.")
    public void putContextCompound(String key, CompoundTag value) {
        context.put(key, value);
    }

    @Info("Get the XP award map currently stored in this PMMO trigger context.")
    public Map<String, Long> getXpAwards() {
        if (!context.contains(APIUtils.SERIALIZED_AWARD_MAP, Tag.TAG_COMPOUND)) {
            return new LinkedHashMap<>();
        }

        return new LinkedHashMap<>(CoreUtils.deserializeAwardMap(context.getCompound(APIUtils.SERIALIZED_AWARD_MAP)));
    }

    @Info("Replace the XP award map stored in this PMMO trigger context.")
    public void setXpAwards(Map<String, ? extends Number> awards) {
        context.put(APIUtils.SERIALIZED_AWARD_MAP, APIUtils.serializeAwardMap(PmmoHelper.toLongMap(awards)));
    }

    @Info("Set one skill award inside the PMMO trigger context.")
    public void setXpAward(String skill, Number amount) {
        Map<String, Long> awards = getXpAwards();
        awards.put(skill, amount.longValue());
        setXpAwards(awards);
    }

    @Info("Add to one skill award inside the PMMO trigger context.")
    public void addXpAward(String skill, Number amount) {
        Map<String, Long> awards = getXpAwards();
        awards.merge(skill, amount.longValue(), Long::sum);
        setXpAwards(awards);
    }

    @Info("Clear the XP award map stored in this PMMO trigger context.")
    public void clearXpAwards() {
        context.remove(APIUtils.SERIALIZED_AWARD_MAP);
    }

    @HideFromJS
    public CompoundTag getMutableContext() {
        return context;
    }

    private static Player resolvePlayer(Event event) {
        if (event instanceof PlayerEvent playerEvent) {
            return playerEvent.getEntity();
        }

        if (event == null) {
            return null;
        }

        Player fromMethod = invokePlayerGetter(event, "getPlayer");
        if (fromMethod != null) {
            return fromMethod;
        }

        return invokePlayerGetter(event, "getEntity");
    }

    private static Player invokePlayerGetter(Event event, String methodName) {
        try {
            Method method = event.getClass().getMethod(methodName);
            Object value = method.invoke(event);
            return value instanceof Player player ? player : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
