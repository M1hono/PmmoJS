package com.pickaid.pmmojs.contents.perks;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public abstract class AbstractPerkContextJS {
    private final ResourceLocation perkId;
    private final Player player;
    private final PerkTagJS settings;

    protected AbstractPerkContextJS(ResourceLocation perkId, Player player, PerkTagJS settings) {
        this.perkId = perkId;
        this.player = player;
        this.settings = settings;
    }

    @Info("Get the registered perk id.")
    public String getPerkId() {
        return perkId.toString();
    }

    @Info("Get the registered perk id as a ResourceLocation.")
    public ResourceLocation getPerkIdLocation() {
        return perkId;
    }

    @Info("Get the player executing this perk callback.")
    public Player getPlayer() {
        return player;
    }

    @Info("Get the server player when available.")
    public ServerPlayer getServerPlayer() {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    @Info("Get the merged perk settings/source tag for this callback.")
    public PerkTagJS getSettings() {
        return settings;
    }
}
