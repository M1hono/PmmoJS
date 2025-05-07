package com.pickaid.pmmojs.utils;

import dev.latvian.mods.kubejs.typings.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Info("Builder for constructing complex NBT paths using a fluent interface")
public class NbtPathBuilder {
    private final List<PathComponent> components = new ArrayList<>();

    @Info("Creates a new root path builder instance")
    public static NbtPathBuilder root() {
        return new NbtPathBuilder();
    }

    @Info("Appends a final value node to the path")
    public NbtPathBuilder value(String key) {
        components.add(new ValueComponent(key));
        return this;
    }

    @Info("Appends a compound tag node without conditions")
    public NbtPathBuilder compound(String key) {
        components.add(new CompoundComponent(key, null));
        return this;
    }

    @Info("Appends a compound tag node with matching conditions")
    public NbtPathBuilder compoundWithConditions(String key, Condition... conditions) {
        components.add(new CompoundComponent(key, Condition.combine(conditions)));
        return this;
    }

    @Info("Appends a list node that matches all elements")
    public NbtPathBuilder list(String key) {
        components.add(new ListComponent(key, ListQuery.ALL));
        return this;
    }

    @Info("Appends a list node targeting specific index")
    public NbtPathBuilder listWithIndex(String key, int index) {
        components.add(new ListComponent(key, new IndexQuery(index)));
        return this;
    }

    @Info("Appends a list node with element matching conditions")
    public NbtPathBuilder listWithConditions(String key, Condition... conditions) {
        components.add(new ListComponent(key, new ConditionQuery(conditions)));
        return this;
    }

    @Info("""
        Constructs the final NBT path string
        @throws IllegalStateException if path doesn't end with value node
        """)
    public String build() {
        validatePath();
        return components.stream()
                .map(PathComponent::toString)
                .collect(Collectors.joining("."));
    }

    private void validatePath() {
        if (components.isEmpty()) return;

        PathComponent last = components.get(components.size() - 1);
        if (!(last instanceof ValueComponent)) {
            throw new IllegalStateException("Path must end with a value component");
        }
    }

    @Info("Base interface for NBT path components")
    private interface PathComponent {
        String toString();
    }

    @Info("Represents a terminal value node in the path")
    private record ValueComponent(String key) implements PathComponent {
        @Override
        public String toString() {
            return key;
        }
    }

    @Info("Represents a compound tag with optional conditions")
    private record CompoundComponent(String key, String condition) implements PathComponent {
        @Override
        public String toString() {
            return condition == null ?
                    key + "{}" :
                    key + "{" + condition + "}";
        }
    }

    @Info("Represents a list with specific query parameters")
    private record ListComponent(String key, ListQuery query) implements PathComponent {
        @Override
        public String toString() {
            return key + query;
        }
    }

    @Info("Condition for filtering NBT structures")
    public static class Condition {
        private final String key;
        private final Object value;

        private Condition(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Info("Creates a key-value matching condition")
        public static Condition of(String key, Object value) {
            return new Condition(key, value);
        }

        @Info("Combines multiple conditions with AND logic")
        static String combine(Condition... conditions) {
            return String.join(",",
                    java.util.Arrays.stream(conditions)
                            .map(Condition::toString)
                            .toArray(String[]::new)
            );
        }

        @Override
        public String toString() {
            return formatKey(key) + ":" + formatValue(value);
        }

        private String formatKey(String key) {
            return key.contains(" ") ? "\"" + key + "\"" : key;
        }

        private String formatValue(Object value) {
            if (value instanceof String) {
                return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
            } else if (value instanceof Number) {
                return value.toString();
            } else if (value instanceof Boolean) {
                return (Boolean) value ? "true" : "false";
            }
            return value.toString();
        }
    }

    @Info("Interface for list query types")
    private interface ListQuery {
        ListQuery ALL = new ListQuery() {
            @Override
            public String toString() {
                return "[]";
            }
        };

        String toString();
    }

    @Info("Query for specific list index")
    private static class IndexQuery implements ListQuery {
        private final int index;

        IndexQuery(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "[" + index + "]";
        }
    }

    @Info("Query for list elements matching conditions")
    private static class ConditionQuery implements ListQuery {
        private final String conditions;

        ConditionQuery(Condition[] conditions) {
            this.conditions = "[{" + Condition.combine(conditions) + "}]";
        }

        @Override
        public String toString() {
            return conditions;
        }
    }
}