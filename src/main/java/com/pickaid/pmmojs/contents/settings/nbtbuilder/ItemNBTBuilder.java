package com.pickaid.pmmojs.contents.settings.nbtbuilder;

import com.pickaid.pmmojs.contents.settings.ItemSettingsBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import harmonised.pmmo.api.enums.ReqType;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.config.codecs.ObjectData;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.core.nbt.BehaviorToPrevious;
import harmonised.pmmo.core.nbt.LogicEntry;
import harmonised.pmmo.core.nbt.Operator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

@Info("Builder for NBT-based requirements, XP, and bonuses for items in PMMO")
public class ItemNBTBuilder {

    private final ItemSettingsBuilder parent;
    private final ObjectType objectType;
    private final ResourceLocation objectId;
    private final ReqType reqType;
    private EventType eventType = null;
    private ModifierDataType modifierType = null;
    private boolean isXpValue = false;
    private boolean isBonus = false;
    private boolean isOverride;

    // Current state for building
    private BehaviorToPrevious behavior = BehaviorToPrevious.ADD_TO;
    private boolean additive = false;

    // Current case being built
    private List<String> currentPaths = new ArrayList<>();
    private List<LogicEntry.Criteria> currentCriteria = new ArrayList<>();

    // All cases to be included in the logic entry
    private List<LogicEntry.Case> cases = new ArrayList<>();

    // Constructor for requirements
    public ItemNBTBuilder(ItemSettingsBuilder parent, ObjectType objectType, ResourceLocation objectId, ReqType reqType, boolean isOverride) {
        this.parent = parent;
        this.objectType = objectType;
        this.objectId = objectId;
        this.reqType = reqType;
        this.isOverride = isOverride;
    }

    // Constructor for XP values
    public ItemNBTBuilder(ItemSettingsBuilder parent, ObjectType objectType, ResourceLocation objectId, EventType eventType, boolean isOverride) {
        this.parent = parent;
        this.objectType = objectType;
        this.objectId = objectId;
        this.reqType = null;
        this.eventType = eventType;
        this.isXpValue = true;
        this.isOverride = isOverride;
    }

    // Constructor for bonuses
    public ItemNBTBuilder(ItemSettingsBuilder parent, ObjectType objectType, ResourceLocation objectId, ModifierDataType modifierType, boolean isOverride) {
        this.parent = parent;
        this.objectType = objectType;
        this.objectId = objectId;
        this.reqType = null;
        this.modifierType = modifierType;
        this.isBonus = true;
        this.isOverride = isOverride;
    }

    @Info("Sets the behavior for combining with existing requirements")
    public ItemNBTBuilder behavior(BehaviorToPrevious behavior) {
        this.behavior = behavior;
        return this;
    }

    @Info("Sets whether requirements should be added or replace existing ones (should_cases_add field)")
    public ItemNBTBuilder additive(boolean additive) {
        this.additive = additive;
        return this;
    }

    @Info("Creates a new case with the specified NBT path")
    public ItemNBTBuilder newCase(String nbtPath) {
        // If we have an existing case with paths and criteria, save it
        if (!currentPaths.isEmpty() && !currentCriteria.isEmpty()) {
            saveCurrentCase();
        }

        // Start a new case
        currentPaths = new ArrayList<>();
        currentCriteria = new ArrayList<>();
        currentPaths.add(nbtPath);

        return this;
    }

    @Info("Adds an NBT path to the current case")
    public ItemNBTBuilder path(String nbtPath) {
        if (currentPaths.isEmpty() && !cases.isEmpty()) {
            // Starting a new case
            currentPaths = new ArrayList<>();
            currentCriteria = new ArrayList<>();
        }

        currentPaths.add(nbtPath);
        return this;
    }

    @Info("Sets multiple NBT paths to the current case")
    public ItemNBTBuilder paths(List<String> nbtPaths) {
        if (currentPaths.isEmpty() && !cases.isEmpty()) {
            // Starting a new case
            currentPaths = new ArrayList<>();
            currentCriteria = new ArrayList<>();
        }

        currentPaths.addAll(nbtPaths);
        return this;
    }

    @Info("Adds a criteria to the current case with the specified skill requirements")
    public ItemNBTBuilder criteria(Operator operator, List<String> comparators, Map<String, ? extends Number> skillValues) {
        Map<String, Double> doubleValues = new HashMap<>();
        for (Map.Entry<String, ? extends Number> entry : skillValues.entrySet()) {
            doubleValues.put(entry.getKey(), entry.getValue().doubleValue());
        }

        currentCriteria.add(new LogicEntry.Criteria(
                operator,
                comparators.isEmpty() ? Optional.empty() : Optional.of(comparators),
                doubleValues
        ));

        return this;
    }

    @Info("Adds a criteria to the current case for a specific skill and value")
    public ItemNBTBuilder addCriteria(Operator operator, List<String> comparators, String skill, Number value) {
        Map<String, Double> values = new HashMap<>();
        values.put(skill, value.doubleValue());

        currentCriteria.add(new LogicEntry.Criteria(
                operator,
                comparators.isEmpty() ? Optional.empty() : Optional.of(comparators),
                values
        ));

        return this;
    }

