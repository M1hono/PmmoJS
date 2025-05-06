package com.pickaid.pmmojs.kubejs.events.server.penalty;

import com.pickaid.pmmojs.api.events.EntityDamagePenaltyEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.config.codecs.DataSource;
import harmonised.pmmo.core.Core;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class EntityDamagePenaltyEventJS extends PlayerEventJS {
    EntityDamagePenaltyEvent event;

    public EntityDamagePenaltyEventJS(EntityDamagePenaltyEvent event) {
        this.event = event;
    }

    public Entity getTarget() {
        return this.event.getTarget();
    }

    public float getDamage() {
        return this.event.getDamage();
    }

    public void setDamage(float damage) {
        this.event.setDamage(damage);
    }

    public DataSource<?> getData() {
        return Core.get(getPlayer().level()).getLoader().getLoader(ObjectType.ENTITY).getData(ForgeRegistries.ENTITY_TYPES.getKey(getEntity().getType()));
    }

    public Map<String, Integer> getReqData() {
        return Core.get(getPlayer().level()).getReqMap(ReqType.KILL, getEntity());
    }

    @Override
    public Player getEntity() {
        return this.event.getPlayer();
    }
}
