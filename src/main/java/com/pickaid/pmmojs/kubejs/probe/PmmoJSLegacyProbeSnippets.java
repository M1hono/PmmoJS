package com.pickaid.pmmojs.kubejs.probe;

import com.pickaid.pmmojs.utils.PmmoHelper;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.snippet.Snippet;
import zzzank.probejs.lang.snippet.SnippetDump;

import java.util.List;
import java.util.Locale;

final class PmmoJSLegacyProbeSnippets {
    private static final String DEFAULT_ITEM_ID = "minecraft:diamond_sword";
    private static final String DEFAULT_ENTITY_ID = "minecraft:zombie";
    private static final String DEFAULT_BLOCK_ID = "minecraft:stone";

    private PmmoJSLegacyProbeSnippets() {
    }

    static void addSnippets(SnippetDump dump) {
        addSpecialTypeChoiceSnippets(dump);
        addTriggerHandlerSnippet(dump);
        addInternalHandlerSnippet(dump);
        addSettingsSnippet(dump);
        addPerkLifecycleSnippet(dump);
        addConfigSnippet(dump);
    }

    private static void addSpecialTypeChoiceSnippets(SnippetDump dump) {
        addChoiceSnippet(dump, "PmmoEventType", "pmmo_event_type",
                PmmoHelper.getEventTypeIds(), "PmmoJS EventType enum values");
        addChoiceSnippet(dump, "PmmoTriggerType", "pmmo_trigger_type",
                PmmoHelper.getTriggerTypeIds(), "PmmoJS trigger hook type IDs");
        addChoiceSnippet(dump, "PmmoInternalType", "pmmo_internal_type",
                PmmoHelper.getInternalTypeIds(), "PmmoJS internal hook type IDs");
        addChoiceSnippet(dump, "PmmoReqType", "pmmo_req_type",
                PmmoHelper.getReqTypeIds(), "PmmoJS requirement type values");
        addChoiceSnippet(dump, "PmmoItemReqType", "pmmo_item_req_type",
                PmmoHelper.getItemReqTypeIds(), "PmmoJS item-eligible requirement types");
        addChoiceSnippet(dump, "PmmoBlockReqType", "pmmo_block_req_type",
                PmmoHelper.getBlockReqTypeIds(), "PmmoJS block-eligible requirement types");
        addChoiceSnippet(dump, "PmmoEntityReqType", "pmmo_entity_req_type",
                PmmoHelper.getEntityReqTypeIds(), "PmmoJS entity-eligible requirement types");
        addChoiceSnippet(dump, "PmmoObjectType", "pmmo_object_type",
                PmmoHelper.getObjectTypeIds(), "PmmoJS object type values");
        addChoiceSnippet(dump, "PmmoPerkSide", "pmmo_perk_side",
                List.of("SERVER", "CLIENT", "BOTH"), "PmmoJS perk side values");
    }

    private static void addTriggerHandlerSnippet(SnippetDump dump) {
        Snippet snippet = dump.snippet("PmmoJSTriggerHandler")
                .prefix("@pmmo_trigger")
                .prefix("@pmmojs_trigger")
                .description("PmmoJS trigger event handler with type-safe EventType");
        snippet.literal("PmmoJS.trigger(EventType.")
                .tabStop(1, "BLOCK_BREAK")
                .literal(", event => {")
                .newline().literal("  const typeId = event.getTypeId()")
                .newline().literal("  const player = event.getPlayer()")
                .newline().literal("  ")
                .newline().literal("  // Access context data")
                .newline().literal("  if (event.hasContextKey('damage')) {")
                .newline().literal("    const dmg = event.getContextDouble('damage')")
                .newline().literal("  }")
                .newline().literal("  ")
                .newline().literal("  // Add XP awards")
                .newline().literal("  event.addXpAward('combat', 50)")
                .newline().literal("  ")
                .newline().literal("  // Cancel PMMO processing for this trigger")
                .newline().literal("  // event.setCancelled(true)")
                .newline().literal("})")
                .tabStop(0);
    }

