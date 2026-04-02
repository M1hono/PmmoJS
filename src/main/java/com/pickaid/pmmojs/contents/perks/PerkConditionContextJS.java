package com.pickaid.pmmojs.contents.perks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PerkConditionContextJS extends AbstractPerkContextJS {
    public PerkConditionContextJS(ResourceLocation perkId, Player player, PerkTagJS settings) {
        super(perkId, player, settings);
    }
}
