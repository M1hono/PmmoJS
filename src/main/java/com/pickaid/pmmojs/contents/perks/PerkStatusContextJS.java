package com.pickaid.pmmojs.contents.perks;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PerkStatusContextJS extends AbstractPerkContextJS {
    private final List<MutableComponent> lines = new ArrayList<>();

    public PerkStatusContextJS(ResourceLocation perkId, Player player, PerkTagJS settings) {
        super(perkId, player, settings);
    }

    @Info("Add one status line for this perk.")
    public void addLine(Object line) {
        lines.add(coerceComponent(line));
    }

    @Info("Add one line or a JS array/list of lines for this perk.")
    public void addLines(Object values) {
        for (Object value : ListJS.orSelf(Wrapper.unwrapped(values))) {
            addLine(value);
        }
    }

    @Info("Clear all collected status lines.")
    public void clearLines() {
        lines.clear();
    }

    @Info("Get the currently collected status lines.")
    public List<MutableComponent> getLines() {
        return List.copyOf(lines);
    }

    @HideFromJS
    public List<MutableComponent> rawLines() {
        return lines;
    }

    private static MutableComponent coerceComponent(Object value) {
        if (value == null) {
            return Component.empty();
        }

        if (value instanceof MutableComponent mutableComponent) {
            return mutableComponent.copy();
        }

        if (value instanceof Component component) {
            return component.copy();
        }

        return Component.literal(String.valueOf(value));
    }
}
