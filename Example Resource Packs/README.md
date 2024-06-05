## Summary
Both packs use different file structure and both are correct ways to make resource packs for Variant Loader.
All packs are added with the permission of their authors

## Fields
- `name` - name of the variant. Must be specified for every variant
- `texture` - dragon's texture. If not specified, mod will try to grab texture respective to dragon's variant name from its respective base folder. If not presented, placeholder texture will be used
- `model` - dragon's model file. If not specified, default one from the base mod will be used
- `animation` - dragon's animation model file. If not specified, default one from the base mod will be used
- `saddle` - dragon's saddle texture. If not specified, default one from the base mod will be used
- `egg_texture` - egg's entity texture. If not specified, default one from the base mod will be used
- `egg_model` - egg's entity model file. If not specified, default one from the base mod will be used
- `egg_item_model` - model of egg in item form. Must be specified as location of item model. If not specified, default one from the base mod will be used
- `dragon_name` - localisation key that will be used if dragon has no custom name. If not specified, default one from the base mod will be used
- `egg_name` - localisation key that will be used if egg entity has no custom name. If not specified, default one from the base mod will be used
- `egg_item_name` - localisation key that will be used if egg item has no custom name. If not specified, default one from the base mod will be used
- `nametag_accessible` - boolean value, defines if variant can be displayed on dragon if its custom name matches variant name (not case-sensitive). If not specified, defaults to true. Eggs cannot display variant from name and thus are not affected by this toggle.

Textures, models and animation can be eiter specified as file names if they are located in correct folders already (respective (almost) to the original mod asset structure, for example see Nether Dragons example pack) or as locations of those files (see Sakura's Mixed Dragons example pack).

## Model redirects
Model redirect files should go in `model_redirects` folder under any custom namespace. 
WARNING: if you add model redirect files under same namespace in your resource pack as someone else's or `isleofberk` one, this will cause an override of model redirect files under those namespaces. If this is not your intention, **don't do that**.

Model redirect file names should be respective to their in-game dragon's ID with small exception in face of Monstrous Nightmare (blame Isle of Berk's name inconsistency, it's actually horrible).

| Dragon              | File name                   |
|---------------------|-----------------------------|
| Deadly Nadder       | `deadly_nadder.json`        |
| Gronckle            | `gronckle.json`             |
| Light Fury          | `light_fury.json`           |
| Monstrous Nightmare | `nightmare.json`            |
| Night Fury          | `night_fury.json`           |
| Night Light         | `night_light.json`          |
| Skrill              | `skrill.json`               |
| Speed Stinger       | `speed_stinger.json`        |
| Lead Speed Stinger  | `speed_stinger_leader.json` |
| Stinger             | `stinger.json`              |
| Terrible Terror     | `terrible_terror.json`      |
| Triple Stryke       | `triple_stryke.json`        |
| Hideous Zippleback  | `zippleback.json`           |

## Glowing layer and Deadly Nadder's membranes
To add glowing layer to either dragon or egg, just place texture with parts you want to glow in the same folder as main texture file and named same as main texture file, but with postfix `_glowing`. I.e. if dragon's variant name is `red`, main texture file name will be `red.png`. To add glowing layer, you need to place texture named `red_glowing.png` alongside it.

Similar story regarding Deadly Nadder's membrane texture, you need to add `_membranes` postfix and place alongside main texture (i.e. `red_membranes.png`). To add glowing layer to membranes, use `_membranes_glowing` (i.e. `red_membranes_glowing.png`).

## Summoning dragon of specific variant
To summon a dragon or egg of specific variant, use `/isleofberk:dragon_or_egg_id ~ ~ ~ {VariantName:name}`, where `isleofberk:dragon_or_egg_id` - in-game id of either egg or dragon from the Isle of Berk mod you want to summon, `name` - name of the variant. 
Alternatively, if `nametag_accessible` for specific variant wasn't set to `false` for a variant you want and `disable_named_variants` client config option isn't set to `true`, you can use name tag to display variant you want. Using name tag to display variant won't override actual dragon's variant (which is stored in `VariantName` NBT). Only works with dragons

## Autogenerating translation keys
By setting `generate_translations` in client config to `true`, you enable translation key autogenerator. It'll autogenerate localisation keys for eggs and dragons and print them in log file for all variants that have model redirects (yes, even if model redirect contains only a name) on each resource reload.