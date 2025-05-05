package com.pickaid.pmmojs.kubejs.events.server;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.events.SalvageEvent;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.features.party.PartyUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//    public void setSalvageKey(ResourceLocation key) {
//        this.event.salvage = Map.entry(key, salvage.getValue());
//    }
//
//    public void setOutputStack(ItemStack itemStack) {
//        ServerPlayer player = (ServerPlayer) this.event.getEntity();
//        player.drop(itemStack, false, true);
//        Map<String, Long> xpAwards= new HashMap<>();
//        for (Map.Entry<String, Long> award : salvage.getValue().xpAward().entrySet()) {
//            xpAwards.merge(award.getKey(), award.getValue(), Long::sum);
//        }
//        List<ServerPlayer> party = PartyUtils.getPartyMembersInRange(player);
//        Core.get(player.level()).awardXP(party, xpAwards);
//        this.setCanceled(true);
//    }
}
