## Adding custom variant spawns/editing existing via datapack (Dragon Variant Spawner)
If you wish to override default variants spawns, you'll need to edit main file in isleofberk namespace and ship it with your data pack. Else just declare spawns under different namespace. You can look up example of variant declaration in example data pack. Full path to the file would look something like this: `data/your_namespace/dragon_variants/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

For example refer to Nether Dragons pack

### Fields:
- `name` - code name of the variant. Can be either specified as single name or list of names
- `collections` - list of variant collections that will be added as entries. Note: collections ignore dragon id specified for the file and instead will be using ones specified for lists contained in variant lists of those collections
- `weight` - declares a chance of certain variant appearing naturally (not via breeding). Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanApppearOnThisSpot`)
- `breeding_weight` - declares a chanse of a variant being applied via breeding. Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanBeObtainedViaBreeding`). If not specified, value defaults to one declared in `weight` field (optional)
- `banned_biomes` - declares biomes in which variant cannot appear under any circumstances. Basically a biome blacklist. Supports declaration via tags (optional) and biome IDs (optional)
  - `biome` - list of biome ids
  - `tag` - list of biome tags
- `allowed_biomes` - declares biomes where certain variant only can appear. Works like a whitelist. If not presented, variant will spawn in any biome it can. Supports declaration via tags (optional) and biome IDs (optional). If not specified and weight is over 0, variants will be able to spawn only in OW biomes
  - `biome` - list of biome ids
  - `tag` - list of biome tags
- `altitude` - defines on which range of world height variant can spawn. If not stated, variant will spawn on any world height. Supports declaration of minimum (optional) and maximum (optional) height
- `surface_restriction` - allows to specify if dragon has to be able to see sky or not in order to spawn. Possible values (if not specified, defaults to `none`):
  - `none` - no restrictions
  - `underground` - dragon has to not be able to see sky in order to spawn
  - `surface` - dragon has to be able to see sky in order to spawn
### Mandatory fields for each variant:
- `name` or `collections` - without specifying either of those entry will not be added
- `weight` - weight must be defined in order to variant to spawn

Additionally, you can specify to which dragon variant spawn or hitbox override belongs right within file. If done so, file name will be ignored. To do that, you have to add field called `dragon` outside `redirects` array. I.e.:
```json
{
  "dragon": "some_dragon",
  "variants": [
    ...
  ]
}
```
In this case, whatever file name is, this specific model redirect file will always belong to dragon with ID `some_dragon`.
Same rules for `dragon` field apply as for file name.

If entry of dragon variant spawner has several variants that can spawn, one of them will be chosen randomly with equal chance for each one.

Variant Loader comes with build-in dragon variant spawners for all base dragons (located in `data/isleofberk/dragon_variants`). Note: spawn odds do not match original ones in the mod. All of them (except `speed_stinger`, `speed_stinger_leader` and `stinger`) use variant collections for pools of different rarities (common, uncommon and rare). So unless your variants need to have some special conditions, consider adding your variant to those collections instead of adding entry directly (except for the exceptions)


## Modifying attack boxes, hit boxes and passenger positions per variant (Hitbox Redirects)
You can modify offsets for attack box (if dragon got one), hit box and passenger positions per variant. This will work only if dragon actual variant (nbt value `VariantName`) matches with one you defined, **renaming dragon will not work**. 
To make redirect for any of those, you have to declare them in `data/your_namespace/hitbox_redirects/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.
Similarly to `dragon_variants`, you as well can specify dragon to which it belongs to in `dragon` field in the file itself instead of file name.

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

For example refer to Hitbox Example pack

### Fields:
- `name` - code name of the variant. Can be either specified as single name or list of names
- `collections` - list of variant collections that will be added as entries. Note: collections ignore dragon id specified for the file and instead will be using ones specified for lists contained in variant lists of those collections
- `hitbox` - hit box override, has height and width fields (both mandatory). If not specified, default one will be used instead
- `attack_box` - attack box override, has height and width fields (both mandatory). If not specified, default one will be used instead
- `attack_box_position` - changes position of attack box relative to dragon's position, must be declared as array of 3 numbers (can be floating point ones, each number represents offsets on x, y and z axis relative to entity rotation respectively). If not specified, default offsets will be used instead
- `passenger_positions` - passenger positions offsets, must be declared as array containing arrays of 3 numbers (can be floating point ones, each number represents offsets on x, y and z axis relative to entity rotation respectively). First array will be used to change offset for first passenger, second for second and so on. If not specified, default offsets will be used instead
### Mandatory fields for each variant:
- `name` or `collections` - without specifying either of those entry will not be added

## Sound Redirects
Sound redirects allow you to redefine (most of) hardcoded sounds per variant, as well as add sounds to play for your sound keyframes that you added in animation. For reasons sounds having to be synced on both client and server, this has to be done in datapack.
To make sound redirect, you need to declare it in `data/your_namespace/sound_redirects/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.
Alternatively you can specify dragon to which it belongs to in `dragon` field in the file itself instead of file name.

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

