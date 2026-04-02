package com.pickaid.pmmojs.kubejs.events.startup;

import com.pickaid.pmmojs.contents.PerkRegistryFactory;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import harmonised.pmmo.api.perks.Perk;
import net.minecraft.resources.ResourceLocation;

import static com.pickaid.pmmojs.kubejs.PMMOKubeJSPlugin.PERKS;

public class PerksRegistryEventJS extends StartupEventJS {

    @Info("Create a new startup perk registration builder. Accepts a perk id string or ResourceLocation. Bare ids default to the kubejs namespace.")
    public PerkRegistryFactory create(Object perkId, Side side) {
        return new PerkRegistryFactory(coercePerkId(perkId), this, side);
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

    @HideFromJS
    private static ResourceLocation coercePerkId(Object perkId) {
        Object value = Wrapper.unwrapped(perkId);
        if (value instanceof ResourceLocation resourceLocation) {
            return resourceLocation;
        }

        if (value instanceof CharSequence charSequence) {
            String raw = charSequence.toString().trim();
            if (raw.isEmpty()) {
                throw new IllegalArgumentException("Perk id cannot be empty");
            }

            String normalized = raw.contains(":") ? raw : "kubejs:" + raw;
            ResourceLocation parsed = ResourceLocation.tryParse(normalized);
            if (parsed == null) {
                throw new IllegalArgumentException("Invalid perk id: " + raw);
            }

            return parsed;
        }

        throw new IllegalArgumentException("Unsupported perk id: " + value);
    }
}
