package com.pickaid.pmmojs.kubejs;

import com.pickaid.pmmojs.kubejs.events.startup.PerksRegistryEventJS;
import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalType;
import com.pickaid.pmmojs.utils.ClientSkillHelper;
import com.pickaid.pmmojs.utils.CustomReqHelper;
import com.pickaid.pmmojs.utils.NbtPathBuilder;
import com.pickaid.pmmojs.utils.PmmoHelper;
import com.pickaid.pmmojs.utils.SkillHelper;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.util.TagBuilder;
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
        ScriptType type = event.getType();

        if (type.isStartup()) {
            event.add("TagBuilder", TagBuilder.class);
            event.add("NbtPathBuilder", NbtPathBuilder.class);
            event.add("PmmoHelper", PmmoHelper.class);
        } else if (type == ScriptType.SERVER) {
            event.add("PmmoHelper", PmmoHelper.class);
            event.add("CustomReqMap", CustomReqHelper.class);
            event.add("SkillHelper", SkillHelper.class);
            event.add("SKillHelper", SkillHelper.class);
            event.add("TagBuilder", TagBuilder.class);
            event.add("NbtPathBuilder", NbtPathBuilder.class);
            event.add("SalvageBuilder", APIUtils.SalvageBuilder.class);
        } else if (type.isClient()) {
            event.add("SkillHelper", ClientSkillHelper.class);
            event.add("SKillHelper", ClientSkillHelper.class);
        }

        // Common used Enums.
        event.add("EventType", EventType.class);
        event.add("PMMOInternalType", PMMOInternalType.class);
        event.add("PMMOPerkSide", PerksRegistryEventJS.Side.class);
        event.add("ModifierDataType", ModifierDataType.class);
        event.add("ObjectType", ObjectType.class);
        event.add("ReqType", ReqType.class);
    }
}