For example refer to Sakura's Mixed Dragons pack.

### Fields
- `name` - code name of the variant. Can be either specified as single name or list of names
- `collections` - list of variant collections that will be added as entries. Note: collections ignore dragon id specified for the file and instead will be using ones specified for lists contained in variant lists of those collections
- Sound:
  - `pitch` - sound pitch. Field itself is not limited, but game's sound engine has some hardcoded limitations on how high or low it can go. Defaults to 1 if not specified
  - `volume` - from 0 to 1: defines how loud sound will be. From 1 and above - defines from how afar sound can be heard, i.e. volume of 1 means 16 blocks, volume of 2 means 32 and so on. This is how sound engine of the game interprets it. Defaults to 1 if not specified
### Mandatory fields for each variant:
- `name` or `collections` - without specifying either of those entry will not be added
- Sound:
  - `name` - name of sound keyframe or of hardcoded sound
  - `id` - ID of sound to be played

### Table of hardcoded sound names

| Name         | Dragons                                                                      | Description                                                                                                            |
|--------------|------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| bite         | All                                                                          | Bite attack sound                                                                                                      |
| sting        | Speed Stinger, Lead Speed Stinger, Triple Stryke, Deadly Nadder              | Sting attack sound, for Nadder - spike throw attack                                                                    |
| melee_attack | Triple Stryke                                                                | Sound of Triple Stryke attacking with its claw                                                                         |
| fire         | All dragons with breath attack                                               | Sound emitted when breath projectile is created                                                                        |
| fire_weak    | Terrible Terror, Night Fury, Light Fury, Nightlight, Gronckle, Triple Stryke | For Terrible Terror - played when it eats food it holds. For everyone else this sound is played when weak shot is made |
| tame         | All tameable dragons                                                         | Played when dragon is tamed                                                                                            |
| sleep        | All                                                                          | Idle sound, played when dragon is sleeping                                                                             |
| growl        | All                                                                          | Idle sound                                                                                                             |
| flap         | All flying dragons                                                           | Wing flap sound. Yes, that one                                                                                         |
| hurt         | All                                                                          | Played when dragon is taking damage                                                                                    |
| death        | All                                                                          | Played when dragon is dying                                                                                            |
| step         | All                                                                          | Played when dragon is walking on ground                                                                                |

