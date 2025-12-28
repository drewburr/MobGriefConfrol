# MobGriefControl

A comprehensive Minecraft plugin that provides **granular control over mob griefing behaviors**. This directly addresses [this feedback](https://feedback.minecraft.net/hc/en-us/community/posts/24147860626445-More-granular-control-of-mob-griefing) on the Minecraft Forum.

Unlike Minecraft's single `mobGriefing` gamerule that affects all mobs at once, MobGriefControl lets you toggle specific mob behaviors individually, giving you precise control over your server's gameplay experience.

## Features

MobGriefControl provides independent toggles for **14 different mob behaviors**:

### Explosive Mobs

- **Creeper Explosions** - Prevent creepers from destroying blocks
- **Ghast Fireballs** - Prevent ghast explosions from damaging terrain
- **Wither** - Prevent wither explosions from destroying blocks
- **End Crystals** - Control End Crystal explosions (Bedrock parity feature)

### Block Manipulation

- **Enderman Block Pickup** - Stop endermen from picking up and placing blocks
- **Ender Dragon** - Prevent the dragon from destroying blocks during battle
- **Silverfish Infesting** - Stop silverfish from hiding in and emerging from blocks

### Entity Interactions

- **Zombie Door Breaking** - Control whether zombies can break down doors
- **Villager Farming** - Toggle villagers picking up and farming crops
- **Sheep Eating Grass** - Control sheep consuming grass blocks
- **Rabbit Crop Eating** - Prevent rabbits from eating carrots and crops
- **Fox Item Pickup** - Stop foxes from picking up items

### Environmental Effects

- **Snow Golem Trails** - Control whether snow golems leave snow trails
- **Ravager Crop Destruction** - Prevent ravagers from trampling crops

## Usage

### Commands

**Main command:** `/mobgriefcontrol` or `/mobgrief`

**View current settings:**

```bash
/mobgriefcontrol config
```

**Toggle specific behaviors:**

```bash
/mobgriefcontrol do_enderman_pickup <true/false>
/mobgriefcontrol do_creeper_explode <true/false>
/mobgriefcontrol do_ghast_explode <true/false>
/mobgriefcontrol do_wither_explode <true/false>
/mobgriefcontrol do_dragon_destroy <true/false>
/mobgriefcontrol do_zombie_break_doors <true/false>
/mobgriefcontrol do_villager_farm <true/false>
/mobgriefcontrol do_sheep_eat_grass <true/false>
/mobgriefcontrol do_rabbit_eat_crops <true/false>
/mobgriefcontrol do_fox_pickup_items <true/false>
/mobgriefcontrol do_snowgolem_snow_trail <true/false>
/mobgriefcontrol do_silverfish_infest_blocks <true/false>
/mobgriefcontrol do_ravager_destroy_crops <true/false>
/mobgriefcontrol do_endcrystal_explode <true/false>
```

**Other commands:**

```bash
/mobgriefcontrol help - Show all available commands
/mobgriefcontrol reload - Reload the configuration
/mobgriefcontrol toggledebug - Toggle debug mode (OP only)
```

### Permissions

- `mobgriefcontrol.admin` - Access to all commands
- `mobgriefcontrol.op` - OP-level access
- `mobgriefcontrol.toggledebug` - Access to debug toggle

## Configuration

By default, **all mob griefing behaviors are permitted** (set to `true`). This means mobs will have normal behavir until you choose to disable them.

You can edit `plugins/MobGriefControl/config.yml` directly or use in-game commands to configure behavior.

Example configuration:

```yaml
version: 2.0.1
debug: false
lang: en_US
auto_update_check: true

# Mob griefing controls (false = disabled, true = enabled)
do_enderman_pickup: true
do_creeper_explode: true
do_ghast_explode: true
do_wither_explode: true
do_dragon_destroy: true
do_zombie_break_doors: true
do_villager_farm: true
do_sheep_eat_grass: true
do_rabbit_eat_crops: true
do_fox_pickup_items: true
do_snowgolem_snow_trail: true
do_silverfish_infest_blocks: true
do_ravager_destroy_crops: true
do_endcrystal_explode: true
```

## Requirements

- **Minecraft Version:** 1.14+
- **Server Software:** Paper 1.20.4+ (Spigot/Paper/Purpur)
- **Java:** 21+

## Installation

1. Download the latest release from [Releases](../../releases)
2. Place the `.jar` file in your server's `plugins/` folder
3. Restart your server
4. Configure settings using `/mobgriefcontrol` commands or edit `config.yml`

## Building from Source

```bash
./gradlew clean build
```

The compiled JAR will be in `build/libs/`

## Why MobGriefControl?

Minecraft's `mobGriefing` gamerule is all-or-nothing: it either enables all mob griefing behaviors or disables them all. This means you can't have villagers farm crops without also allowing creepers to blow up your builds.

MobGriefControl solves this by letting you enable or disable each behavior independently, giving you complete control over how mobs interact with your world.

## Download Links

- [SpigotMC](https://www.spigotmc.org/resources/no-enderman-grief2.71236/)
- [BukkitDev](https://dev.bukkit.org/projects/no-enderman-grief2)

## Credits

Originally created by JoelGodOfwar. See [NoEndermanGrief](https://github.com/JoelGodOfwar/NoEndermanGrief).
