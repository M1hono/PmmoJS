package com.pickaid.pmmojs.kubejs.events.server.penalty;

import com.pickaid.pmmojs.api.events.ItemStackDamagePenaltyEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.codecs.DataSource;
import harmonised.pmmo.core.Core;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ItemStackDamagePenaltyEventJS extends PlayerEventJS {
    ItemStackDamagePenaltyEvent event;

    public ItemStackDamagePenaltyEventJS(ItemStackDamagePenaltyEvent event) {
        this.event = event;
    }

    public ItemStack getItemStack() {
        return this.event.getMainHandStack();
    }

    public float getDamage() {
        return this.event.getDamage();
    }

    public void setDamage(float damage) {
        this.event.setDamage(damage);
    }

    public DataSource<?> getData() {
        return Core.get(getPlayer().level()).getLoader().getLoader(ObjectType.ITEM).getData(getItemStack().kjs$getIdLocation());
    }

    public Map<String, Integer> getReqData(Boolean ignoreEnchantment) {
        return Core.get(getPlayer().level()).getReqMap(ReqType.KILL, getItemStack() ,ignoreEnchantment);
    }

    @Override
    public Player getEntity() {
        return this.event.getPlayer();
    }
}