## Extras
Extras are various miscellaneous things that can also be configured for variant.
To add extras for your dragon, declare your file in `data/your_namespace/extras/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.
Alternatively you can specify dragon to which it belongs to in `dragon` field in the file itself instead of file name.

For example refer to Sakura's Mixed Dragons pack.

### Fields
- `name` - code name of the variant. Can be either specified as single name or list of names
- `collections` - list of variant collections that will be added as entries. Note: collections ignore dragon id specified for the file and instead will be using ones specified for lists contained in variant lists of those collections
- `variant_group` - value of `VariantGroup` NBT. Recommended to be used for cases where you want to select multiple variants by NBT selector. This NBT field cannot be set via command, only by defining it in extras file for variant
- `loot_table_redirect` - ID of other loot table that can replace default one. If not specified, default loot table will be used
- `attribute_modifiers` - attribute modifiers for variant (such as health, armor, damage etc.). Each object has the following fields (all mandatory):
- Attribute modifier:
  - `id` - attribute registry id. Note: specifying same attribute more than once will not work properly and only one of modifiers will be applied
  - `amount` - amount on which attribute value will be modified. Exact behaviour depends on operation
  - `operation` - defines how exactly modifier will be applied. Following values are allowed: 
    - `"ADDITION"` - adds amount to base value
    - `"MULTIPLY_BASE"` - adds multiplied on specified amount base value
    - `"MULTIPLY_TOTAL"` - similar to "MULTIPLY_BASE", but goes after "MULTIPLY_BASE" got applied

- `taming_items` - items that can be used to tame the dragon. If not specified, items from default tag will be used. If this field is specified but has both empty lists, dragon cannot be tamed with any item
  - `item` - list of item ids (optional)
  - `tag` - list of item tags (without #) (optional)

- `breeding_items` - items that can be used to breed the dragon. If not specified, items from default tag will be used. If this field is specified but has both empty lists, dragon cannot be bred with any item
  - `item` - list of item ids (optional)
  - `tag` - list of item tags (without #) (optional)

- `custom_item_interactions` - items that can execute specified commands upon interaction. Specified as list. If several entries are matching on interaction, all of them will be executed (as long as you have enough items in stack for each of them). All fields (unless specified otherwise) are mandatory
  - `items` - items that can cause specified interaction
    - `item` - list of item ids (optional)
    - `tag` - list of item tags (without #) (optional)
  - `required_amount` - required amount of items in stack
  - `consume_on_use` - if items should be consumed on successful interaction
  - `can_interact_with_untamed` - if specified interaction can be performed on untamed dragon
  - `can_interact_if_not_owner`- if specified interaction can be performed on tamed dragon that is not owned by user
  - `commands` - list of commands that will be executed on successful interaction
  - Command:
    - `executed_command` - command to be executed. **Warning: all commands are executed with permission level of 2 (same as server operator)**
    - `executor` - whom will be considered executor of command (important for commands that use relative to executor position or target self). Following values are allowed:
      - `USER` - player, whom interacted with the dragon, will be the executor
      - `DRAGON` - dragon that is being interacted with will be the executor
      - `SERVER` - server itself will be the executor
### Mandatory fields for each variant:
- `name` or `collections` - without specifying either of those entry will not be added


## Variant Collections
Variant collections are collection of lists of variants. Additionally, they also can hold other collections if specified. All variant collections must be in `variant_collections` folder.

Name of the collection is defined by file name (including folders, starting from `variant_collections` as root). I.e. if collection full path is `data/isleofberk/variant_collections/skrill_rare`, collection name will be `skrill_rare`. If you place collection in subfolder, i.e. so full path would be `data/isleofberk/variant_collections/skrill/skrill_rare`, collection name will be `skrill/skrill_rare`.

If you want to add entry to existing collection, you need to make collection with same name **under your namespace**. If you make it under same namespace where that collection is originated from, it will result in collection file under this namespace to be overridden. If you do this without intention to actually override the file, *I'll slap you with a slipper personally*.

For example refer to Sakura's Mixed Dragons and AA Hybrid Breeding Example packs.

Following table below lists all build-in collections with how they're used in Variant Loader by default. All of listed collection are located under `isleofberk` namespace (full path `data/isleofberk/variant_collections`)

| Build-In Collection            | Description                                                                               |
|--------------------------------|-------------------------------------------------------------------------------------------|
| `intentionally_empty`          | Empty collection that is not used anywhere                                                |
| `deadly_nadder`                | All Deadly Nadder variants                                                                |
| `deadly_nadder_common`         | Common variants of Deadly Nadder. Used during natural spawn and in breeding lists         |
| `deadly_nadder_uncommon`*      | Uncommon variants of Deadly Nadder. Used during natural spawn and in breeding lists       |
| `deadly_nadder_rare`*          | Rare variants of Deadly Nadder. Used during natural spawn and in breeding lists           |
| `gronckle`                     | All Gronckle variants                                                                     |
| `gronckle_common`              | Common variants of Gronckle. Used during natural spawn and in breeding lists              |
| `gronckle_uncommon`*           | Uncommon variants of Gronckle. Used during natural spawn and in breeding lists            |
| `gronckle_rare`*               | Rare variants of Gronckle. Used during natural spawn and in breeding lists                |
| `light_fury`                   | All Light Fury variants                                                                   |
| `light_fury_common`            | Common variants of Light Fury. Used during natural spawn and in breeding lists            |
| `light_fury_uncommon`          | Uncommon variants of Light Fury. Used during natural spawn and in breeding lists          |
| `light_fury_rare`*             | Rare variants of Light Fury. Used during natural spawn and in breeding lists              |
| `monstrous_nightmare`          | All Monstrous Nightmare variants                                                          |
| `monstrous_nightmare_common`   | Common variants of Monstrous Nightmare. Used during natural spawn and in breeding lists   |
| `monstrous_nightmare_uncommon` | Uncommon variants of Monstrous Nightmare. Used during natural spawn and in breeding lists |
| `monstrous_nightmare_rare`     | Rare variants of Monstrous Nightmare. Used during natural spawn and in breeding lists     |
| `night_fury`                   | All Night Fury variants                                                                   |
| `night_fury_common`            | Common variants of Night Fury. Used during natural spawn and in breeding lists            |
| `night_fury_uncommon`*         | Uncommon variants of Night Fury. Used during natural spawn and in breeding lists          |
| `night_fury_rare`              | Rare variants of Night Fury. Used during natural spawn and in breeding lists              |
| `night_light`                  | All Night Light variants                                                                  |
| `night_light_common`           | Common variants of Night Light. Used during natural spawn and in breeding lists           |
| `night_light_uncommon`*        | Uncommon variants of Night Light. Used during natural spawn and in breeding lists         |
| `night_light_rare`*            | Rare variants of Night Light. Used during natural spawn and in breeding lists             |
| `skrill`                       | All Skrill variants                                                                       |
| `skrill_common`                | Common variants of Skrill. Used during natural spawn and in breeding lists                |
| `skrill_uncommon`              | Uncommon variants of Skrill. Used during natural spawn and in breeding lists              |
| `skrill_rare`                  | Rare variants of Skrill. Used during natural spawn and in breeding lists                  |
| `speed_stinger`                | All Speed Stinger variants                                                                |
| `speed_stinger_common`         | Common variants of Speed Stinger. Used in breeding lists                                  |
| `speed_stinger_uncommon`*      | Uncommon variants of Speed Stinger. Used in breeding lists                                |
| `speed_stinger_rare`*          | Rare variants of Speed Stinger. Used in breeding lists                                    |
| `speed_stinger_leader`         | All Leader Speed Stinger variants                                                         |
| `stinger`                      | All Stinger variants                                                                      |
| `stinger_common`               | Common variants of Stinger. Used in breeding lists                                        |
| `stinger_uncommon`             | Uncommon variants of Stinger. Used in breeding lists                                      |
| `stinger_rare`                 | Rare variants of Stinger. Used in breeding lists                                          |
| `terrible_terror`              | All Terrible Terror variants                                                              |
| `terrible_terror_common`       | Common variants of Terrible Terror. Used during natural spawn and in breeding lists       |
| `terrible_terror_uncommon`*    | Uncommon variants of Terrible Terror. Used during natural spawn and in breeding lists     |
| `terrible_terror_rare`*        | Rare variants of Terrible Terror. Used during natural spawn and in breeding lists         |
| `triple_stryke`                | All Triple Stryke variants                                                                |
| `triple_stryke_common`         | Common variants of Triple Stryke. Used during natural spawn and in breeding lists         |
| `triple_stryke_uncommon`       | Uncommon variants of Triple Stryke. Used during natural spawn and in breeding lists       |
| `triple_stryke_rare`           | Rare variants of Triple Stryke. Used during natural spawn and in breeding lists           |
| `zippleback`                   | All Zippleback variants                                                                   |
| `zippleback_common`            | Common variants of Zippleback. Used during natural spawn and in breeding lists            |
| `zippleback_uncommon`*         | Uncommon variants of Zippleback. Used during natural spawn and in breeding lists          |
| `zippleback_rare`*             | Rare variants of Zippleback. Used during natural spawn and in breeding lists              |

`*` - those collections do not have build-in file and technically considered to be empty, but are used for spawning or/and breeding in other build-in files

### Fields
- `variant_lists` - list of variant lists
- Variant List:
  - `dragon` - id of dragon species
  - `variants` - list of code variant names
- `collections` - list of names of other variant collections that will be included in this collection

## Breeding Lists
Breeding lists are collection of variant lists that can be obtained via breeding specified pair of parents. When breeding, dragons will automatically look up for matching lists (for a match parent dragon needs to match at least one entry in parents listing). By default, Variant Loader will prioritize usage of breeding lists and use fallback system (taking entries from dragon variant spawners) when it cannot find pair matching any breeding list. This behaviour can be configured in config

File name for breeding list does not matter, so you can name it as you please. Just place all of them in `breeding_lists` folder.

For example refer to Sakura's Mixed Dragons and AA Hybrid Breeding Example packs.

There are build-in breeding lists in mod under `isleofberk` namespace (full path `data/isleofberk/breeding_lists`) for each one of dragons and one for producing Night Lights from breeding Light and Night Furies. All of breeding lists utilize variant collections for their pools (you can check names of collections in paragraph about Variant Collections)

### Fields
- `parents` - list of 2, containing variant lists and collections acceptable for each of their parents
- Parent:
  - `variant_lists` - list of variant lists
  - Variant List:
    - `dragon` - id of dragon species
    - `variants` - list of code variant names
  - `collections` - list of names of variant collections
- `entries` - possible outcomes for breeding result
- Entry:
  - `variant_lists` - list of variant lists
  - Variant List:
    - `dragon` - id of dragon species
    - `variants` - list of code variant names
  - `collections` - list of names of variant collections
  - `weight` - how much weight each entry has, weight affects final chance of entry being chosen (higher the weight, higher the chance)

If entry has several possible variants as outcome, one of them will be chosen randomly with equal chance for each one