    private static void addInternalHandlerSnippet(SnippetDump dump) {
        Snippet snippet = dump.snippet("PmmoJSInternalHandler")
                .prefix("@pmmo_internal")
                .prefix("@pmmojs_internal")
                .description("PmmoJS internal event handler with skip/deny");
        snippet.literal("PmmoJS.internal(PMMOInternalType.")
                .tabStop(1, "POTION_BREW")
                .literal(", event => {")
                .newline().literal("  if (event.getContextBoolean('alreadyTracked')) return")
                .newline().literal("  ")
                .newline().literal("  // Conditionally skip PMMO processing")
                .newline().literal("  if (event.canCancelAction() && shouldSkip) {")
                .newline().literal("    event.deny()")
                .newline().literal("    return")
                .newline().literal("  }")
                .newline().literal("  ")
                .newline().literal("  event.addXpAward('alchemy', 10)")
                .newline().literal("  event.putContextString('alreadyTracked', 'true')")
                .newline().literal("})")
                .tabStop(0);
    }

    private static void addSettingsSnippet(SnippetDump dump) {
        Snippet snippet = dump.snippet("PmmoJSSettingsBatch")
                .prefix("@pmmo_settings")
                .prefix("@pmmojs_settings")
                .description("PmmoJS batch settings with item salvage and entity requirements");
        snippet.literal("PmmoJS.settings(event => {")
                .newline().literal("  event.items(['")
                .tabStop(1, DEFAULT_ITEM_ID)
                .literal("'], setting => {")
                .newline().literal("    setting.override(true)")
                .newline().literal("    setting.setRequirement(ReqType.WEAPON, 'combat', 20)")
                .newline().literal("    setting.setXp(EventType.CRAFT, 'smithing', 150)")
                .newline().literal("    setting.setXpBonus('combat', 1.1)")
                .newline().literal("    setting.salvage('minecraft:diamond')")
                .newline().literal("      .setChancePerLevel(0.005)")
                .newline().literal("      .setLevelReq(10)")
                .newline().literal("      .setXpAward('smithing', 50)")
                .newline().literal("  })")
                .newline().literal("  ")
                .newline().literal("  event.entities(['")
                .tabStop(2, DEFAULT_ENTITY_ID)
                .literal("'], setting => {")
                .newline().literal("    setting.override(true)")
                .newline().literal("    setting.setRequirement(ReqType.ENTITY_INTERACT, 'charisma', 8)")
                .newline().literal("  })")
                .newline().literal("})")
                .tabStop(0);
    }

    private static void addPerkLifecycleSnippet(SnippetDump dump) {
        Snippet snippet = dump.snippet("PmmoJSPerkLifecycle")
                .prefix("@pmmo_perk")
                .prefix("@pmmojs_perk")
                .description("PmmoJS perk lifecycle with conditions, start/tick/stop, and status display");
        snippet.literal("PmmoJS.registerPerk(event => {")
                .newline().literal("  event.create('")
                .tabStop(1, "kubejs:my_perk")
                .literal("', PMMOPerkSide.SERVER)")
                .newline().literal("    .withSkill('combat')")
                .newline().literal("    .withCooldown(200)")
                .newline().literal("    .withDuration(100)")
                .newline().literal("    .withMinLevel(5)")
                .newline().literal("    .withMilestones([5, 10, 20])")
                .newline().literal("    .description('A custom perk registered from KubeJS.')")
                .newline().literal("    .conditions(ctx => {")
                .newline().literal("      return ctx.getPlayer().getHealth() < ctx.getPlayer().getMaxHealth()")
                .newline().literal("    })")
                .newline().literal("    .start(ctx => {")
                .newline().literal("      ctx.getSettings().putBoolean('active', true)")
                .newline().literal("    })")
                .newline().literal("    .tick(ctx => {")
                .newline().literal("      if (ctx.getElapsedTicks() % 20 === 0) {")
                .newline().literal("        ctx.getPlayer().heal(1)")
                .newline().literal("      }")
                .newline().literal("    })")
                .newline().literal("    .stop(ctx => {")
                .newline().literal("      ctx.getSettings().putBoolean('active', false)")
                .newline().literal("    })")
                .newline().literal("    .status(ctx => {")
                .newline().literal("      ctx.addLine(`Skill: \\${ctx.getSettings().getSkill()}`)")
                .newline().literal("      ctx.addLine(`Level: \\${ctx.getSettings().getResolvedLevel()}`)")
                .newline().literal("      ctx.addLine(`Cooldown: \\${ctx.getSettings().getCooldown()}t`)")
                .newline().literal("      ctx.addLine(`Chance: \\${ctx.getSettings().getChance() * 100}%`)")
                .newline().literal("    })")
                .newline().literal("    .register()")
                .newline().literal("})")
                .tabStop(0);
    }

