## Adding custom variant spawns/editing existing via datapack
If you wish to override default variants spawns, you'll need to edit main file in isleofberk namespace and ship it with your data pack. Else just declare spawns under different namespace. You can look up example of variant declaration in example data pack. Full path to the file would look something like this: `data/your_namespace/dragon_variants/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

For example refer to Nether Dragons pack

### Fields:
- `weight` - declares a chance of certain variant appearing naturally (not via breeding). Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanApppearOnThisSpot`)
- `breeding_weight` - declares a chanse of a variant being applied via breeding. Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanBeObtainedViaBreeding`). If not specified, value defaults to one declared in `weight` field (optional)
- `banned_biomes` - declares biomes in which variant cannot appear under any circumstances. Basically a biome blacklist. Supports declaration via tags (optional) and biome IDs (optional)
- `allowed_biomes` - declares biomes where certain variant only can appear. Works like a whitelist. If not presented, variant will spawn in any biome it can. Supports declaration via tags (optional) and biome IDs (optional). If not specified and weight is over 0, variants will be able to spawn only in OW biomes
- `altitude` - defines on which range of world height variant can spawn. If not stated, variant will spawn on any world height. Supports declaration of minimum (optional) and maximum (optional) height
- `surface_restriction` - allows to specify if dragon has to be able to see sky or not in order to spawn. Possible values (if not specified, defaults to `none`):
  - `none` - no restrictions
  - `underground` - dragon has to not be able to see sky in order to spawn
  - `surface` - dragon has to be able to see sky in order to spawn
### Mandatory fields for each variant:
- `name` - name of the variant. Texture file name must correspond to the variant name in order to work correctly
- `weight` - weight must be defined in order to variant to spawn

## Modifying attack boxes, hit boxes and passenger positions per variant
You can modify offsets for attack box (if dragon got one), hit box and passenger positions per variant. This will work only if dragon actual variant (nbt value `VariantName`) matches with one you defined, **renaming dragon will not work**. 
To make redirect for any of those, you have to declare them in `data/your_namespace/hitbox_redirects/dragon_id.json`, where `dragon_id` is dragon's in game id. I.e. for Night Fury it'd be night_fury, and `your_namespace` is unique name for your folder, that contains only `a-z`, `0-9` or `_`.

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

For example refer to Hitbox Example pack

### Fields:
- `hitbox` - hit box override, has height and width fields (both mandatory). If not specified, default one will be used instead
- `attack_box` - attack box override, has height and width fields (both mandatory). If not specified, default one will be used instead
- `attack_box_position` - changes position of attack box relative to dragon's position, must be declared as array of 3 numbers (can be floating point ones, each number represents offsets on x, y and z axis relative to entity rotation respectively). If not specified, default offsets will be used instead
- `passenger_positions` - passenger positions offsets, must be declared as array containing arrays of 3 numbers (can be floating point ones, each number represents offsets on x, y and z axis relative to entity rotation respectively). First array will be used to change offset for first passenger, second for second and so on. If not specified, default offsets will be used instead
### Mandatory fields for each variant:
- `name` - name of the variant