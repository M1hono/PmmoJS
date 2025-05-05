package com.pickaid.pmmojs.kubejs.events.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.contents.settings.*;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.UtilsJS;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Info("Event for registering PMMO settings through KubeJS")
public class PMMOSettingEventJS extends EventJS {

    @Info("Creates a new item settings builder for the specified object ID")
    public ItemSettingsBuilder item(Item object) {
        ResourceLocation resLoc = object.kjs$getIdLocation();
        return new ItemSettingsBuilder(ObjectType.ITEM, resLoc);
    }

    @Info("Creates a new block settings builder for the specified object ID")
    public BlockSettingsBuilder block(Block object) {
        ResourceLocation resLoc = object.kjs$getIdLocation();
        return new BlockSettingsBuilder(ObjectType.BLOCK, resLoc);
    }

    @Info("Creates a new entity settings builder for the specified object ID")
    public EntitySettingsBuilder entity(EntityType<?> object) {
        ResourceLocation resLoc = ForgeRegistries.ENTITY_TYPES.getKey(object);
        return new EntitySettingsBuilder(ObjectType.ENTITY, resLoc);
    }

    @Info("Creates a new biome settings builder for the specified object ID")
    public LocationSettingsBuilder biome(String object) {
        ResourceLocation resLoc = ResourceLocation.tryParse(object);
        return new LocationSettingsBuilder(ObjectType.BIOME, resLoc);
    }

    @Info("Creates a new dimension settings builder for the specified object ID")
    public LocationSettingsBuilder dimension(String object) {
        ResourceLocation resLoc = ResourceLocation.tryParse(object);
        return new LocationSettingsBuilder(ObjectType.DIMENSION, resLoc);
    }

    @Info("""
        Creates a new player settings builder
        Although it's not recommended to use generic Settings,
    """)
    public GenericSettingsBuilder generic(ObjectType type, String objectId) {
        ResourceLocation resLoc = UtilsJS.getMCID(ScriptType.SERVER.manager.get().context, KubeJS.appendModId(objectId));
        return new GenericSettingsBuilder(type, resLoc);
    }

    @Info("""
            Allow to set custom requirements for specific objects.
            Just for easier data management.
            """)
    public void customReq(String object, Map<String, ? extends Number> map) {
        for (Map.Entry<String, ? extends Number> entry : map.entrySet()) {
            var newMap = new HashMap<>(PmmoJS.REQ_MAP.getOrDefault(object, new HashMap<>()));
            newMap.put(entry.getKey(), entry.getValue().intValue());
            PmmoJS.REQ_MAP.put(object, newMap);
        }
    }

    @Info("""
            Instead of setting, it's actually overide the vanilla settings.
            Useful when you want to reset vanilla settings.
            """)
    public void clearVanillaItemSettings() {
        for (ObjectType type : ObjectType.values()) {
            for (ReqType reqType : ReqType.values()) {
                BuiltInRegistries.ITEM.forEach(item -> {
                    APIUtils.registerRequirement(type, item.kjs$getIdLocation(), reqType, Map.of("combat", 0), true);
                });
            }
        }
    }

    public void clearVanillaBlockSettings() {
        for (ObjectType type : ObjectType.values()) {
            for (ReqType reqType : ReqType.values()) {
                BuiltInRegistries.BLOCK.forEach(block -> {
                    APIUtils.registerRequirement(type, block.kjs$getIdLocation(), reqType, Map.of("combat", 0), true);
                });
            }
        }
    }

    public void clearVanillaEntitySettings() {
        for (ObjectType type : ObjectType.values()) {
            for (ReqType reqType : ReqType.values()) {
                BuiltInRegistries.ENTITY_TYPE.forEach(entity -> {
                    APIUtils.registerRequirement(type, ForgeRegistries.ENTITY_TYPES.getKey(entity), reqType, Map.of("combat", 0), true);
                });
            }
        }
    }
}