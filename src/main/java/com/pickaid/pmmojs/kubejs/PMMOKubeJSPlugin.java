package com.pickaid.pmmojs.kubejs;

import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.utils.CustomReqHelper;
import com.pickaid.pmmojs.utils.SkillHelper;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PMMOKubeJSPlugin extends KubeJSPlugin {
    public static final Map<ResourceLocation, PerksRegistryEventJS.PerkRegistryObject> PERKS = new HashMap<>();

    @Override
    public void registerEvents() {
        PMMOKubeJSEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("CustomReqMap", CustomReqHelper.class);
        event.add("SKillHelper", SkillHelper.class);
    }
}

