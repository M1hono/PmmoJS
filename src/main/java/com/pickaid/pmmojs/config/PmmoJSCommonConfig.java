package com.pickaid.pmmojs.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class PmmoJSCommonConfig {
    public static final ForgeConfigSpec SPEC;
    private static final PmmoJSCommonConfig INSTANCE;

    final ForgeConfigSpec.BooleanValue disableAllDefaults;
    final ForgeConfigSpec.BooleanValue disableDefaultSkills;
    final ForgeConfigSpec.BooleanValue disableDefaultPerks;
    final ForgeConfigSpec.BooleanValue disableDefaultRequirements;
    final ForgeConfigSpec.BooleanValue disableDefaultXpAwards;
    final ForgeConfigSpec.BooleanValue disableDefaultItemExtras;
    final ForgeConfigSpec.BooleanValue disableDefaultBlockVeinData;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        INSTANCE = new PmmoJSCommonConfig(builder);
        SPEC = builder.build();
    }

    private PmmoJSCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("PmmoJS common configuration").push("disableDefaultSettings");

        disableAllDefaults = builder
                .comment("Master switch. When true, all supported PMMO default data categories below are disabled before pack customizations run.")
                .define("all", false);

        disableDefaultSkills = builder
                .comment("Disable PMMO's default skills before custom SkillsConfig edits are applied.")
                .define("skills", false);

        disableDefaultPerks = builder
                .comment("Disable PMMO's default perk lists before custom PerksConfig edits are applied.")
                .define("perks", false);

        disableDefaultRequirements = builder
                .comment("Disable default PMMO item, block, and entity requirements before PmmoJS.settings scripts run.")
                .define("requirements", false);

        disableDefaultXpAwards = builder
                .comment("Disable default PMMO item, block, and entity XP-award data before PmmoJS.settings scripts run.")
                .define("xpAwards", false);

        disableDefaultItemExtras = builder
                .comment("Disable default PMMO item extras: bonuses, effects, salvage, and item vein data before PmmoJS.settings scripts run.")
                .define("itemExtras", false);

        disableDefaultBlockVeinData = builder
                .comment("Disable default PMMO block vein-mining data before PmmoJS.settings scripts run.")
                .define("blockVeinData", false);

        builder.pop();
    }

    public static PmmoDefaultSettingsPolicy defaultSettingsPolicy() {
        return new PmmoDefaultSettingsPolicy(
                INSTANCE.disableAllDefaults.get(),
                INSTANCE.disableDefaultSkills.get(),
                INSTANCE.disableDefaultPerks.get(),
                INSTANCE.disableDefaultRequirements.get(),
                INSTANCE.disableDefaultXpAwards.get(),
                INSTANCE.disableDefaultItemExtras.get(),
                INSTANCE.disableDefaultBlockVeinData.get()
        );
    }
}
