package com.pickaid.pmmojs.contents.perks;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public class PerkTickContextJS extends AbstractPerkContextJS {
    private final int elapsedTicks;

    public PerkTickContextJS(ResourceLocation perkId, Player player, PerkTagJS settings, int elapsedTicks) {
        super(perkId, player, settings);
        this.elapsedTicks = elapsedTicks;
    }

    @Info("Get the number of perk ticks that have already elapsed.")
    public int getElapsedTicks() {
        return elapsedTicks;
    }
}
