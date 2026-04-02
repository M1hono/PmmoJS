package com.pickaid.pmmojs.mixin;

import com.mojang.serialization.Codec;
import harmonised.pmmo.config.readers.TomlConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TomlConfigHelper.ConfigObject.class)
public interface ConfigObjectAccessor<T> {
    @Accessor(value = "value", remap = false)
    ForgeConfigSpec.ConfigValue<Object> pmmojs$getValue();

    @Accessor(value = "codec", remap = false)
    Codec<T> pmmojs$getCodec();

    @Accessor(value = "cachedObject", remap = false)
    void pmmojs$setCachedObject(Object cachedObject);

    @Accessor(value = "parsedObject", remap = false)
    void pmmojs$setParsedObject(T parsedObject);
}
