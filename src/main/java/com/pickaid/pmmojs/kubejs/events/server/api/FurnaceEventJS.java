package com.pickaid.pmmojs.kubejs.events.server.api;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.events.FurnaceBurnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FurnaceEventJS extends EventJS {
    FurnaceBurnEvent event;

    public FurnaceEventJS(FurnaceBurnEvent event) {
        this.event = event;
    }

    @HideFromJS
    public FurnaceBurnEvent getEvent() {
        return event;
    }

    @Info("Input item stack in the furnace")
    public ItemStack getInput() {
        return this.event.getInput();
    }

    @Info("Output item stack in the furnace")
    public Level getLevel() {
        return this.event.getLevel();
    }

    @Info("Position of the furnace")
    public BlockPos getPos() {
        return this.event.getPos();
    }
}
