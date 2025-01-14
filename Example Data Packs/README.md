## Adding custom variant spawns/editing existing via datapack
If you wish to override default variants spawns, you'll need to edit main file in isleofberk namespace and ship it with your data pack. Else just declare spawns under different namespace. You can look up example of variant declaration in example data pack. Full path to the file would look something like this: "data/\*your\_namespace\*/dragon\_variants/\*dragon_id\*.json", where dragon_id is dragon's in game id. I.e. for Night Fury it'd be night_fury.

### Fields:
- `weight` - declares a chance of certain variant appearing naturally (not via breeding). Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanApppearOnThisSpot`)
- `breeding_weight` - declares a chanse of a variant being applied via breeding. Higher the weight, higher the chances (formula for a chance looks like something like this: `variantWeight / totalWeightOfAllVariantsThatCanBeObtainedViaBreeding`). If not specified, value defaults to one declared in `weight` field (optional)
- `banned_biomes` - declares biomes in which variant cannot appear under any circumstances. Basically a biome blacklist. Supports declaration via tags (optional) and biome IDs (optional)
- `allowed_biomes` - declares biomes where certain variant only can appear. Works like a whitelist. If not presented, variant will spawn in any biome it can. Supports declaration via tags (optional) and biome IDs (optional). If not specified and weight is over 0, variants will be able to spawn only in OW biomes
- `altitude` - defines on which range of world height variant can spawn. If not stated, variant will spawn on any world height. Supports declaration of minimum (optional) and maximum (optional) height
### Mandatory fields for each variant:
- `name` - name of the variant. Texture file name must correspond to the variant name in order to work correctly
- `weight` - weight must be defined in order to variant to spawn

## Modifying attack boxes, hit boxes and passenger positions per variant
You can modify offsets for attack box (if dragon got one), hit box and passenger positions per variant. This will work only if dragon actual variant (nbt value `VariantName`) matches with one you defined. 