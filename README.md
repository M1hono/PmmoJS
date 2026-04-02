<div align="center">
  <img src="https://docs.mihono.cn/logo.png" width="200" height="200" alt="PmmoJS logo" />

# PmmoJS

KubeJS integration for Project MMO on Minecraft 1.20.1 Forge.
</div>

`PmmoJS` exposes PMMO settings, config hooks, runtime events, startup registries, and direct helper access to PMMO internals so packs can script Project MMO behavior without shipping a separate compatibility mod for each tweak.

## What It Covers

- Server-side PMMO object settings for items, blocks, entities, biomes, and dimensions
- PMMO config mutation hooks for globals, skills, perks, server config, auto-values, and anti-cheese
- Runtime gameplay hooks for XP, enchanting, furnace output, salvage, damage penalties, and all PMMO `EventType` trigger callbacks through `PmmoJS.trigger`
- Startup hooks for custom PMMO predicates and custom perk registration
- Utility bindings such as `PmmoHelper`, `SkillHelper`, `CustomReqMap`, `TagBuilder`, `NbtPathBuilder`, `SalvageBuilder`, and PMMO enums

## Supported Stack

- Minecraft `1.20.1`
- Forge `47.x`
- Project MMO `1.7.40` for `1.20.1`
- KubeJS `2001.6.x`

For local regression testing this workspace also includes EMI `1.1.22+1.20.1+forge` as a runtime-only dependency. It is not linked into the mod at compile time.

## Documentation

The authored wiki pages live in [`docs`](./docs):

- [`docs/overview.mdx`](./docs/overview.mdx)
- [`docs/tag-model.mdx`](./docs/tag-model.mdx)
- [`docs/startupevents/registry.mdx`](./docs/startupevents/registry.mdx)
- [`docs/serverevents/internal.mdx`](./docs/serverevents/internal.mdx)
- [`docs/serverevents/settings.mdx`](./docs/serverevents/settings.mdx)
- [`docs/serverevents/config.mdx`](./docs/serverevents/config.mdx)
- [`docs/serverevents/reset-recipes.mdx`](./docs/serverevents/reset-recipes.mdx)
- [`docs/serverevents/normal.mdx`](./docs/serverevents/normal.mdx)

## Common Config

Global PMMO default-data suppression now lives in `config/pmmojs-common.toml`.

Available switches:

- `disableDefaultSettings.all`
- `disableDefaultSettings.skills`
- `disableDefaultSettings.perks`
- `disableDefaultSettings.requirements`
- `disableDefaultSettings.xpAwards`
- `disableDefaultSettings.itemExtras`
- `disableDefaultSettings.blockVeinData`

The old reset example bundle is no longer part of the published workflow. Use the reset documentation page and place the snippets you keep in your own `server_scripts/`.

## Development Notes

- PMMO config mutations are applied from the server/static-data reload path, not the client tag sync path.
- EMI is present only to reproduce and validate recipe-viewer sync/indexing issues.
- Startup bindings: `PmmoHelper`, `TagBuilder`, `NbtPathBuilder`
- Server bindings: `PmmoHelper`, `SkillHelper`, `SKillHelper`, `CustomReqMap`, `TagBuilder`, `NbtPathBuilder`, `SalvageBuilder`
- Client bindings: read-only `SkillHelper`, `SKillHelper`
