# 0.4.12

## Highlights

- Added a Forge common config for disabling PMMO default data before KubeJS customizations run.
- Added broader PMMO runtime bridges, including trigger-registry hooks and internal handler hooks.
- Expanded startup perk registration with clearer lifecycle surfaces and JS-friendly tag access.
- Reworked the documentation so the published pages match the current workflow and include full lifecycle examples.

## Added

- `config/pmmojs-common.toml` support through `disableDefaultSettings.*` toggles.
- `PmmoJS.internal(...)` with `PMMOInternalType` support for:
  `dimensionTravel`, `explosion`, `login`, `mount`, `piston`, `playerDeath`, `potionBrew`, and `sleepFinished`.
- `PmmoJS.trigger(...)` bridge registration across PMMO `EventType` handlers with mutable runtime context.
- `xpBySkill` event routing for skill-scoped XP listeners.
- JS-friendly startup perk lifecycle contexts:
  `PerkConditionContextJS`, `PerkStartContextJS`, `PerkTickContextJS`, `PerkStopContextJS`, and `PerkStatusContextJS`.
- Startup perk default-tag helpers such as `defaults(...)`, `withMilestones(...)`, `withStringList(...)`, `withNumberList(...)`, and `withCompound(...)`.
- Unit coverage for default-setting policy behavior with JUnit 5.

## Changed

- Default PMMO skill, perk, requirement, XP-award, item-extra, and block-vein data can now be cleared before pack scripts apply custom rules.
- PMMO settings builders now accept more flexible targets and batch application patterns for items, blocks, entities, biomes, dimensions, and generic object types.
- PMMO config mutation now runs on the server/static-data reload path so it stays aligned with config reloads and recipe-viewer tag sync behavior.
- Startup perk registration now exposes clearer description, status, and lifecycle APIs for KubeJS scripts.
- The documentation now explains reset strategy, tag flow, startup perk lifecycle, internal hooks, and current script entrypoints in both English and Chinese.

## Fixed

- Removed published documentation references to local filesystem paths.
- Fixed legacy `clearVanillaItemSettings()`, `clearVanillaBlockSettings()`, and `clearVanillaEntitySettings()` so they only reset requirement data for their own object category.
- Fixed default-reset ordering so pack scripts can rebuild custom PMMO data after defaults are suppressed.
- Fixed stale documentation that still pointed to the removed reset example bundle.

## Docs

- Added a dedicated reset/defaults guide with common-config usage and script rebuild patterns.
- Added full perk lifecycle walkthroughs, tag snapshots, and event-output examples.
- Replaced placeholder homepages with real entrypoint pages and aligned sidebar metadata with the actual doc tree.

## Notes

- Biome and dimension settings still do not have a blanket global default-wipe switch. Those remain explicit, script-driven overrides.
- The old reset example bundle is no longer part of the workflow. Use the documented snippets in your own `server_scripts/`.
