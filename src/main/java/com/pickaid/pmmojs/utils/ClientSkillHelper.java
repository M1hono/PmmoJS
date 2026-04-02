package com.pickaid.pmmojs.utils;

import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.SkillData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class ClientSkillHelper {
    @Info("""
            get the current level of a skill.
            """)
    public static int getLevel(Player player, String skill) {
        return APIUtils.getLevel(skill, player);
    }

    @Info("""
            get the current experience points of a skill.
            """)
    public static long getXp(Player player, String skill) {
        return APIUtils.getXp(skill, player);
    }

    @Info("""
            Can only use when config values have been synced.
            Get all skills and their data.
            """)
    public static Map<String, SkillData> getSkills() {
        Map<String, SkillData> map = new HashMap<>();
        try {
            map.putAll(SkillsConfig.SKILLS.get());
        } catch (Exception ignored) {
        }
        return map;
    }

    @Info("""
            Can only use when config values have been synced.
            Get skill data by its name.
            """)
    public static SkillData getSkill(String skill) {
        return SkillsConfig.SKILLS.get().getOrDefault(skill, SkillData.Builder.getDefault());
    }
}