    private static void addConfigSnippet(SnippetDump dump) {
        Snippet snippet = dump.snippet("PmmoJSConfigEvents")
                .prefix("@pmmo_config")
                .prefix("@pmmojs_config")
                .description("PmmoJS config event patterns (server, skills, auto-value, anti-cheese)");
        snippet.literal("// Server config")
                .newline().literal("PmmoJS.serverConfig(event => {")
                .newline().literal("  event.setMaxLevel(200)")
                .newline().literal("  event.setVeinChargeCap(100)")
                .newline().literal("  event.setVeinChargeRate(0.5)")
                .newline().literal("  event.setDeathLossEnabled(true)")
                .newline().literal("  event.setTreasureEnabled(true)")
                .newline().literal("  event.setPartyRange(128)")
                .newline().literal("})")
                .newline().literal("")
                .newline().literal("// Skill config")
                .newline().literal("PmmoJS.skillsConfig(event => {")
                .newline().literal("  event.addSkill('custom_skill', builder => {")
                .newline().literal("    builder.withColor(0xFF5500)")
                .newline().literal("    builder.withIcon('minecraft:diamond')")
                .newline().literal("    builder.withMaxLevel(100)")
                .newline().literal("    builder.withDisplayName('Custom Skill')")
                .newline().literal("  })")
                .newline().literal("})")
                .newline().literal("")
                .newline().literal("// Auto-value config")
                .newline().literal("PmmoJS.autoValueConfig(event => {")
                .newline().literal("  event.setAutoValuesEnabled(true)")
                .newline().literal("  event.setRaritiesModifier(1.5)")
                .newline().literal("  event.setHardnessModifier(0.8)")
                .newline().literal("})")
                .newline().literal("")
                .newline().literal("// Anti-cheese config")
                .newline().literal("PmmoJS.antiCheeseConfig(event => {")
                .newline().literal("  event.setAfkCanSubtract(true)")
                .newline().literal("  event.addAfkSetting(builder => {")
                .newline().literal("    builder.source('movement')")
                .newline().literal("    builder.minTime(6000)")
                .newline().literal("    builder.reduction(0.5)")
                .newline().literal("  })")
                .newline().literal("})")
                .tabStop(0);
    }

    private static void addChoiceSnippet(SnippetDump dump, String name, String prefix,
                                         List<String> ids, String description) {
        Snippet snippet = dump.snippet(name)
                .prefix("@" + prefix)
                .description(description);
        List<String> choices = jsonChoices(ids);
        if (choices.isEmpty()) {
            snippet.tabStop(1, ProbeJS.GSON.toJson("placeholder"));
        } else {
            snippet.choices(choices);
        }
    }

    private static List<String> jsonChoices(List<String> ids) {
        return ids.stream()
                .sorted()
                .distinct()
                .map(ProbeJS.GSON::toJson)
                .toList();
    }

    private static String toSnakeCase(String value) {
        return value
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z\\d])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
    }
}
