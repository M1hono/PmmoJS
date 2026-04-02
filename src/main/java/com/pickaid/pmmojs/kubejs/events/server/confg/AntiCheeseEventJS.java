package com.pickaid.pmmojs.kubejs.events.server.confg;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.features.anticheese.CheeseTracker;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AntiCheeseEventJS extends EventJS {
    public static Boolean afkCanSubtract;

    public static Map<EventType, CheeseTracker.Setting> customAfkSettings = new HashMap<>();
    public static Map<EventType, Boolean> removedAfkSettings = new HashMap<>();

    public static Map<EventType, CheeseTracker.Setting> customDiminishingSettings = new HashMap<>();
    public static Map<EventType, Boolean> removedDiminishingSettings = new HashMap<>();

    public static Map<EventType, CheeseTracker.Setting> customNormalizationSettings = new HashMap<>();
    public static Map<EventType, Boolean> removedNormalizationSettings = new HashMap<>();

    public AntiCheeseEventJS() {
        afkCanSubtract = null;
        customAfkSettings = new LinkedHashMap<>();
        removedAfkSettings = new LinkedHashMap<>();
        customDiminishingSettings = new LinkedHashMap<>();
        removedDiminishingSettings = new LinkedHashMap<>();
        customNormalizationSettings = new LinkedHashMap<>();
        removedNormalizationSettings = new LinkedHashMap<>();
        for (EventType eventType : EventType.values()) {
            removedAfkSettings.put(eventType, false);
            removedDiminishingSettings.put(eventType, false);
            removedNormalizationSettings.put(eventType, false);
        }
    }

    @Info("""
            Sets whether AFK penalties can subtract XP or only reduce it to zero.
            """)
    public AntiCheeseEventJS setAfkCanSubtract(boolean value) {
        afkCanSubtract = value;
        return this;
    }

    @Info("""
            Adds an AFK tracking setting for an event type.
            """)
    public AfkSettingBuilder addAfkSetting(EventType eventType) {
        return new AfkSettingBuilder(this, eventType);
    }

    @Info("""
            Removes an AFK tracking setting for an event type.
            """)
    public AntiCheeseEventJS removeAfkSetting(EventType eventType) {
        removedAfkSettings.put(eventType, true);
        return this;
    }

    @Info("""
            Adds a diminishing XP setting for an event type.
            """)
    public DiminishingSettingBuilder addDiminishingSetting(EventType eventType) {
        return new DiminishingSettingBuilder(this, eventType);
    }

    @Info("""
            Removes a diminishing XP setting for an event type.
            """)
    public AntiCheeseEventJS removeDiminishingSetting(EventType eventType) {
        removedDiminishingSettings.put(eventType, true);
        return this;
    }

    @Info("""
            Adds a normalization setting for an event type.
            """)
    public NormalizationSettingBuilder addNormalizationSetting(EventType eventType) {
        return new NormalizationSettingBuilder(this, eventType);
    }

    @Info("""
            Removes a normalization setting for an event type.
            """)
    public AntiCheeseEventJS removeNormalizationSetting(EventType eventType) {
        removedNormalizationSettings.put(eventType, true);
        return this;
    }

    // Builder for AFK settings
    public static class AfkSettingBuilder {
        private final AntiCheeseEventJS event;
        private final EventType eventType;
        private final CheeseTracker.Setting.Builder builder;

        private AfkSettingBuilder(AntiCheeseEventJS event, EventType eventType) {
            this.event = event;
            this.eventType = eventType;
            this.builder = CheeseTracker.Setting.build();
        }

        @Info("""
                Specifies the source(s) this setting applies to.
                """)
        public AfkSettingBuilder source(String... sources) {
            builder.source(sources);
            return this;
        }

        @Info("""
                Sets the minimum time (in ticks) a player must be AFK before penalties apply.
                """)
        public AfkSettingBuilder minTime(Number ticks) {
            builder.minTime(ticks.intValue());
            return this;
        }

        @Info("""
                Sets the reduction factor per half-second of AFK time.
                """)
        public AfkSettingBuilder reduction(Number value) {
            builder.reduction(value.doubleValue());
            return this;
        }

        @Info("""
                Sets the amount of AFK time reduced per half-second when not AFK.
                """)
        public AfkSettingBuilder cooloff(Number amount) {
            builder.cooloff(amount.intValue());
            return this;
        }

        @Info("""
                Sets the distance tolerance for determining if a player is AFK.
                """)
        public AfkSettingBuilder tolerance(Number blocks) {
            builder.tolerance(blocks.doubleValue());
            return this;
        }

        @Info("""
                Sets whether camera movement should be considered for AFK detection.
                """)
        public AfkSettingBuilder strictTolerance(boolean strict) {
            builder.setStrictness(strict);
            return this;
        }

        @Info("""
                Builds and adds the setting to the configuration.
                """)
        public AntiCheeseEventJS build() {
            CheeseTracker.Setting setting = builder.build();
            event.customAfkSettings.put(eventType, setting);
            return event;
        }
    }

    public static class DiminishingSettingBuilder {
        private final AntiCheeseEventJS event;
        private final EventType eventType;
        private final CheeseTracker.Setting.Builder builder;

        private DiminishingSettingBuilder(AntiCheeseEventJS event, EventType eventType) {
            this.event = event;
            this.eventType = eventType;
            this.builder = CheeseTracker.Setting.build();
        }

        @Info("""
                Specifies the source(s) this setting applies to.
                """)
        public DiminishingSettingBuilder source(String... sources) {
            builder.source(sources);
            return this;
        }

        @Info("""
                Sets the duration (in ticks) before diminished XP resets.
                """)
        public DiminishingSettingBuilder retention(Number ticks) {
            builder.retention(ticks.intValue());
            return this;
        }

        @Info("""
                Sets the reduction factor per repeated event.
                """)
        public DiminishingSettingBuilder reduction(Number value) {
            builder.reduction(value.doubleValue());
            return this;
        }

        @Info("""
                Builds and adds the setting to the configuration.
                """)
        public AntiCheeseEventJS build() {
            CheeseTracker.Setting setting = builder.build();
            event.customDiminishingSettings.put(eventType, setting);
            return event;
        }
    }

    public static class NormalizationSettingBuilder {
        private final AntiCheeseEventJS event;
        private final EventType eventType;
        private final CheeseTracker.Setting.Builder builder;

        private NormalizationSettingBuilder(AntiCheeseEventJS event, EventType eventType) {
            this.event = event;
            this.eventType = eventType;
            this.builder = CheeseTracker.Setting.build();
        }

        @Info("""
                Specifies the source(s) this setting applies to.
                """)
        public NormalizationSettingBuilder source(String... sources) {
            builder.source(sources);
            return this;
        }

        @Info("""
                Sets the duration (in ticks) to retain normalized values.
                """)
        public NormalizationSettingBuilder retention(Number ticks) {
            builder.retention(ticks.intValue());
            return this;
        }

        @Info("""
                Sets the flat tolerance for XP increases.
                """)
        public NormalizationSettingBuilder toleranceFlat(Number value) {
            builder.tolerance(value.intValue());
            return this;
        }

        @Info("""
                Sets the percentage tolerance for XP increases.
                """)
        public NormalizationSettingBuilder tolerancePercent(Number value) {
            builder.tolerance(value.doubleValue());
            return this;
        }

        @Info("""
                Builds and adds the setting to the configuration.
                """)
        public AntiCheeseEventJS build() {
            CheeseTracker.Setting setting = builder.build();
            event.customNormalizationSettings.put(eventType, setting);
            return event;
        }
    }
}
