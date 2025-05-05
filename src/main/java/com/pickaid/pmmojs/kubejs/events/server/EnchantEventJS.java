package com.pickaid.pmmojs.kubejs.events.server;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.events.EnchantEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantEventJS extends PlayerEventJS {
    EnchantEvent event;

    public EnchantEventJS(EnchantEvent event) {
        this.event = event;
    }

    @HideFromJS
    public EnchantEvent getEvent() {
        return event;
    }

    @Override
    public Player getEntity() {
        return this.event.getEntity();
    }

    public EnchantmentInstance getEnchantmentInstance() {
        return this.event.getEnchantment();
    }

    public ItemStack getStack() {
        return this.event.getItem();
    }
}