    @Info("Adds an EQUALS criteria for multiple string values")
    public ItemNBTBuilder equals(String[] comparators, String skill, Number value) {
        return addCriteria(Operator.EQUALS, Arrays.asList(comparators), skill, value);
    }

    @Info("Adds an EQUALS criteria for a single string value")
    public ItemNBTBuilder equals(String comparator, String skill, Number value) {
        return addCriteria(Operator.EQUALS, List.of(comparator), skill, value);
    }

    @Info("Adds a GREATER_THAN criteria for a numeric value")
    public ItemNBTBuilder greaterThan(Number comparator, String skill, Number value) {
        return addCriteria(Operator.GREATER_THAN, List.of(comparator.toString()), skill, value);
    }

    @Info("Adds a LESS_THAN criteria for a numeric value")
    public ItemNBTBuilder lessThan(Number comparator, String skill, Number value) {
        return addCriteria(Operator.LESS_THAN, List.of(comparator.toString()), skill, value);
    }

    @Info("Adds a GREATER_THAN_OR_EQUAL criteria for a numeric value")
    public ItemNBTBuilder greaterThanOrEqual(Number comparator, String skill, Number value) {
        return addCriteria(Operator.GREATER_THAN_OR_EQUAL, List.of(comparator.toString()), skill, value);
    }

    @Info("Adds a LESS_THAN_OR_EQUAL criteria for a numeric value")
    public ItemNBTBuilder lessThanOrEqual(Number comparator, String skill, Number value) {
        return addCriteria(Operator.LESS_THAN_OR_EQUAL, List.of(comparator.toString()), skill, value);
    }

    @Info("Adds an EXISTS criteria with no comparator")
    public ItemNBTBuilder exists(String skill, Number value) {
        Map<String, Double> values = new HashMap<>();
        values.put(skill, value.doubleValue());

        currentCriteria.add(new LogicEntry.Criteria(
                Operator.EXISTS,
                Optional.empty(),
                values
        ));

        return this;
    }

    @Info("Sets up a requirement based on an enchantment level")
    public ItemNBTBuilder enchantmentRequirement(String enchantmentId, String skill, Number requiredLevel) {
        String enchantPath = "Enchantments[{id:\"" + enchantmentId + "\"}].lvl";

        if (!currentPaths.isEmpty() && !currentCriteria.isEmpty()) {
            saveCurrentCase();
        }

        currentPaths = new ArrayList<>();
        currentCriteria = new ArrayList<>();
        currentPaths.add(enchantPath);

        Map<String, Double> values = new HashMap<>();
        values.put(skill, requiredLevel.doubleValue());

        currentCriteria.add(new LogicEntry.Criteria(
                Operator.GREATER_THAN_OR_EQUAL,
                Optional.empty(),
                values
        ));

        return this;
    }

    private void saveCurrentCase() {
        if (!currentPaths.isEmpty() && !currentCriteria.isEmpty()) {
            cases.add(new LogicEntry.Case(
                    new ArrayList<>(currentPaths),
                    new ArrayList<>(currentCriteria)
            ));
        }
    }

    @Info("Registers the NBT requirement, XP value, or bonus")
    public ItemSettingsBuilder done() {
        if (!currentPaths.isEmpty() && !currentCriteria.isEmpty()) {
            saveCurrentCase();
        }

        if (cases.isEmpty()) {
            throw new IllegalStateException("NBT data must have at least one case with path and criteria");
        }

        LogicEntry entry = new LogicEntry(
                behavior,
                additive,
                cases
        );

        ObjectData data = (ObjectData) Core.get(LogicalSide.SERVER)
                .getLoader()
                .getLoader(objectType)
                .getData()
                .get(objectId.toString());

        if (data == null) {
            data = new ObjectData(isOverride);
        }

        if (isXpValue && eventType != null) {
            Map<EventType, List<LogicEntry>> nbtXpValues = data.nbtXpValues();
            List<LogicEntry> entries = nbtXpValues.computeIfAbsent(eventType, k -> new ArrayList<>());
            entries.add(entry);
        } else if (isBonus && modifierType != null) {
            Map<ModifierDataType, List<LogicEntry>> nbtBonuses = data.nbtBonuses();
            List<LogicEntry> entries = nbtBonuses.computeIfAbsent(modifierType, k -> new ArrayList<>());
            entries.add(entry);
        } else if (reqType != null) {
            Map<ReqType, List<LogicEntry>> nbtReqs = data.nbtReqs();
            List<LogicEntry> entries = nbtReqs.computeIfAbsent(reqType, k -> new ArrayList<>());
            entries.add(entry);
        } else {
            throw new IllegalStateException("Invalid NBT data type");
        }
        Core.get(LogicalSide.SERVER)
                .getLoader()
                .getLoader(objectType)
                .registerOverride(objectId, data);

        return parent;
    }
}