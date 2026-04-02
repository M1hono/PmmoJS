package com.pickaid.pmmojs.kubejs.events.server.api;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PMMOInternalType {
    DIMENSION_TRAVEL("dimensionTravel"),
    EXPLOSION("explosion"),
    LOGIN("login"),
    MOUNT("mount"),
    PISTON("piston"),
    PLAYER_DEATH("playerDeath"),
    POTION_BREW("potionBrew"),
    SLEEP_FINISHED("sleepFinished");

    private static final Map<String, PMMOInternalType> BY_ID = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(PMMOInternalType::getId, Function.identity()));

    private final String id;

    PMMOInternalType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static PMMOInternalType byId(String id) {
        if (id == null) {
            return null;
        }

        String normalized = id.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        PMMOInternalType byExactId = BY_ID.get(normalized);
        if (byExactId != null) {
            return byExactId;
        }

        for (PMMOInternalType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        return null;
    }
}
