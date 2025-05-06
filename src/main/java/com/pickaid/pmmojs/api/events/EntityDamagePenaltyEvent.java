package com.pickaid.pmmojs.api.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class EntityDamagePenaltyEvent extends Event {
    private float damage;
    private Player player;
    private Entity target;

    public EntityDamagePenaltyEvent(float damage, Player player, Entity target) {
        this.damage = damage;
        this.player = player;
        this.target = target;
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

    public Entity getTarget() {
        return target;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
