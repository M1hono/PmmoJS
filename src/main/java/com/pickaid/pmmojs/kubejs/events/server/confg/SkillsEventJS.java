package com.pickaid.pmmojs.kubejs.events.server.confg;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.config.Config;
import harmonised.pmmo.config.codecs.SkillData;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class SkillsEventJS extends EventJS {
    public static Map<String, SkillData> customSkills = new HashMap<>();
    public static List<String> removedSkills = new ArrayList<String>();

    public Builder addSkill(String string) {
        return Builder.start(string);
    }

    public void removeDefaultSkill(String string) {
        removedSkills.add(string);
    }

    private static class Builder {
        String skillName;
        int color = 16777215;
        int maxLevel = Integer.MAX_VALUE;
        int iconSize = 18;
        boolean afkExempt = false;
        boolean displayName = false;
        boolean useTotal = false;
        boolean showInList = true;
        ResourceLocation icon = new ResourceLocation("pmmo", "textures/skills/missing_icon.png");
        Map<String, Double> groupOf = new HashMap();

        private Builder(String skillName) {
            this.skillName = skillName;
        }

        public static SkillData getDefault() {
            return new SkillData(Optional.of(16777215), Optional.of(false), Optional.of(false), Optional.of(true), Optional.of(false), Optional.empty(), Optional.of((Integer) Config.MAX_LEVEL.get()), Optional.of(new ResourceLocation("pmmo", "textures/skills/missing_icon.png")), Optional.of(18));
        }

        public static Builder start(String skillName) {
            return new Builder(skillName);
        }

        @Info("the hex color converted to integer")
        public Builder withColor(Number color) {
            this.color = color.intValue();
            return this;
        }

        @Info("Sets the texture location for the skill's icon in the inventory menu")
        public Builder withIcon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        @Info("Tells pmmo how many pixels the above image is so it can be scaled properly")
        public Builder withIconSize(Number size) {
            this.iconSize = size.intValue();
            return this;
        }

        @Info("Sets the max level for this specific skill independent of the global max level\n")
        public Builder withMaxLevel(Number maxLevel) {
            this.maxLevel = maxLevel.intValue();
            return this;
        }

        @Info("if true afk penalties from Anti-Cheese are ignored for this skill")
        public Builder withAfkExempt(boolean afkExempt) {
            this.afkExempt = afkExempt;
            return this;
        }

        public Builder withDisplayName(boolean displayGroupName) {
            this.displayName = displayGroupName;
            return this;
        }

        @Info("""
                if true, will display the skill's group name before the skill's name in the inventory menu\s
                if false, will display only the skill's name in the inventory menu
                """)
        public Builder withUseTotal(boolean useTotalLevels) {
            this.useTotal = useTotalLevels;
            return this;
        }

        @Info("if false, this skill will not show in either skill list.")
        public Builder omitFromList() {
            this.showInList = false;
            return this;
        }

        @Info("""
                the skills that make up this skill group.  the numbers represent weights that
                each skill has and tell pmmo how to divide the xp or requirements.
                """)
        public Builder setGroupOf(Map<String, Number> group) {
            for (Map.Entry<String, Number> entry : group.entrySet()) {
                this.groupOf.put(entry.getKey(), entry.getValue().doubleValue());
            }
            return this;
        }

        public void build() {
            customSkills.put(this.skillName, new SkillData(Optional.of(this.color), Optional.of(this.afkExempt), Optional.of(this.displayName), Optional.of(this.showInList), Optional.of(this.useTotal), this.groupOf.isEmpty() ? Optional.empty() : Optional.of(this.groupOf), Optional.of(this.maxLevel), Optional.of(this.icon), Optional.of(this.iconSize)));
        }
    }
}
