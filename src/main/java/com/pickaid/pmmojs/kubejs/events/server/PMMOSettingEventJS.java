package com.pickaid.pmmojs.kubejs.events.server;

import com.pickaid.pmmojs.PmmoJS;
import com.pickaid.pmmojs.config.PmmoDefaultSettingsDisabler;
import com.pickaid.pmmojs.contents.settings.*;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Info("Event for registering PMMO settings through KubeJS")
public class PMMOSettingEventJS extends EventJS {

    @Info("Creates a new item settings builder. Accepts an item, item stack, resource location, or item id string.")
    public ItemSettingsBuilder item(Object object) {
        return new ItemSettingsBuilder(ObjectType.ITEM, coerceItemId(object));
    }

    @Info("Applies the same item settings callback to one target or a JS array/list of item targets.")
    public void items(Object objects, Consumer<ItemSettingsBuilder> consumer) {
        applyToTargets(objects, this::item, consumer);
    }

    @Info("Creates a new block settings builder. Accepts a block, block state, resource location, or block id string.")
    public BlockSettingsBuilder block(Object object) {
        return new BlockSettingsBuilder(ObjectType.BLOCK, coerceBlockId(object));
    }

    @Info("Applies the same block settings callback to one target or a JS array/list of block targets.")
    public void blocks(Object objects, Consumer<BlockSettingsBuilder> consumer) {
        applyToTargets(objects, this::block, consumer);
    }

    @Info("Creates a new entity settings builder. Accepts an entity type, resource location, or entity id string.")
    public EntitySettingsBuilder entity(Object object) {
        return new EntitySettingsBuilder(ObjectType.ENTITY, coerceEntityId(object));
    }

    @Info("Applies the same entity settings callback to one target or a JS array/list of entity targets.")
    public void entities(Object objects, Consumer<EntitySettingsBuilder> consumer) {
        applyToTargets(objects, this::entity, consumer);
    }

    @Info("Creates a new biome settings builder for the specified biome id.")
    public LocationSettingsBuilder biome(Object object) {
        return new LocationSettingsBuilder(ObjectType.BIOME, coerceLooseId(object, "biome"));
    }

    @Info("Applies the same biome settings callback to one target or a JS array/list of biome ids.")
    public void biomes(Object objects, Consumer<LocationSettingsBuilder> consumer) {
        applyToTargets(objects, this::biome, consumer);
    }

    @Info("Creates a new dimension settings builder for the specified dimension id.")
    public LocationSettingsBuilder dimension(Object object) {
        return new LocationSettingsBuilder(ObjectType.DIMENSION, coerceLooseId(object, "dimension"));
    }

    @Info("Applies the same dimension settings callback to one target or a JS array/list of dimension ids.")
    public void dimensions(Object objects, Consumer<LocationSettingsBuilder> consumer) {
        applyToTargets(objects, this::dimension, consumer);
    }

    @Info("""
        Creates a new player settings builder
        Although it's not recommended to use generic Settings,
    """)
    public GenericSettingsBuilder generic(ObjectType type, Object objectId) {
        return new GenericSettingsBuilder(type, coerceAnyId(objectId, "generic object"));
    }

    @Info("Applies the same generic settings callback to one target or a JS array/list of targets.")
    public void genericMany(ObjectType type, Object objectIds, Consumer<GenericSettingsBuilder> consumer) {
        applyToTargets(objectIds, target -> generic(type, target), consumer);
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
        PmmoDefaultSettingsDisabler.resetRequirements(ObjectType.ITEM);
    }

    public void clearVanillaBlockSettings() {
        PmmoDefaultSettingsDisabler.resetRequirements(ObjectType.BLOCK);
    }

    public void clearVanillaEntitySettings() {
        PmmoDefaultSettingsDisabler.resetRequirements(ObjectType.ENTITY);
    }

    private <B> void applyToTargets(Object objects, Function<Object, B> factory, Consumer<B> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Settings consumer cannot be null");
        }

        List<?> targets = ListJS.orSelf(Wrapper.unwrapped(objects));
        for (Object target : targets) {
            consumer.accept(factory.apply(target));
        }
    }

    private static ResourceLocation coerceItemId(Object object) {
        Object value = unwrap(object);
        if (value instanceof Item item) {
            return requireRegistryId(ForgeRegistries.ITEMS, item.kjs$getIdLocation(), "item");
        }

        if (value instanceof ItemStack stack) {
            return requireRegistryId(ForgeRegistries.ITEMS, stack.getItem().kjs$getIdLocation(), "item");
        }

        return requireRegistryId(ForgeRegistries.ITEMS, coerceAnyId(value, "item"), "item");
    }

    private static ResourceLocation coerceBlockId(Object object) {
        Object value = unwrap(object);
        if (value instanceof Block block) {
            return requireRegistryId(ForgeRegistries.BLOCKS, block.kjs$getIdLocation(), "block");
        }

        if (value instanceof BlockState blockState) {
            return requireRegistryId(ForgeRegistries.BLOCKS, blockState.getBlock().kjs$getIdLocation(), "block");
        }

        return requireRegistryId(ForgeRegistries.BLOCKS, coerceAnyId(value, "block"), "block");
    }

    private static ResourceLocation coerceEntityId(Object object) {
        Object value = unwrap(object);
        if (value instanceof EntityType<?> entityType) {
            return requireRegistryId(ForgeRegistries.ENTITY_TYPES, ForgeRegistries.ENTITY_TYPES.getKey(entityType), "entity");
        }

        return requireRegistryId(ForgeRegistries.ENTITY_TYPES, coerceAnyId(value, "entity"), "entity");
    }

    private static ResourceLocation coerceLooseId(Object object, String label) {
        return coerceAnyId(object, label);
    }

    private static ResourceLocation coerceAnyId(Object object, String label) {
        Object value = unwrap(object);
        if (value instanceof ResourceLocation resourceLocation) {
            return resourceLocation;
        }

        if (value instanceof Item item) {
            return item.kjs$getIdLocation();
        }

        if (value instanceof ItemStack stack) {
            return stack.getItem().kjs$getIdLocation();
        }

        if (value instanceof Block block) {
            return block.kjs$getIdLocation();
        }

        if (value instanceof BlockState blockState) {
            return blockState.getBlock().kjs$getIdLocation();
        }

        if (value instanceof EntityType<?> entityType) {
            return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        }

        if (value instanceof CharSequence) {
            return UtilsJS.getMCID(ScriptType.SERVER.manager.get().context, KubeJS.appendModId(value.toString()));
        }

        throw new IllegalArgumentException("Unsupported " + label + " target: " + value);
    }

    private static Object unwrap(Object object) {
        return Wrapper.unwrapped(object);
    }

    private static <T> ResourceLocation requireRegistryId(IForgeRegistry<T> registry, ResourceLocation id, String label) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid " + label + " id");
        }

        if (!registry.containsKey(id)) {
            throw new IllegalArgumentException("Unknown " + label + " id: " + id);
        }

        return id;
    }
}
