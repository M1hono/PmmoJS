package com.pickaid.pmmojs.utils;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.SkillData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class SkillHelper {
    @Info("""
            get the current level of a skill.
            """)
    public static int getLevel(Player player, String skill) {
        return APIUtils.getLevel(skill, player);
    }

    @Info("""
            get the current experience points of a skill.
            """)
    public static void setLevel(Player player, String skill, Number amount) {
        APIUtils.setLevel(skill, player, amount.intValue());
    }

    @Info("""
            get the current experience points of a skill.
            """)
    public static long getXp(Player player, String skill) {
        return APIUtils.getXp(skill, player);
    }

    @Info("""
            set the current experience points of a skill.
            """)
    public static void setXp(Player player, String skill, Number xp) {
        APIUtils.setXp(skill, player, xp.longValue());
    }

    @Info("""
            add experience points to a skill.
            """)
    public static void addXP(Player player, String skill, Number amount) {
        APIUtils.addXp(skill, player, amount.longValue());
    }

    @Info("""
            Can only use when server has been loaded and config values obtained.
            Get all skills and their data.
            """)
    public static Map<String, SkillData> getSkills() {
        Map<String, SkillData> map = new HashMap<>();
        try {
            map.putAll(SkillsConfig.SKILLS.get());
        } catch (Exception ignored) {}
        return map;
    }

    @Info("""
            Can only use when server has been loaded and config values obtained.
            Get skill data by its name.
            """)
    public static SkillData getSkill(String skill) {
        return SkillsConfig.SKILLS.get().getOrDefault(skill, SkillData.Builder.getDefault());
    }
}
