package com.pickaid.pmmojs.kubejs.events.server.api;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.events.SalvageEvent;
import harmonised.pmmo.config.codecs.CodecTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SalvageEventJS extends PlayerEventJS {
    SalvageEvent event;
    WrappedBuilder builder;

    public SalvageEventJS(SalvageEvent event) {
        this.event = event;
        CodecTypes.SalvageData salvage = event.getSalvage();
        this.builder = WrappedBuilder.start()
                .setChancePerLevel(new HashMap<>(salvage.chancePerLevel()))
                .setLevelReq(new HashMap<>(salvage.levelReq()))
                .setXpAward(new HashMap<>(salvage.xpAward()))
                .setSalvageMax(salvage.salvageMax())
                .setBaseChance(salvage.baseChance())
                .setMaxChance(salvage.maxChance());
    }

    @HideFromJS
    public SalvageEvent getEvent() {
        return event;
    }

    @Override
    public Player getEntity() {
        return this.event.getEntity();
    }

    @Info("""
            Return the input stack.
            Get the mainhand item that the player salvages.
            """)
    public ItemStack getInputStack() {
        return this.event.getInputStack();
    }

    @Info("""
            get the input stack.
            """)
    public ItemStack getOutputStack() {
        return this.event.getOutputStack();
    }

    @Info("""
            Set the output stack.
            """)
    public void setOutputStack(ItemStack stack) {
        this.event.setOutputStack(stack);
    }

    @Info("""
            Return the Salvage data.
            """)
    public CodecTypes.SalvageData getSalvage() {
        return this.event.getSalvage();
    }

    @Info("""
            Return the Salvage Builder for modifying the original Salvage data.
            """)
    public WrappedBuilder getBuilder() {
        return builder;
    }

    public static class WrappedBuilder {
        private Map<String, Double> chancePerLevel = new HashMap();
        private Map<String, Integer> levelReq = new HashMap();
        private Map<String, Long> xpAward = new HashMap();
        private int salvageMax = 1;
        private double baseChance = 0.0;
        private double maxChance = 1.0;

        private WrappedBuilder() {
        }

        public static WrappedBuilder start() {
            return new WrappedBuilder();
        }

        public WrappedBuilder setChancePerLevel(Map<String, Number> chancePerLevel) {
            for (Map.Entry<String, Number> entry : chancePerLevel.entrySet()) {
                this.chancePerLevel.put(entry.getKey(), entry.getValue().doubleValue());
            }
            return this;
        }

        public WrappedBuilder setLevelReq(Map<String, Number> levelReq) {
            for (Map.Entry<String, Number> entry : levelReq.entrySet()) {
                this.levelReq.put(entry.getKey(), entry.getValue().intValue());
            }
            return this;
        }

        public WrappedBuilder setXpAward(Map<String, Number> xpAward) {
            for (Map.Entry<String, Number> entry : xpAward.entrySet()) {
                this.xpAward.put(entry.getKey(), entry.getValue().longValue());
            }
            return this;
        }

        public WrappedBuilder setSalvageMax(Number max) {
            this.salvageMax = max.intValue();
            return this;
        }

        public WrappedBuilder setBaseChance(Number chance) {
            this.baseChance = chance.doubleValue();
            return this;
        }

        public WrappedBuilder setMaxChance(Number chance) {
            this.maxChance = chance.doubleValue();
            return this;
        }

        public CodecTypes.SalvageData build() {
            return new CodecTypes.SalvageData(this.chancePerLevel, this.levelReq, this.xpAward, this.salvageMax, this.baseChance, this.maxChance);
        }
    }
}
