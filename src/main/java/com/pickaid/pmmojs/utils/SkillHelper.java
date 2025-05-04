package com.pickaid.pmmojs.utils;

import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.config.SkillsConfig;
import harmonised.pmmo.config.codecs.SkillData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class SkillHelper {
    public static int getLevel(Player player, String skill) {
        return APIUtils.getLevel(skill, player);
    }

    public static void setLevel(Player player, String skill, Number amount) {
        APIUtils.setLevel(skill, player, amount.intValue());
    }

    public static long getXp(Player player, String skill) {
        return APIUtils.getXp(skill, player);
    }

    public static void setXp(Player player, String skill, long xp) {
        APIUtils.setXp(skill, player, xp);
    }

    public static void addXP(Player player, String skill, Number amount) {
        APIUtils.addXp(skill, player, amount.longValue());
    }

    public static Map<String, SkillData> getSkills() {
        Map<String, SkillData> map = new HashMap<>();
        try {
            map.putAll(SkillsConfig.SKILLS.get());
        } catch (Exception ignored) {}
        return map;
    }

    public static SkillData getSkill(String skill) {
        return SkillsConfig.SKILLS.get().get(skill);
    }
}
