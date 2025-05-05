package com.pickaid.pmmojs.contents.settings;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Info("Base class for all PMMO settings builders")
public abstract class PMMOSettingsBuilder<T extends PMMOSettingsBuilder<T>> {
    protected final ObjectType objectType;
    protected final ResourceLocation objectId;
    protected boolean isOverride = true;

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    public PMMOSettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        this.objectType = objectType;
        this.objectId = objectId;
    }

    @Info("Sets a specific skill requirement level")
    public T setRequirement(ReqType reqType, String skill, Number level) {
        Map<String, Integer> reqs = new HashMap<>();
        reqs.put(skill, level.intValue());
        return requirement(reqType, reqs);
    }

    @Info("Sets requirement levels for specific skills")
    public T requirement(ReqType reqType, Map<String, ? extends Number> requirements) {
        Map<String, Integer> intRequirements = new HashMap<>();

        for (Map.Entry<String, ? extends Number> entry : requirements.entrySet()) {
            intRequirements.put(entry.getKey(), entry.getValue().intValue());
        }

        APIUtils.registerRequirement(objectType, objectId, reqType, intRequirements, isOverride);
        return self();
    }

    @Info("Sets XP awards for an event type")
    public T xp(EventType eventType, Map<String, ? extends Number> xpAwards) {
        Map<String, Long> longXpAwards = new HashMap<>();

        for (Map.Entry<String, ? extends Number> entry : xpAwards.entrySet()) {
            longXpAwards.put(entry.getKey(), entry.getValue().longValue());
        }

        APIUtils.registerXpAward(objectType, objectId, eventType, longXpAwards, isOverride);
        return self();
    }

    @Info("Sets a specific skill XP award")
    public T setXp(EventType eventType, String skill, Number amount) {
        Map<String, Long> xps = new HashMap<>();
        xps.put(skill, amount.longValue());
        return xp(eventType, xps);
    }

    @Info("Sets the object as an override. You must enable this to change vanilla stuff.")
    public abstract T override(boolean override);

    public ResourceLocation getObjectId() {
        return objectId;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public boolean isOverride() {
        return isOverride;
    }
}