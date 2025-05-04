package com.pickaid.pmmojs.contents;

import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import dev.latvian.mods.kubejs.typings.Info;
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

    @Info("Sets the conditions for activating the perk.")
    public PerkRegistryFactory conditions(BiPredicate<Player, CompoundTag> conditions) {
        this.conditions = conditions;
        return this;
    }

    @Info("Sets the default properties for the perk.")
    public PerkRegistryFactory propertyDefaults(CompoundTag defaults) {
        this.propertyDefaults = defaults;
        return this;
    }

    @Info("Sets the start function for the perk.")
    public PerkRegistryFactory start(BiFunction<Player, CompoundTag, CompoundTag> start) {
        this.start = start;
        return this;
    }

    @Info("Sets the tick function for the perk.")
    public PerkRegistryFactory tick(TriFunction<Player, CompoundTag, Integer, CompoundTag> tick) {
        this.tick = tick;
        return this;
    }

    @Info("Sets the stop function for the perk.")
    public PerkRegistryFactory stop(BiFunction<Player, CompoundTag, CompoundTag> stop) {
        this.stop = stop;
        return this;
    }

    @Info("Sets the description for the perk.")
    public PerkRegistryFactory description(MutableComponent description) {
        this.description = description;
        return this;
    }

    @Info("Sets the status function for the perk.")
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
    public CompoundTag start(Player player, CompoundTag nbt) {
        return this.start.apply(player, nbt);
    }

    @Info("Ticks the perk for the given player, NBT data, and elapsed ticks.")
    public CompoundTag tick(Player player, CompoundTag nbt, int elapsedTicks) {
        return this.tick.apply(player, nbt, elapsedTicks);
    }

    @Info("Stops the perk for the given player and NBT data.")
    public CompoundTag stop(Player player, CompoundTag nbt) {
        return this.stop.apply(player, nbt);
    }
}