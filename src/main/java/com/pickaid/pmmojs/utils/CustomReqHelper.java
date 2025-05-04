package com.pickaid.pmmojs.utils;

import com.pickaid.pmmojs.PmmoJS;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.APIUtils;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomReqHelper {
    @Info("""
            Get custom requirements map.
            """)
    public static Map<String, Map<String, Integer>> getCustomRequirements() {
        return PmmoJS.REQ_MAP;
    }

    @Info("""
            Get custom requirement for given object.
            """)
    public static Map<String, Integer> getCustomRequirement(String objectName) {
        return PmmoJS.REQ_MAP.get(objectName);
    }

    public static void addCustomReqObject(String objectName, Map<String, ? extends Number> requirements) {
        for (Map.Entry<String, ? extends Number> entry : requirements.entrySet()) {
            String skill = entry.getKey();
            int value = entry.getValue().intValue();
            PmmoJS.REQ_MAP.computeIfAbsent(objectName, k -> new HashMap<>()).put(skill, value);
        }
    }

    public static void removeCustomReqObject(String objectName) {
        PmmoJS.REQ_MAP.remove(objectName);
    }

    public static void replaceCustomReqObject(String objectName, Map<String, ? extends Number> requirements) {
        PmmoJS.REQ_MAP.remove(objectName);
        addCustomReqObject(objectName, requirements);
    }

    public static boolean meetReq(Player player, String objectName) {
        Map<String, Integer> requirement = PmmoJS.REQ_MAP.get(objectName);
        if (requirement == null) {
            PmmoJS.LOGGER.warn("No custom requirements found for object '{}'", objectName);
            return true;
        }
        for (Map.Entry<String, Integer> entry : requirement.entrySet()) {
            String skill = entry.getKey();
            int value = entry.getValue();
            int playerValue = APIUtils.getLevel(skill, player);
            if (playerValue < value) {
                return false;
            }
        }
        return true;
    }

    public static List<String> SkillsNotMet(Player player, String objectName) {
        Map<String, Integer> requirement = PmmoJS.REQ_MAP.get(objectName);
        if (requirement == null) {
            return null;
        }
        List<String> notMet = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : requirement.entrySet()) {
            String skill = entry.getKey();
            int value = entry.getValue();
            int playerValue = APIUtils.getLevel(skill, player);
            if (playerValue < value) {
                notMet.add(skill);
            }
        }
        return notMet.isEmpty()? null : notMet;
    }
}
