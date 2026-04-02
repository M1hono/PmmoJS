package com.pickaid.pmmojs.contents.perks;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public class PerkStartContextJS extends AbstractPerkContextJS {
    private final PerkTagJS result;

    public PerkStartContextJS(ResourceLocation perkId, Player player, PerkTagJS settings, PerkTagJS result) {
        super(perkId, player, settings);
        this.result = result;
    }

    @Info("Get the start-result tag that PMMO will merge back into the current perk execution.")
    public PerkTagJS getResult() {
        return result;
    }

    @HideFromJS
    public CompoundTag rawResult() {
        return result.raw();
    }
}
