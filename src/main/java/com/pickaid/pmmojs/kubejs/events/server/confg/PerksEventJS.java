package com.pickaid.pmmojs.kubejs.events.server.confg;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.enums.EventType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class PerksEventJS extends EventJS {
    public static Map<EventType, List<CompoundTag>> customPerks = new HashMap<>();
    public static List<EventType> clearedPerks = new ArrayList<>();
    public static List<CompoundTag> removedPerks = new ArrayList<>();
    public static List<String> removedPerkTypes = new ArrayList<>();

    public PerksEventJS() {
        customPerks = new LinkedHashMap<>();
        clearedPerks = new ArrayList<>();
        removedPerks = new ArrayList<>();
        removedPerkTypes = new ArrayList<>();
        for (EventType eventType : EventType.values()) {
            customPerks.put(eventType, new ArrayList<>());
        }
    }

    @Info("""
            To add custom perks to the server.
            Returns a builder for adding custom perks to the server.
            
            @param eventType The type of perk to add.
            @param perk The name of the perk.
            @param skill The skill required to unlock this perk.
            """)
    public PerkBuilder addPerk(EventType eventType, String perk, String skill) {
        return new PerkBuilder(this, eventType, perk, skill);
    }

    @Info("""
            Not always used.
            """)
    void addPerkTag(EventType eventType, CompoundTag tag) {
        customPerks.get(eventType).add(tag);
    }

    @Info("""
            Only works for existing perks.
            clear all perks of a specific type.
            """)
    public void clearPerks(EventType eventType) {
        clearedPerks.add(eventType);
    }

    @Info("""
            Only works for existing perks.
            Removes all perks in specific types.
            """)
    public void removePerk(CompoundTag tag) {
        removedPerks.add(tag);
    }

    @Info("""
            Removes a perk by its type.
            @param perkType The type of perk to remove like "pmmo:fireworks
            """)
    public void removePerkType(String perkType) {
        removedPerkTypes.add(perkType);
    }

    public static class PerkBuilder {
        protected final PerksEventJS event;
        protected final EventType eventType;
        protected final CompoundTag tag = new CompoundTag();

        protected PerkBuilder(PerksEventJS event, EventType eventType, String perk, String skill) {
            this.event = event;
            this.eventType = eventType;
            this.tag.putString("perk", perk);
            this.tag.putString("skill", skill);
        }

        public PerksEventJS build() {
            event.addPerkTag(eventType, tag);
            return event;
        }

        // Factory methods for specialized builders
        @Info("""
                Creates an AttributePerk builder for attribute perks.
                """)
        public AttributePerkBuilder asAttributePerk() {
            return new AttributePerkBuilder(this);
        }

        @Info("""
                Creates a TempAttributePerk builder for temporary attribute perks.
                """)
        public TempAttributePerkBuilder asTempAttributePerk() {
            return new TempAttributePerkBuilder(this);
        }

        @Info("""
                Creates a DamageBoostPerk builder for damage boost perks.
                """)
        public DamageBoostPerkBuilder asDamageBoostPerk() {
            return new DamageBoostPerkBuilder(this);
        }

        @Info("""
                Creates a DamageReducePerk builder for damage reduction perks.
                """)
        public DamageReducePerkBuilder asDamageReducePerk() {
            return new DamageReducePerkBuilder(this);
        }

        @Info("""
                Creates a BreakSpeedPerk builder for break speed perks.
                """)
        public BreakSpeedPerkBuilder asBreakSpeedPerk() {
            return new BreakSpeedPerkBuilder(this);
        }

        @Info("""
                Creates an EffectPerk builder for potion effect perks.
                """)
        public EffectPerkBuilder asEffectPerk() {
            return new EffectPerkBuilder(this);
        }

        @Info("""
                Creates a JumpBoostPerk builder for jump boost perks.
                """)
        public JumpBoostPerkBuilder asJumpBoostPerk() {
            return new JumpBoostPerkBuilder(this);
        }

        @Info("""
                Creates a BreathPerk builder for underwater breathing perks.
                """)
        public BreathPerkBuilder asBreathPerk() {
            return new BreathPerkBuilder(this);
        }

        @Info("""
                Creates a VillagerBoostPerk builder for trading perks.
                """)
        public VillagerBoostPerkBuilder asVillagerBoostPerk() {
            return new VillagerBoostPerkBuilder(this);
        }

        @Info("""
                Creates a FireworksPerk builder for fireworks perks.
                """)
        public FireworksPerkBuilder asFireworksPerk() {
            return new FireworksPerkBuilder(this);
        }

        @Info("""
                Creates a TameBoostPerk builder for taming boost perks.
                """)
        public TameBoostPerkBuilder asTameBoostPerk() {
            return new TameBoostPerkBuilder(this);
        }

        @Info("""
                Creates a CommandPerk builder for command execution perks.
                """)
        public CommandPerkBuilder asCommandPerk() {
            return new CommandPerkBuilder(this);
        }

        @Info("""
                Adds entities to the "applies_to" list for entity predicate.
                """)
        public PerkBuilder addForEntities(String... entities) {
            ListTag list;
            if (tag.contains("applies_to", Tag.TAG_LIST)) {
                list = tag.getList("applies_to", Tag.TAG_STRING);
            } else {
                list = new ListTag();
            }
            for (String entity : entities) {
                list.add(StringTag.valueOf(entity));
            }
            tag.put("applies_to", list);
            return this;
        }

        @Info("""
                Adds damage types to the "for_damage" list for damage predicate.
                """)
        public PerkBuilder addForDamages(String... damageTypes) {
            ListTag list;
            if (tag.contains("for_damage", Tag.TAG_LIST)) {
                list = tag.getList("for_damage", Tag.TAG_STRING);
            } else {
                list = new ListTag();
            }
            for (String damageType : damageTypes) {
                list.add(StringTag.valueOf(damageType));
            }
            tag.put("for_damage", list);
            return this;
        }

        @Info("""
                Sets the cooldown for this perk.
                """)
        public PerkBuilder withCooldown(Number cooldown) {
            tag.putInt("cooldown", cooldown.intValue());
            return this;
        }

        @Info("""
                Sets the chance for this perk to activate (0.0-1.0).
                """)
        public PerkBuilder withChance(Number chance) {
            tag.putDouble("chance", chance.doubleValue());
            return this;
        }

        @Info("""
                Sets the minimum level required in the skill for this perk to activate.
                """)
        public PerkBuilder withMinLevel(Number minLevel) {
            tag.putInt("min_level", minLevel.intValue());
            return this;
        }

        @Info("""
                Sets the maximum level in the skill for this perk to activate.
                """)
        public PerkBuilder withMaxLevel(Number maxLevel) {
            tag.putInt("max_level", maxLevel.intValue());
            return this;
        }

        @Info("""
                Sets the perk to execute only if player level is divisible by this value.
                """)
        public PerkBuilder withPerXLevel(Number perXLevel) {
            tag.putInt("per_x_level", perXLevel.intValue());
            return this;
        }

        @Info("""
                Sets specific level milestones for this perk to execute.
                """)
        public PerkBuilder withMilestones(Number... milestones) {
            ListTag list = new ListTag();
            for (Number milestone : milestones) {
                list.add(DoubleTag.valueOf(milestone.doubleValue()));
            }
            tag.put("milestones", list);
            return this;
        }

        public PerkBuilder withString(@NonNull String key, @NonNull String value) {
            tag.putString(key, value);
            return this;
        }

        public PerkBuilder withBool(@NonNull String key, boolean value) {
            tag.putBoolean(key, value);
            return this;
        }

        public PerkBuilder withFloat(@NonNull String key, Number value) {
            tag.putFloat(key, value.floatValue());
            return this;
        }

        public PerkBuilder withInt(@NonNull String key, Number value) {
            tag.putInt(key, value.intValue());
            return this;
        }

        public PerkBuilder withDouble(@NonNull String key, Number value) {
            tag.putDouble(key, value.doubleValue());
            return this;
        }

        public PerkBuilder withLong(@NonNull String key, Number value) {
            tag.putLong(key, value.longValue());
            return this;
        }

        public PerkBuilder withList(@NonNull String key, @NonNull ListTag list) {
            tag.put(key, list);
            return this;
        }

        public PerkBuilder withList(@NonNull String key, Tag... tags) {
            ListTag list = new ListTag();
            list.addAll(Arrays.asList(tags));
            tag.put(key, list);
            return this;
        }

        public PerkBuilder withStringList(@NonNull String key, @NonNull String... values) {
            ListTag list = new ListTag();
            for (String value : values) {
                list.add(StringTag.valueOf(value));
            }
            tag.put(key, list);
            return this;
        }

        public PerkBuilder withCompound(@NonNull String key, @NonNull CompoundTag compound) {
            tag.put(key, compound);
            return this;
        }

        public static class BasePerkBuilder<T extends BasePerkBuilder<T>> extends PerkBuilder {
            protected BasePerkBuilder(PerkBuilder builder) {
                super(builder.event, builder.eventType, builder.tag.getString("perk"), builder.tag.getString("skill"));
                for (String key : builder.tag.getAllKeys()) {
                    this.tag.put(key, builder.tag.get(key).copy());
                }
            }

            @SuppressWarnings("unchecked")
            protected T self() {
                return (T) this;
            }

            public T withMaxBoost(double value) {
                tag.putDouble("max_boost", value);
                return self();
            }

            public T withPerLevel(double value) {
                tag.putDouble("per_level", value);
                return self();
            }

            public T withBase(double value) {
                tag.putDouble("base", value);
                return self();
            }

            @Override
            public T withCooldown(Number cooldown) {
                super.withCooldown(cooldown);
                return self();
            }

            @Override
            public T withChance(Number chance) {
                super.withChance(chance);
                return self();
            }

            @Override
            public T withMinLevel(Number minLevel) {
                super.withMinLevel(minLevel);
                return self();
            }

            @Override
            public T withMaxLevel(Number maxLevel) {
                super.withMaxLevel(maxLevel);
                return self();
            }

            @Override
            public T addForEntities(String... entities) {
                super.addForEntities(entities);
                return self();
            }

            @Override
            public T addForDamages(String... damageTypes) {
                super.addForDamages(damageTypes);
                return self();
            }

            @Override
            public T withPerXLevel(Number perXLevel) {
                super.withPerXLevel(perXLevel);
                return self();
            }

            @Override
            public T withMilestones(Number... milestones) {
                super.withMilestones(milestones);
                return self();
            }

            @Override
            public T withString(@NonNull String key, @NonNull String value) {
                super.withString(key, value);
                return self();
            }

            @Override
            public T withBool(@NonNull String key, boolean value) {
                super.withBool(key, value);
                return self();
            }

            @Override
            public T withFloat(@NonNull String key, Number value) {
                super.withFloat(key, value);
                return self();
            }

            @Override
            public T withInt(@NonNull String key, Number value) {
                super.withInt(key, value);
                return self();
            }

            @Override
            public T withDouble(@NonNull String key, Number value) {
                super.withDouble(key, value);
                return self();
            }

            @Override
            public T withLong(@NonNull String key, Number value) {
                super.withLong(key, value);
                return self();
            }
        }

        public static class BreakSpeedPerkBuilder extends BasePerkBuilder<BreakSpeedPerkBuilder> {
            protected BreakSpeedPerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the pickaxe dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withPickaxeDig(double value) {
                tag.putDouble("pickaxe_dig", value);
                return this;
            }

            @Info("""
                    Sets the axe dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withAxeDig(double value) {
                tag.putDouble("axe_dig", value);
                return this;
            }

            @Info("""
                    Sets the shovel dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withShovelDig(double value) {
                tag.putDouble("shovel_dig", value);
                return this;
            }

            @Info("""
                    Sets the hoe dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withHoeDig(double value) {
                tag.putDouble("hoe_dig", value);
                return this;
            }

            @Info("""
                    Sets the shears dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withShearsDig(double value) {
                tag.putDouble("shears_dig", value);
                return this;
            }

            @Info("""
                    Sets the sword dig speed bonus.
                    """)
            public BreakSpeedPerkBuilder withSwordDig(double value) {
                tag.putDouble("sword_dig", value);
                return this;
            }
        }

        public static class FireworksPerkBuilder extends BasePerkBuilder<FireworksPerkBuilder> {
            protected FireworksPerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the colors for the fireworks.
                    """)
            public FireworksPerkBuilder withColors(int... colors) {
                ListTag list = new ListTag();
                for (int color : colors) {
                    list.add(StringTag.valueOf(String.valueOf(color)));
                }
                tag.put("colors", list);
                return this;
            }
        }

        public static class AttributePerkBuilder extends BasePerkBuilder<AttributePerkBuilder> {
            protected AttributePerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the attribute for this attribute perk.
                    """)
            public AttributePerkBuilder withAttribute(Attribute attribute) {
                tag.putString("attribute", ForgeRegistries.ATTRIBUTES.getKey(attribute).toString());
                return this;
            }

            @Info("""
                    Sets whether the attribute should be applied multiplicatively.
                    """)
            public AttributePerkBuilder withMultiplicative(boolean value) {
                tag.putBoolean("multiplicative", value);
                return this;
            }
        }

        public static class TempAttributePerkBuilder extends AttributePerkBuilder {
            protected TempAttributePerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the duration of the temporary attribute.
                    """)
            public TempAttributePerkBuilder withDuration(int duration) {
                tag.putInt("duration", duration);
                return this;
            }
        }

        public static class JumpBoostPerkBuilder extends BasePerkBuilder<JumpBoostPerkBuilder> {
            protected JumpBoostPerkBuilder(PerkBuilder builder) {
                super(builder);
            }
        }

        public static class BreathPerkBuilder extends BasePerkBuilder<BreathPerkBuilder> {
            protected BreathPerkBuilder(PerkBuilder builder) {
                super(builder);
            }
        }

        public static class DamageReducePerkBuilder extends BasePerkBuilder<DamageReducePerkBuilder> {
            protected DamageReducePerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets damage types to reduce.
                    """)
            public DamageReducePerkBuilder forDamageTypes(String... damageTypes) {
                return addForDamages(damageTypes);
            }
        }

        public static class DamageBoostPerkBuilder extends BasePerkBuilder<DamageBoostPerkBuilder> {
            protected DamageBoostPerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets weapon types this damage boost applies to.
                    """)
            public DamageBoostPerkBuilder forWeapons(String... weapons) {
                return addForEntities(weapons);
            }

            @Info("""
                    Sets whether the damage boost should be applied multiplicatively.
                    """)
            public DamageBoostPerkBuilder withMultiplicative(boolean value) {
                tag.putBoolean("multiplicative", value);
                return this;
            }
        }

        public static class EffectPerkBuilder extends BasePerkBuilder<EffectPerkBuilder> {
            protected EffectPerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the effect for this perk.
                    """)
            public EffectPerkBuilder withEffect(String effectId) {
                tag.putString("effect", effectId);
                return this;
            }

            @Info("""
                    Sets the duration of the effect.
                    """)
            public EffectPerkBuilder withDuration(int duration) {
                tag.putInt("duration", duration);
                return this;
            }

            @Info("""
                    Sets the amplifier for this effect.
                    """)
            public EffectPerkBuilder withModifier(int modifier) {
                tag.putInt("modifier", modifier);
                return this;
            }

            @Info("""
                    Sets whether the effect's particles should be hidden.
                    """)
            public EffectPerkBuilder withAmbient(boolean ambient) {
                tag.putBoolean("ambient", ambient);
                return this;
            }

            @Info("""
                    Sets whether the effect should be visible in the player's inventory and HUD.
                    """)
            public EffectPerkBuilder withVisible(boolean visible) {
                tag.putBoolean("visible", visible);
                return this;
            }

            @Info("""
                    Sets whether the effect's icon should be shown in the HUD and inventory.
                    """)
            public EffectPerkBuilder withShowIcon(boolean showIcon) {
                tag.putBoolean("show_icon", showIcon);
                return this;
            }
        }

        public static class CommandPerkBuilder extends BasePerkBuilder<CommandPerkBuilder> {
            protected CommandPerkBuilder(PerkBuilder builder) {
                super(builder);
            }

            @Info("""
                    Sets the command to execute.
                    """)
            public CommandPerkBuilder withCommand(String command) {
                tag.putString("command", command);
                return this;
            }

            @Info("""
                    Sets the function to execute.
                    """)
            public CommandPerkBuilder withFunction(String function) {
                tag.putString("function", function);
                return this;
            }
        }

        public static class VillagerBoostPerkBuilder extends BasePerkBuilder<VillagerBoostPerkBuilder> {
            protected VillagerBoostPerkBuilder(PerkBuilder builder) {
                super(builder);
            }
        }

        public static class TameBoostPerkBuilder extends BasePerkBuilder<TameBoostPerkBuilder> {
            protected TameBoostPerkBuilder(PerkBuilder builder) {
                super(builder);
            }
        }
    }
}
