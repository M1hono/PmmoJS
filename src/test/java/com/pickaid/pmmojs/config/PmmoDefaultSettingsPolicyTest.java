package com.pickaid.pmmojs.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PmmoDefaultSettingsPolicyTest {
    @Test
    void masterToggleDisablesEveryCategory() {
        PmmoDefaultSettingsPolicy policy = new PmmoDefaultSettingsPolicy(
                true,
                false,
                false,
                false,
                false,
                false,
                false
        );

        assertTrue(policy.disableSkills());
        assertTrue(policy.disablePerks());
        assertTrue(policy.disableRequirements());
        assertTrue(policy.disableXpAwards());
        assertTrue(policy.disableItemExtras());
        assertTrue(policy.disableBlockVeinData());
    }

    @Test
    void individualTogglesStayScopedWhenMasterSwitchIsOff() {
        PmmoDefaultSettingsPolicy policy = new PmmoDefaultSettingsPolicy(
                false,
                true,
                false,
                true,
                false,
                true,
                false
        );

        assertTrue(policy.disableSkills());
        assertFalse(policy.disablePerks());
        assertTrue(policy.disableRequirements());
        assertFalse(policy.disableXpAwards());
        assertTrue(policy.disableItemExtras());
        assertFalse(policy.disableBlockVeinData());
    }
}
