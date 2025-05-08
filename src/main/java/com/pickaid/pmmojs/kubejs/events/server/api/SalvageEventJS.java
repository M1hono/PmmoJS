package com.pickaid.pmmojs.kubejs.events.server.api;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.events.SalvageEvent;
import harmonised.pmmo.config.codecs.CodecTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SalvageEventJS extends PlayerEventJS {
    SalvageEvent event;

    public SalvageEventJS(SalvageEvent event) {
        this.event = event;
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
    public APIUtils.SalvageBuilder getBuilder() {
        return this.event.getBuilder();
    }
}
