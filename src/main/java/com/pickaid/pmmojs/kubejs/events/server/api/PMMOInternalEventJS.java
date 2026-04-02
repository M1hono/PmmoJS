package com.pickaid.pmmojs.kubejs.events.server.api;

import com.pickaid.pmmojs.utils.PmmoHelper;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.core.CoreUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PMMOInternalEventJS extends EventJS {
    private final PMMOInternalType type;
    private final Event forgeEvent;
    private final CompoundTag context;
    private final Player player;
    private boolean skipPmmo;
    private boolean actionCancelled;

    public PMMOInternalEventJS(PMMOInternalType type, Event forgeEvent) {
        this.type = type;
        this.forgeEvent = forgeEvent;
        this.context = new CompoundTag();
        this.player = resolvePlayer(forgeEvent);
        this.skipPmmo = false;
        this.actionCancelled = forgeEvent != null && forgeEvent.isCancelable() && forgeEvent.isCanceled();
    }

    @Info("Get the PMMO internal hook type enum. Compare against PMMOInternalType.*")
    public PMMOInternalType getType() {
        return type;
    }

    @Info("Get the PMMO internal hook id string.")
    public String getTypeId() {
        return type.getId();
    }

    @Info("Check whether this internal PMMO hook matches a specific enum.")
    public boolean isType(PMMOInternalType type) {
        return this.type == type;
    }

    @Info("Check whether this internal PMMO hook matches a specific id string.")
    public boolean hasTypeId(String typeId) {
        PMMOInternalType candidate = PmmoHelper.coerceInternalType(typeId);
        return candidate == type;
    }

    @Info("Get the underlying Forge event instance passed through this PMMO internal hook.")
    public Event getForgeEvent() {
        return forgeEvent;
    }

    @Info("Get the underlying Forge event class name.")
    public String getForgeEventClassName() {
        return forgeEvent == null ? null : forgeEvent.getClass().getName();
    }

    @Info("Check whether this internal PMMO hook has a player.")
    public boolean hasPlayer() {
        return player != null;
    }

    @Info("Get the player involved in this PMMO internal hook, or null when there is none.")
    public Player getPlayer() {
        return player;
    }

    @Info("Get the server player involved in this PMMO internal hook, or null when it is not a server player.")
    public ServerPlayer getServerPlayer() {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    @Info("Skip PMMO's built-in handler logic for this hook.")
    public void skipPmmo() {
        this.skipPmmo = true;
    }

    @Info("Set whether PMMO's built-in handler logic should be skipped for this hook.")
    public void setSkipPmmo(boolean skipPmmo) {
        this.skipPmmo = skipPmmo;
    }

    @Info("Check whether PMMO's built-in handler logic will be skipped.")
    public boolean shouldSkipPmmo() {
        return skipPmmo;
    }

    @Info("Check whether the wrapped Forge event supports cancellation.")
    public boolean canCancelAction() {
        return forgeEvent != null && forgeEvent.isCancelable();
    }

    @Info("Set whether the underlying Forge action should be cancelled when the event is cancelable.")
    public void setActionCancelled(boolean actionCancelled) {
        this.actionCancelled = actionCancelled;
        if (actionCancelled) {
            this.skipPmmo = true;
        }
    }

    @Info("Cancel the underlying Forge action when the wrapped Forge event is cancelable.")
    public void deny() {
        setActionCancelled(true);
    }

    @Info("Check whether the underlying Forge action will be cancelled.")
    public boolean isActionCancelled() {
        return actionCancelled;
    }

    @Info("Get the mutable internal PMMO hook context tag.")
    public CompoundTag getContext() {
        return context;
    }

    @Info("Get all keys currently present on the internal PMMO hook context.")
    public Set<String> getContextKeys() {
        return context.getAllKeys();
    }

    @Info("Check whether the internal PMMO hook context contains a key.")
    public boolean hasContextKey(String key) {
        return context.contains(key);
    }

    @Info("Remove a key from the internal PMMO hook context.")
    public void removeContextKey(String key) {
        context.remove(key);
    }

    @Info("Get a boolean from the internal PMMO hook context.")
    public boolean getContextBoolean(String key) {
        return context.getBoolean(key);
    }

    @Info("Get a string from the internal PMMO hook context.")
    public String getContextString(String key) {
        return context.getString(key);
    }

    @Info("Get an int from the internal PMMO hook context.")
    public int getContextInt(String key) {
        return context.getInt(key);
    }

    @Info("Get a long from the internal PMMO hook context.")
    public long getContextLong(String key) {
        return context.getLong(key);
    }

    @Info("Get a double from the internal PMMO hook context.")
    public double getContextDouble(String key) {
        return context.getDouble(key);
    }

    @Info("Get a nested compound tag from the internal PMMO hook context.")
    public CompoundTag getContextCompound(String key) {
        return context.getCompound(key);
    }

    @Info("Put a boolean into the internal PMMO hook context.")
    public void putContextBoolean(String key, boolean value) {
        context.putBoolean(key, value);
    }

    @Info("Put a string into the internal PMMO hook context.")
    public void putContextString(String key, String value) {
        context.putString(key, value);
    }

    @Info("Put an int into the internal PMMO hook context.")
    public void putContextInt(String key, Number value) {
        context.putInt(key, value.intValue());
    }

    @Info("Put a long into the internal PMMO hook context.")
    public void putContextLong(String key, Number value) {
        context.putLong(key, value.longValue());
    }

    @Info("Put a double into the internal PMMO hook context.")
    public void putContextDouble(String key, Number value) {
        context.putDouble(key, value.doubleValue());
    }

    @Info("Put a nested compound tag into the internal PMMO hook context.")
    public void putContextCompound(String key, CompoundTag value) {
        context.put(key, value);
    }

    @Info("Get the PMMO XP award map currently stored in this internal PMMO hook context.")
    public Map<String, Long> getXpAwards() {
        if (!context.contains(APIUtils.SERIALIZED_AWARD_MAP, Tag.TAG_COMPOUND)) {
            return new LinkedHashMap<>();
        }

        return new LinkedHashMap<>(CoreUtils.deserializeAwardMap(context.getCompound(APIUtils.SERIALIZED_AWARD_MAP)));
    }

    @Info("Replace the PMMO XP award map stored in this internal PMMO hook context.")
    public void setXpAwards(Map<String, ? extends Number> awards) {
        context.put(APIUtils.SERIALIZED_AWARD_MAP, APIUtils.serializeAwardMap(PmmoHelper.toLongMap(awards)));
    }

    @Info("Set one skill award inside the internal PMMO hook context.")
    public void setXpAward(String skill, Number amount) {
        Map<String, Long> awards = getXpAwards();
        awards.put(skill, amount.longValue());
        setXpAwards(awards);
    }

    @Info("Add to one skill award inside the internal PMMO hook context.")
    public void addXpAward(String skill, Number amount) {
        Map<String, Long> awards = getXpAwards();
        awards.merge(skill, amount.longValue(), Long::sum);
        setXpAwards(awards);
    }

    @Info("Clear the PMMO XP award map stored in this internal PMMO hook context.")
    public void clearXpAwards() {
        context.remove(APIUtils.SERIALIZED_AWARD_MAP);
    }

    @HideFromJS
    public void applyActionCancellation() {
        if (actionCancelled && forgeEvent != null && forgeEvent.isCancelable()) {
            forgeEvent.setCanceled(true);
        }
    }

    private static Player resolvePlayer(Event event) {
        if (event instanceof PlayerEvent playerEvent) {
            return playerEvent.getEntity();
        }

        if (event instanceof LivingEvent livingEvent && livingEvent.getEntity() instanceof Player player) {
            return player;
        }

        if (event instanceof EntityEvent entityEvent && entityEvent.getEntity() instanceof Player player) {
            return player;
        }

        if (event == null) {
            return null;
        }

        Player fromMethod = invokePlayerGetter(event, "getPlayer");
        if (fromMethod != null) {
            return fromMethod;
        }

        fromMethod = invokePlayerGetter(event, "getEntityMounting");
        if (fromMethod != null) {
            return fromMethod;
        }

        Entity entity = invokeEntityGetter(event, "getEntity");
        if (entity instanceof Player player) {
            return player;
        }

        entity = invokeEntityGetter(event, "getEntityMounting");
        if (entity instanceof Player player) {
            return player;
        }

        return null;
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

    private static Entity invokeEntityGetter(Event event, String methodName) {
        try {
            Method method = event.getClass().getMethod(methodName);
            Object value = method.invoke(event);
            return value instanceof Entity entity ? entity : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
