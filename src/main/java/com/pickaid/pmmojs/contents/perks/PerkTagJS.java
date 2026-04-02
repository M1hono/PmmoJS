package com.pickaid.pmmojs.contents.perks;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class PerkTagJS {
    private final CompoundTag tag;

    public PerkTagJS(CompoundTag tag) {
        this.tag = tag == null ? new CompoundTag() : tag;
    }

    @Info("Get all keys currently stored in this perk tag.")
    public Set<String> keys() {
        return tag.getAllKeys();
    }

    @Info("Check whether this perk tag contains a key.")
    public boolean has(String key) {
        return tag.contains(key);
    }

    @Info("Check whether this perk tag contains a nested compound tag.")
    public boolean hasCompound(String key) {
        return tag.contains(key, Tag.TAG_COMPOUND);
    }

    @Info("Remove a key from this perk tag.")
    public void remove(String key) {
        tag.remove(key);
    }

    @Info("Clear all keys from this perk tag.")
    public void clear() {
        for (String key : List.copyOf(tag.getAllKeys())) {
            tag.remove(key);
        }
    }

    @Info("Get a string value from this perk tag.")
    public String getString(String key) {
        return tag.getString(key);
    }

    @Info("Get a string value from this perk tag, or a fallback when it is missing.")
    public String getStringOr(String key, String fallback) {
        return has(key) ? getString(key) : fallback;
    }

    @Info("Get an int value from this perk tag.")
    public int getInt(String key) {
        return tag.getInt(key);
    }

    @Info("Get an int value from this perk tag, or a fallback when it is missing.")
    public int getIntOr(String key, Number fallback) {
        return has(key) ? getInt(key) : fallback.intValue();
    }

    @Info("Get a long value from this perk tag.")
    public long getLong(String key) {
        return tag.getLong(key);
    }

    @Info("Get a long value from this perk tag, or a fallback when it is missing.")
    public long getLongOr(String key, Number fallback) {
        return has(key) ? getLong(key) : fallback.longValue();
    }

    @Info("Get a float value from this perk tag.")
    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    @Info("Get a float value from this perk tag, or a fallback when it is missing.")
    public float getFloatOr(String key, Number fallback) {
        return has(key) ? getFloat(key) : fallback.floatValue();
    }

    @Info("Get a double value from this perk tag.")
    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    @Info("Get a double value from this perk tag, or a fallback when it is missing.")
    public double getDoubleOr(String key, Number fallback) {
        return has(key) ? getDouble(key) : fallback.doubleValue();
    }

    @Info("Get a boolean value from this perk tag.")
    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    @Info("Get a boolean value from this perk tag, or a fallback when it is missing.")
    public boolean getBooleanOr(String key, boolean fallback) {
        return has(key) ? getBoolean(key) : fallback;
    }

    @Info("Get or create a nested compound tag and wrap it for JS use.")
    public PerkTagJS getOrCreateCompound(String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) {
            tag.put(key, new CompoundTag());
        }

        return new PerkTagJS(tag.getCompound(key));
    }

    @Info("Set a string value on this perk tag.")
    public PerkTagJS putString(String key, String value) {
        tag.putString(key, value);
        return this;
    }

    @Info("Set a boolean value on this perk tag.")
    public PerkTagJS putBoolean(String key, boolean value) {
        tag.putBoolean(key, value);
        return this;
    }

    @Info("Set an int value on this perk tag.")
    public PerkTagJS putInt(String key, Number value) {
        tag.putInt(key, value.intValue());
        return this;
    }

    @Info("Set a long value on this perk tag.")
    public PerkTagJS putLong(String key, Number value) {
        tag.putLong(key, value.longValue());
        return this;
    }

    @Info("Set a float value on this perk tag.")
    public PerkTagJS putFloat(String key, Number value) {
        tag.putFloat(key, value.floatValue());
        return this;
    }

    @Info("Set a double value on this perk tag.")
    public PerkTagJS putDouble(String key, Number value) {
        tag.putDouble(key, value.doubleValue());
        return this;
    }

    @Info("Set a nested compound tag on this perk tag.")
    public PerkTagJS putCompound(String key, Object value) {
        if (value instanceof PerkTagJS perkTag) {
            tag.put(key, perkTag.raw().copy());
            return this;
        }

        if (value instanceof CompoundTag compoundTag) {
            tag.put(key, compoundTag.copy());
            return this;
        }

        throw new IllegalArgumentException("Unsupported compound value: " + value);
    }

    @Info("Set a list of strings on this perk tag.")
    public PerkTagJS putStringList(String key, Object values) {
        List<?> listValues = ListJS.orSelf(Wrapper.unwrapped(values));
        ListTag list = new ListTag();
        for (Object value : listValues) {
            list.add(StringTag.valueOf(String.valueOf(value)));
        }

        tag.put(key, list);
        return this;
    }

    @Info("Set a list of numeric values on this perk tag. Values are stored as doubles.")
    public PerkTagJS putNumberList(String key, Object values) {
        List<?> listValues = ListJS.orSelf(Wrapper.unwrapped(values));
        ListTag list = new ListTag();
        for (Object value : listValues) {
            if (!(value instanceof Number number)) {
                throw new IllegalArgumentException("Non-number in numeric perk tag list: " + value);
            }

            list.add(DoubleTag.valueOf(number.doubleValue()));
        }

        tag.put(key, list);
        return this;
    }

    @Info("Merge another compound tag into this perk tag.")
    public PerkTagJS merge(Object value) {
        if (value instanceof PerkTagJS perkTag) {
            tag.merge(perkTag.raw().copy());
            return this;
        }

        if (value instanceof CompoundTag compoundTag) {
            tag.merge(compoundTag.copy());
            return this;
        }

        throw new IllegalArgumentException("Unsupported merge value: " + value);
    }

    @Info("Create a detached copy of this perk tag wrapper.")
    public PerkTagJS copy() {
        return new PerkTagJS(tag.copy());
    }

    @Info("Get the perk skill key from this settings tag.")
    public String getSkill() {
        return has("skill") ? getString("skill") : null;
    }

    @Info("Get the resolved PMMO level currently injected into this settings tag.")
    public int getResolvedLevel() {
        return getInt("level");
    }

    @Info("Get the configured cooldown in ticks.")
    public int getCooldown() {
        return getInt("cooldown");
    }

    @Info("Get the configured duration in ticks.")
    public int getDuration() {
        return getInt("duration");
    }

    @Info("Get the configured activation chance.")
    public double getChance() {
        return getDouble("chance");
    }

    @HideFromJS
    public CompoundTag raw() {
        return tag;
    }
}
