package com.pickaid.pmmojs.kubejs.probe;

import com.pickaid.pmmojs.kubejs.events.server.api.PMMOInternalType;
import com.pickaid.pmmojs.utils.PmmoHelper;
import com.probejs.ProbeJS;
import com.probejs.ProbeJSPlugin;
import com.probejs.features.plugin.DocGenerationEventJS;
import com.probejs.features.plugin.ProbeJSEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.api.enums.ModifierDataType;
import harmonised.pmmo.api.enums.ObjectType;
import harmonised.pmmo.api.enums.ReqType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PmmoJSProbePlugin extends ProbeJSPlugin {

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        super.registerClasses(type, filter);
        // Ensure PmmoJS packages are always included in documentation generation
        filter.allow("com.pickaid.pmmojs");
    }

    @Override
    public void registerEvents() {
        super.registerEvents();

        ProbeJSEvents.DOC_GEN.listenJava(ScriptType.SERVER, null, event -> {
            if (event instanceof DocGenerationEventJS docEvent) {
                registerSpecialTypes(docEvent);
                registerSnippets(docEvent);
                registerCustomSnippets(docEvent);
            }
            return null;
        });
    }

    private void registerSpecialTypes(DocGenerationEventJS event) {
        // EventType: string union of all event type names (e.g., "BLOCK_BREAK", "CRAFT")
        event.specialType("EventType", jsonChoices(PmmoHelper.getEventTypeIds()));
        // TriggerType: string union of all trigger type IDs (e.g., "block_break", "craft")
        event.specialType("TriggerType", jsonChoices(PmmoHelper.getTriggerTypeIds()));
        // PMMOInternalType: string union (e.g., "dimensionTravel", "login")
        event.specialType("PMMOInternalType", jsonChoices(PmmoHelper.getInternalTypeIds()));
        // ReqType: string union of requirement types (e.g., "WEAPON", "KILL")
        event.specialType("ReqType", jsonChoices(PmmoHelper.getReqTypeIds()));
        // ObjectType: string union (e.g., "ITEM", "BLOCK", "ENTITY")
        event.specialType("ObjectType", jsonChoices(PmmoHelper.getObjectTypeIds()));
        // ModifierDataType: string union (e.g., "BIOME", "HELD", "WORN")
        event.specialType("ModifierDataType", jsonChoices(
                Arrays.stream(ModifierDataType.values()).map(ModifierDataType::name).toList()
        ));
        // PMMOPerkSide: "SERVER" | "CLIENT" | "BOTH"
        event.specialType("PMMOPerkSide", jsonChoices(List.of("SERVER", "CLIENT", "BOTH")));
    }

    private void registerSnippets(DocGenerationEventJS event) {
        event.addSnippet("pmmojs.event_type", new ArrayList<>(PmmoHelper.getEventTypeIds()),
                "PmmoJS EventType values");
        event.addSnippet("pmmojs.trigger_type", new ArrayList<>(PmmoHelper.getTriggerTypeIds()),
                "PmmoJS trigger hook type IDs");
        event.addSnippet("pmmojs.internal_type", new ArrayList<>(PmmoHelper.getInternalTypeIds()),
                "PmmoJS internal hook type IDs");
        event.addSnippet("pmmojs.req_type", new ArrayList<>(PmmoHelper.getReqTypeIds()),
                "PmmoJS requirement type values");
        event.addSnippet("pmmojs.object_type", new ArrayList<>(PmmoHelper.getObjectTypeIds()),
                "PmmoJS object type values");
        event.addSnippet("pmmojs.modifier_type", new ArrayList<>(
                Arrays.stream(ModifierDataType.values()).map(ModifierDataType::name).toList()
        ), "PmmoJS modifier data type values");
        event.addSnippet("pmmojs.perk_side", List.of("SERVER", "CLIENT", "BOTH"),
                "PmmoJS perk side values");
        event.addSnippet("pmmojs.item_req_type", new ArrayList<>(PmmoHelper.getItemReqTypeIds()),
                "PmmoJS item-eligible requirement types");
        event.addSnippet("pmmojs.block_req_type", new ArrayList<>(PmmoHelper.getBlockReqTypeIds()),
                "PmmoJS block-eligible requirement types");
        event.addSnippet("pmmojs.entity_req_type", new ArrayList<>(PmmoHelper.getEntityReqTypeIds()),
                "PmmoJS entity-eligible requirement types");
    }

    private void registerCustomSnippets(DocGenerationEventJS event) {
        // Trigger event handler snippet
        event.customSnippet("PmmoJS Trigger Handler",
                List.of("trigger_type_id"),
                List.of(
                        "PmmoJS.trigger(EventType.${1:BLOCK_BREAK}, event => {",
                        "  const typeId = event.getTypeId()",
                        "  const forgeEvent = event.getForgeEventClassName()",
                        "  console.info(`[PmmoJS] trigger \\${typeId} -> \\${forgeEvent}`)",
                        "  ",
                        "  // Access context data",
                        "  if (event.hasContextKey('damage')) {",
                        "    const dmg = event.getContextDouble('damage')",
                        "  }",
                        "  ",
                        "  // Add XP awards",
                        "  event.addXpAward('combat', 50)",
                        "  // Or set all XP awards at once",
                        "  event.setXpAwards({ mining: 10, crafting: 5 })",
                        "})"
                ), "PmmoJS trigger event handler with type-safe EventType");

        // Internal event handler snippet
        event.customSnippet("PmmoJS Internal Handler",
                List.of("internal_type_id"),
                List.of(
                        "PmmoJS.internal(PMMOInternalType.${1:POTION_BREW}, event => {",
                        "  if (event.getContextBoolean('alreadyTracked')) return",
                        "  ",
                        "  // Conditionally skip PMMO processing",
                        "  if (event.canCancelAction() && shouldSkip) {",
                        "    event.deny()",
                        "    return",
                        "  }",
                        "  ",
                        "  event.addXpAward('alchemy', 10)",
                        "  event.putContextString('alreadyTracked', 'true')",
                        "})"
                ), "PmmoJS internal event handler with skip/deny");

        // Settings batch snippet
        event.customSnippet("PmmoJS Settings Batch",
                List.of("pmmojs.settings"),
                List.of(
                        "PmmoJS.settings(event => {",
                        "  event.items(['minecraft:diamond_sword'], setting => {",
                        "    setting.override(true)",
                        "    setting.setRequirement(ReqType.WEAPON, 'combat', 20)",
                        "    setting.setXp(EventType.CRAFT, 'smithing', 150)",
                        "    setting.setXpBonus('combat', 1.1)",
                        "    setting.salvage('minecraft:diamond')",
                        "      .setChancePerLevel(0.005)",
                        "      .setLevelReq(10)",
                        "      .setXpAward('smithing', 50)",
                        "  })",
                        "  ",
                        "  event.entities(['minecraft:villager'], setting => {",
                        "    setting.override(true)",
                        "    setting.setRequirement(ReqType.ENTITY_INTERACT, 'charisma', 8)",
                        "  })",
                        "})"
                ), "PmmoJS batch settings with item salvage and entity requirements");

        // Perk lifecycle snippet
        event.customSnippet("PmmoJS Perk Lifecycle",
                List.of("perk_id_string"),
                List.of(
                        "PmmoJS.registerPerk(event => {",
                        "  event.create('${1:kubejs:my_perk}', PMMOPerkSide.SERVER)",
                        "    .withSkill('combat')",
                        "    .withCooldown(200)",
                        "    .withDuration(100)",
                        "    .withMinLevel(5)",
                        "    .withMilestones([5, 10, 20])",
                        "    .description('A custom perk registered from KubeJS.')",
                        "    .conditions(ctx => {",
                        "      return ctx.getPlayer().getHealth() < ctx.getPlayer().getMaxHealth()",
                        "    })",
                        "    .start(ctx => {",
                        "      ctx.getSettings().putBoolean('active', true)",
                        "    })",
                        "    .tick(ctx => {",
                        "      if (ctx.getElapsedTicks() % 20 === 0) {",
                        "        ctx.getPlayer().heal(1)",
                        "      }",
                        "    })",
                        "    .stop(ctx => {",
                        "      ctx.getSettings().putBoolean('active', false)",
                        "    })",
                        "    .status(ctx => {",
                        "      ctx.addLine(`Skill: \\${ctx.getSettings().getSkill()}`)",
                        "      ctx.addLine(`Level: \\${ctx.getSettings().getResolvedLevel()}`)",
                        "      ctx.addLine(`Cooldown: \\${ctx.getSettings().getCooldown()}t`)",
                        "      ctx.addLine(`Chance: \\${ctx.getSettings().getChance() * 100}%`)",
                        "    })",
                        "    .register()",
                        "})"
                ), "PmmoJS perk lifecycle with conditions, start/tick/stop, and status display");

        // Config events snippet
        event.customSnippet("PmmoJS Config Events",
                List.of("config_type"),
                List.of(
                        "// Server config",
                        "PmmoJS.serverConfig(event => {",
                        "  event.setMaxLevel(200)",
                        "  event.setVeinChargeCap(100)",
                        "  event.setVeinChargeRate(0.5)",
                        "  event.setDeathLossEnabled(true)",
                        "  event.setTreasureEnabled(true)",
                        "  event.setPartyRange(128)",
                        "})",
                        "",
                        "// Skill config",
                        "PmmoJS.skillsConfig(event => {",
                        "  event.addSkill('custom_skill', builder => {",
                        "    builder.withColor(0xFF5500)",
                        "    builder.withIcon('minecraft:diamond')",
                        "    builder.withMaxLevel(100)",
                        "    builder.withDisplayName('Custom Skill')",
                        "  })",
                        "})",
                        "",
                        "// Auto-value config",
                        "PmmoJS.autoValueConfig(event => {",
                        "  event.setAutoValuesEnabled(true)",
                        "  event.setRaritiesModifier(1.5)",
                        "  event.setHardnessModifier(0.8)",
                        "})",
                        "",
                        "// Anti-cheese config",
                        "PmmoJS.antiCheeseConfig(event => {",
                        "  event.setAfkCanSubtract(true)",
                        "  event.addAfkSetting(builder => {",
                        "    builder.source('movement')",
                        "    builder.minTime(6000)",
                        "    builder.reduction(0.5)",
                        "  })",
                        "})"
                ), "PmmoJS config event patterns (server, skills, auto-value, anti-cheese)");
    }

    private static List<Object> jsonChoices(List<String> ids) {
        return ids.stream()
                .sorted()
                .distinct()
                .map(ProbeJS.GSON::toJson)
                .map(Object.class::cast)
                .toList();
    }
}
