package com.pickaid.pmmojs.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ItemStackDamagePenaltyEvent extends Event {
    private float damage;
    private Player player;
    private ItemStack mainHandStack;

    public ItemStackDamagePenaltyEvent(float damage, Player player, ItemStack mainHandStack) {
        this.damage = damage;
        this.player = player;
        this.mainHandStack = mainHandStack;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getMainHandStack() {
        return mainHandStack;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
