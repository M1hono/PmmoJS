package com.pickaid.pmmojs.contents.settings;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.api.enums.ObjectType;
import net.minecraft.resources.ResourceLocation;
import java.util.Optional;

@Info("Builder for block-related PMMO settings")
public class BlockSettingsBuilder extends PMMOSettingsBuilder {
    private Optional<Integer> veinChargeCap = Optional.empty();
    private Optional<Double> veinChargeRate = Optional.empty();
    private Optional<Integer> veinConsumeAmount = Optional.empty();

    public BlockSettingsBuilder(ObjectType objectType, ResourceLocation objectId) {
        super(objectType, objectId);
    }


    @Info("""
            Sets the object as an override.
            You must enable this to change vanilla stuff.
            """)
    public BlockSettingsBuilder override(boolean override) {
        this.isOverride = override;
        return this;
    }

    @Info("Sets the vein miner charge capacity")
    public BlockSettingsBuilder veinChargeCap(int chargeCap) {
        this.veinChargeCap = Optional.of(chargeCap);
        registerVeinData();
        return this;
    }

    @Info("Sets the vein miner charge rate")
    public BlockSettingsBuilder veinChargeRate(double chargeRate) {
        this.veinChargeRate = Optional.of(chargeRate);
        registerVeinData();
        return this;
    }

    @Info("Sets the vein miner consume amount")
    public BlockSettingsBuilder veinConsumeAmount(int consumeAmount) {
        this.veinConsumeAmount = Optional.of(consumeAmount);
        registerVeinData();
        return this;
    }

    private void registerVeinData() {
        APIUtils.registerVeinData(objectType, objectId, veinChargeCap, veinChargeRate, veinConsumeAmount, isOverride);
    }
}