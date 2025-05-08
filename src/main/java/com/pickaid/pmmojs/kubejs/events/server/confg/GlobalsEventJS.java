package com.pickaid.pmmojs.kubejs.events.server.confg;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;

import java.util.HashMap;
import java.util.Map;

public class GlobalsEventJS extends EventJS {
    public static Map<String, String> customPaths = new HashMap<>();
    public static Map<String, String> removedPaths = new HashMap<>();

    public static Map<String, String> customConstants = new HashMap<>();
    public static Map<String, String> removedConstants = new HashMap<>();
    @Info("""
            Adds or updates a path in the globals configuration.
            
            @param key The name of the path
            @param path The NBT path value
            """)
    public GlobalsEventJS addPath(String key, String path) {
        customPaths.put(key, path);
        return this;
    }

    @Info("""
            Removes a path from the globals configuration.
            
            @param key The name of the path to remove
            """)
    public GlobalsEventJS removePath(String key) {
        removedPaths.put(key, "");
        return this;
    }

    // Constant settings
    @Info("""
            Adds or updates a constant in the globals configuration.
            
            @param key The name of the constant
            @param value The value for the constant
            """)
    @Deprecated
    public GlobalsEventJS addConstant(String key, String value) {
        customConstants.put(key, value);
        return this;
    }

    @Info("""
            Removes a constant from the globals configuration.
            
            @param key The name of the constant to remove
            """)
    @Deprecated
    public GlobalsEventJS removeConstant(String key) {
        removedConstants.put(key, "");
        return this;
    }
}