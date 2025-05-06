package com.pickaid.pmmojs.kubejs.events.server;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.events.XpEvent;
import harmonised.pmmo.core.Core;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class XPEventJS extends PlayerEventJS {
    XpEvent event;

    public XPEventJS(XpEvent event) {
        this.event = event;
    }

    @HideFromJS
    public XpEvent getEvent() {
        return event;
    }

    @Info("the level when gained or lost experience")
    public int startLevel() {
        return this.event.startLevel();
    }

    @Info("the level after gained or lost experience")
    public int endLevel() {
        return this.event.endLevel();
    }

    @Info("whether player leveled up")
    public boolean isLevelUp() {
        return this.startLevel() < this.endLevel();
    }

    public CompoundTag getContext() {
        return this.event.getContext();
    }

    @Override
    public Player getEntity() {
        return this.event.getEntity();
    }

    @Info("the skill gained or lost experience in")
    public String getSkill () {
        return this.event.skill;
    }
}
