package com.pickaid.pmmojs.config;

public record PmmoDefaultSettingsPolicy(
        boolean disableAllDefaults,
        boolean disableDefaultSkills,
        boolean disableDefaultPerks,
        boolean disableDefaultRequirements,
        boolean disableDefaultXpAwards,
        boolean disableDefaultItemExtras,
        boolean disableDefaultBlockVeinData
) {
    public boolean disableSkills() {
        return disableAllDefaults || disableDefaultSkills;
    }

    public boolean disablePerks() {
        return disableAllDefaults || disableDefaultPerks;
    }

    public boolean disableRequirements() {
        return disableAllDefaults || disableDefaultRequirements;
    }

    public boolean disableXpAwards() {
        return disableAllDefaults || disableDefaultXpAwards;
    }

    public boolean disableItemExtras() {
        return disableAllDefaults || disableDefaultItemExtras;
    }

    public boolean disableBlockVeinData() {
        return disableAllDefaults || disableDefaultBlockVeinData;
    }
}
