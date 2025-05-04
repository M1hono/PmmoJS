package com.pickaid.pmmojs.kubejs.events.startup;

import com.pickaid.pmmojs.contents.PerkRegistryFactory;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.perks.Perk;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static com.pickaid.pmmojs.kubejs.PMMOKubeJSPlugin.PERKS;

public class PerksRegistryEventJS extends StartupEventJS {

    @Info("To set the ID for your perk.")
    public PerkRegistryFactory create(ResourceLocation perkId, Side side) {
        return new PerkRegistryFactory(perkId, this, side);
    }

    @HideFromJS
    public void registerPerk(ResourceLocation perkId, Perk perk, Side side) {
        if (!PERKS.containsKey(perkId)) {
            PERKS.put(perkId, new PerkRegistryObject(perk, side));
        }
    }

    public enum Side {
        SERVER, CLIENT, BOTH
    }

    public record PerkRegistryObject(Perk perk, Side side) {}
}