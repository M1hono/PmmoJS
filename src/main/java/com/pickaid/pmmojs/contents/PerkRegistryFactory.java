package com.pickaid.pmmojs.contents;

import com.pickaid.pmmojs.contents.perks.PerkConditionContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStartContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStatusContextJS;
import com.pickaid.pmmojs.contents.perks.PerkStopContextJS;
import com.pickaid.pmmojs.contents.perks.PerkTagJS;
import com.pickaid.pmmojs.contents.perks.PerkTickContextJS;
import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.perks.Perk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PerkRegistryFactory {
    private final ResourceLocation perkId;
    private final PerksRegistryEventJS event;
    private PerksRegistryEventJS.Side side;
    private BiPredicate<Player, CompoundTag> conditions;
    private CompoundTag propertyDefaults;
    private BiFunction<Player, CompoundTag, CompoundTag> start;
    private TriFunction<Player, CompoundTag, Integer, CompoundTag> tick;
    private BiFunction<Player, CompoundTag, CompoundTag> stop;
    private MutableComponent description;
    private BiFunction<Player, CompoundTag, List<MutableComponent>> status;

    public PerkRegistryFactory(ResourceLocation perkId, PerksRegistryEventJS event, PerksRegistryEventJS.Side side) {
        this.event = event;
        this.perkId = perkId;
        this.side = side;
        this.conditions = (p, n) -> true;
        this.propertyDefaults = new CompoundTag();
        this.start = (p, c) -> new CompoundTag();
        this.tick = (p, c, i) -> new CompoundTag();
        this.stop = (p, c) -> new CompoundTag();
        this.description = Component.empty();
        this.status = (p, s) -> List.of();
    }

    @Info("Add default perk settings that act as fallbacks before perks.toml data, incoming trigger data, and prior perk output are merged.")
    public PerkRegistryFactory defaults(Consumer<PerkTagJS> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Perk defaults consumer cannot be null");
        }

        consumer.accept(new PerkTagJS(propertyDefaults));
        return this;
    }

    @Info("Set a default skill key for this perk.")
    public PerkRegistryFactory withSkill(String skill) {
        propertyDefaults.putString("skill", skill);
        return this;
    }

    @Info("Set a default cooldown in ticks for this perk.")
    public PerkRegistryFactory withCooldown(Number cooldown) {
        propertyDefaults.putInt("cooldown", cooldown.intValue());
        return this;
    }

    @Info("Set a default active duration in ticks for this perk.")
    public PerkRegistryFactory withDuration(Number duration) {
        propertyDefaults.putInt("duration", duration.intValue());
        return this;
    }

    @Info("Set a default activation chance for this perk.")
    public PerkRegistryFactory withChance(Number chance) {
        propertyDefaults.putDouble("chance", chance.doubleValue());
        return this;
    }

    @Info("Set a default minimum level requirement for this perk.")
    public PerkRegistryFactory withMinLevel(Number minLevel) {
        propertyDefaults.putInt("min_level", minLevel.intValue());
        return this;
    }

    @Info("Set a default maximum level requirement for this perk.")
    public PerkRegistryFactory withMaxLevel(Number maxLevel) {
        propertyDefaults.putInt("max_level", maxLevel.intValue());
        return this;
    }

    @Info("Set a default level interval requirement for this perk.")
    public PerkRegistryFactory withPerXLevel(Number perXLevel) {
        propertyDefaults.putInt("per_x_level", perXLevel.intValue());
        return this;
    }

    @Info("Set default level milestones for this perk.")
    public PerkRegistryFactory withMilestones(Object milestones) {
        new PerkTagJS(propertyDefaults).putNumberList("milestones", milestones);
        return this;
    }

    @Info("Set a default string property for this perk.")
    public PerkRegistryFactory withString(String key, String value) {
        propertyDefaults.putString(key, value);
        return this;
    }

    @Info("Set a default boolean property for this perk.")
    public PerkRegistryFactory withBool(String key, boolean value) {
        propertyDefaults.putBoolean(key, value);
        return this;
    }

    @Info("Set a default float property for this perk.")
    public PerkRegistryFactory withFloat(String key, Number value) {
        propertyDefaults.putFloat(key, value.floatValue());
        return this;
    }

    @Info("Set a default int property for this perk.")
    public PerkRegistryFactory withInt(String key, Number value) {
        propertyDefaults.putInt(key, value.intValue());
        return this;
    }

    @Info("Set a default double property for this perk.")
    public PerkRegistryFactory withDouble(String key, Number value) {
        propertyDefaults.putDouble(key, value.doubleValue());
        return this;
    }

    @Info("Set a default long property for this perk.")
    public PerkRegistryFactory withLong(String key, Number value) {
        propertyDefaults.putLong(key, value.longValue());
        return this;
    }

    @Info("Set a default string list property for this perk.")
    public PerkRegistryFactory withStringList(String key, Object values) {
        new PerkTagJS(propertyDefaults).putStringList(key, values);
        return this;
    }

    @Info("Set a default numeric list property for this perk.")
    public PerkRegistryFactory withNumberList(String key, Object values) {
        new PerkTagJS(propertyDefaults).putNumberList(key, values);
        return this;
    }

    @Info("Set a default nested compound property for this perk.")
    public PerkRegistryFactory withCompound(String key, Object value) {
        new PerkTagJS(propertyDefaults).putCompound(key, value);
        return this;
    }

    @Info("Set extra activation conditions using a JS-friendly context wrapper.")
    public PerkRegistryFactory conditions(Predicate<PerkConditionContextJS> conditions) {
        if (conditions == null) {
            throw new IllegalArgumentException("Perk condition callback cannot be null");
        }

        this.conditions = (player, settings) -> conditions.test(new PerkConditionContextJS(perkId, player, new PerkTagJS(settings)));
        return this;
    }

    @Info("Sets the conditions for activating the perk.")
    @HideFromJS
    public PerkRegistryFactory conditions(BiPredicate<Player, CompoundTag> conditions) {
        this.conditions = conditions;
        return this;
    }

    @Info("Sets the default properties for the perk.")
    @HideFromJS
    public PerkRegistryFactory propertyDefaults(CompoundTag defaults) {
        this.propertyDefaults = defaults;
        return this;
    }

    @Info("Handle perk start with access to merged settings and a mutable result tag.")
    public PerkRegistryFactory start(Consumer<PerkStartContextJS> start) {
        if (start == null) {
            throw new IllegalArgumentException("Perk start callback cannot be null");
        }

        this.start = (player, settings) -> {
            PerkStartContextJS context = new PerkStartContextJS(perkId, player, new PerkTagJS(settings), new PerkTagJS(new CompoundTag()));
            start.accept(context);
            return context.rawResult().copy();
        };
        return this;
    }

    @Info("Sets the start function for the perk.")
    @HideFromJS
    public PerkRegistryFactory start(BiFunction<Player, CompoundTag, CompoundTag> start) {
        this.start = start;
        return this;
    }

    @Info("Handle perk tick logic with access to merged settings and elapsed ticks.")
    public PerkRegistryFactory tick(Consumer<PerkTickContextJS> tick) {
        if (tick == null) {
            throw new IllegalArgumentException("Perk tick callback cannot be null");
        }

        this.tick = (player, settings, elapsedTicks) -> {
            tick.accept(new PerkTickContextJS(perkId, player, new PerkTagJS(settings), elapsedTicks));
            return new CompoundTag();
        };
        return this;
    }

    @Info("Sets the tick function for the perk.")
    @HideFromJS
    public PerkRegistryFactory tick(TriFunction<Player, CompoundTag, Integer, CompoundTag> tick) {
        this.tick = tick;
        return this;
    }

    @Info("Handle perk stop logic with access to the persisted settings/source tag.")
    public PerkRegistryFactory stop(Consumer<PerkStopContextJS> stop) {
        if (stop == null) {
            throw new IllegalArgumentException("Perk stop callback cannot be null");
        }

        this.stop = (player, settings) -> {
            stop.accept(new PerkStopContextJS(perkId, player, new PerkTagJS(settings)));
            return new CompoundTag();
        };
        return this;
    }

    @Info("Sets the stop function for the perk.")
    @HideFromJS
    public PerkRegistryFactory stop(BiFunction<Player, CompoundTag, CompoundTag> stop) {
        this.stop = stop;
        return this;
    }

    @Info("Set the description for this perk. Strings are converted to literal text components.")
    public PerkRegistryFactory description(Object description) {
        this.description = coerceComponent(description);
        return this;
    }

    @Info("Sets the description for the perk.")
    @HideFromJS
    public PerkRegistryFactory description(MutableComponent description) {
        this.description = description;
        return this;
    }

    @Info("Build perk status lines with a JS-friendly status context.")
    public PerkRegistryFactory status(Consumer<PerkStatusContextJS> status) {
        if (status == null) {
            throw new IllegalArgumentException("Perk status callback cannot be null");
        }

        this.status = (player, settings) -> {
            PerkStatusContextJS context = new PerkStatusContextJS(perkId, player, new PerkTagJS(settings));
            status.accept(context);
            return context.rawLines();
        };
        return this;
    }

    @Info("Sets the status function for the perk.")
    @HideFromJS
    public PerkRegistryFactory status(BiFunction<Player, CompoundTag, List<MutableComponent>> status) {
        this.status = status;
        return this;
    }

    @Info("Builds, registers, and returns the Perk object.")
    public Perk register() {
        Perk perk = new Perk(conditions, propertyDefaults, start, tick, stop, description, status);
        event.registerPerk(perkId, perk, side);
        return perk;
    }


    @Info("Checks if the perk can be activated for the given player and settings.")
    public boolean canActivate(Player player, CompoundTag settings) {
        return Perk.VALID_CONTEXT.test(player, settings) && this.conditions.test(player, settings);
    }

    @Info("Starts the perk for the given player and NBT data.")
    @HideFromJS
    public CompoundTag start(Player player, CompoundTag nbt) {
        return this.start.apply(player, nbt);
    }

    @Info("Ticks the perk for the given player, NBT data, and elapsed ticks.")
    @HideFromJS
    public CompoundTag tick(Player player, CompoundTag nbt, int elapsedTicks) {
        return this.tick.apply(player, nbt, elapsedTicks);
    }

    @Info("Stops the perk for the given player and NBT data.")
    @HideFromJS
    public CompoundTag stop(Player player, CompoundTag nbt) {
        return this.stop.apply(player, nbt);
    }

    private static MutableComponent coerceComponent(Object value) {
        if (value == null) {
            return Component.empty();
        }

        if (value instanceof MutableComponent mutableComponent) {
            return mutableComponent.copy();
        }

        if (value instanceof Component component) {
            return component.copy();
        }

        return Component.literal(String.valueOf(value));
    }
}
