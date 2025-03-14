## Summary
Both packs use different file structure and both are correct ways to make resource packs for Variant Loader.
All packs are added with the permission of their authors

## Fields
- `name` - name of the variant. Must be specified for every variant
- `texture` - dragon's texture. If not specified, mod will try to grab texture respective to dragon's variant name from its respective base folder. If not presented, placeholder texture will be used
- `model` - dragon's model file. If not specified, default one from the base mod will be used
- `animation` - dragon's animation model file. If not specified, default one from the base mod will be used
- `saddle` - dragon's saddle texture. If not specified, default one from the base mod will be used
- `baby_texture` - baby dragon's texture. If not specified, will apply texture as described in `texture` field description
- `baby_model` - baby dragon's model file. If not specified, will apply model as described in `model` field description
- `baby_animation` - baby dragon's animation model file. If not specified, will apply animation as described in `animation` field description
- `baby_saddle` - baby dragon's saddle texture. If not specified, will apply saddle texture as described in `saddle` field description
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

**MAKE SURE NAMESPACE UNIQUE IF YOU DON'T INTEND TO OVERRIDE ANYTHING OR OTHERWISE PREPARE FOR POSSIBLE TECHNICAL ISSUES AND ANGRY USERS SLAMMING YOUR DOOR FOR PACK NOT WORKING**

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

Additionally, you can specify to which dragon model redirect belongs right within file. If done so, file name will be ignored. To do that, you have to add field called `dragon` outside `redirects` array. I.e.:
```json
{
  "dragon": "some_dragon",
  "redirects": [
    ...
  ]
}
```
In this case, whatever file name is, this specific model redirect file will always belong to dragon with ID `some_dragon`.
Same rules for `dragon` field apply as for file name

## Mandatory bones (aka "Do Not Touch" bones)
If you do use model redirect to swap model of dragon, make sure those bones are present on the model. Reason for this is that IoB does some manipulation over them, but never checks if they actually present on the model. And in case of absence of those bones, game on side of the client will crash.

| Dragon              | Bone names                                                                                                                                | Comment                                                                                                                                                                                                                                                                                                                                                                              |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Deadly Nadder       | `Tail5Spikes`, `Tail4Spikes`, `Tail3Spikes`, `Tail2Spikes`, `Tail1Spikes`, `Neck1`, `Neck2`, `head`, `rotation`, `Bags`, `Saddle`         | `TailNSpikes` are used to display how much shots in spike attack left, `NeckN` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                       |
| Gronckle            | `head`, `rotation`, `Bags`, `Saddle`                                                                                                      | `head` is used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                                                                                           |
| Light Fury          | `Neck`, `head`, `rotation`, `Bags`, `Saddle`                                                                                              | `Neck` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                                                                               |
| Monstrous Nightmare | `Neck1`, `Neck2`, `Neck3`, `Neck4`, `head`, `rotation`, `Bags`, `Saddle`                                                                  | `NeckN` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                                                                              |
| Night Fury          | `Neck`, `head`, `rotation`, `Bags`, `Saddle`                                                                                              | `Neck` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                                                                               |
| Night Light         | `Neck`, `head`, `rotation`, `Bags`, `Saddle`                                                                                              | `Neck` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                                                                               |
| Skrill              | `spikeRemove`, `Neck`, `head`, `rotation`, `Bags`, `Saddle`                                                                               | `spikeRemove` is used to hide spikes on back when dragon has saddle equipped. `Neck` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                 |
| Speed Stinger       | `Neck1`, `Neck2`, `head`                                                                                                                  | `NeckN` and `head` are used for head rotations                                                                                                                                                                                                                                                                                                                                       |
| Lead Speed Stinger  | `Neck1`, `Neck2`, `head`                                                                                                                  | `NeckN` and `head` are used for head rotations                                                                                                                                                                                                                                                                                                                                       |
| Stinger             | `Collar`, `Spike2`, `HeadTrack1`, `HeadTrack2`, `HeadTrack3`, `Bags`, `Saddle`                                                            | `Collar` is used to display if dragon is tamed or not,`Spike2` is used to hide spikes on back when dragon has saddle equipped. `HeadTrackN` are used for head rotations. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                                 |
| Terrible Terror     | `HeadTrack1`                                                                                                                              | `HeadTrack1` is used for head rotations. Side note: it will only rotate on half of what it supposed to because someone made a typo in IoB code lol                                                                                                                                                                                                                                   |
| Triple Stryke       | `spikeRemove`, `neck1`, `neck2`, `head`, `rotation`, `Bags`, `Saddle`                                                                     | `spikeRemove` is used to hide spikes on back when dragon has saddle equipped. `neckN` and `head` are used for head rotations. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively                                                                                                |
| Hideous Zippleback  | `SpikeRemove`, `right2Neck`, `right4Neck`, `right6Neck`, `left2Neck`, `left4Neck`, `left6Neck`, `rightHead`, `rotation`, `Bags`, `Saddle` | `SpikeRemove` is used to hide spikes on back when dragon has saddle equipped. `rightNNeck`, `rightNNeck`, `rightHead` and `head` are used for head rotations. `rightHead` is also scaled depending if dragon is baby or not. `rotation` is responsible for pitch rotation during flight. `Bags` and `Saddle` are used to display if dragon got chest or saddle equipped respectively |

Additionally, there's also bone `root` that is responsible for scaling entire model for all dragons, but it fortunately does have presence check. `head` as well is used for scaling model for baby dragons and in that specific scenario also does have presence check, but not in any other

## Glowing layer and Deadly Nadder's membranes
To add glowing layer to either dragon or egg, just place texture with parts you want to glow in the same folder as main texture file and named same as main texture file, but with postfix `_glowing`. I.e. if dragon's variant name is `red`, main texture file name will be `red.png`. To add glowing layer, you need to place texture named `red_glowing.png` alongside it.

Similar story regarding Deadly Nadder's membrane texture, you need to add `_membranes` postfix and place alongside main texture (i.e. `red_membranes.png`). To add glowing layer to membranes, use `_membranes_glowing` (i.e. `red_membranes_glowing.png`).

## Summoning dragon of specific variant
To summon a dragon or egg of specific variant, use `/isleofberk:dragon_or_egg_id ~ ~ ~ {VariantName:name}`, where `isleofberk:dragon_or_egg_id` - in-game id of either egg or dragon from the Isle of Berk mod you want to summon, `name` - name of the variant. 
Alternatively, if `nametag_accessible` for specific variant wasn't set to `false` for a variant you want and `disable_named_variants` client config option isn't set to `true`, you can use name tag to display variant you want. Using name tag to display variant won't override actual dragon's variant (which is stored in `VariantName` NBT). Only works with dragons

## Autogenerating translation keys
By setting `generate_translations` in client config to `true`, you enable translation key autogenerator. It'll autogenerate localisation keys for eggs and dragons and print them in log file for all variants that have model redirects (yes, even if model redirect contains only a name) on each resource reload.

## Making passenger model follow dragon movement
Mod automatically will try to detect any bone on the model named `passengerX`, where X is the number of passenger, starting from 0. So i.e. for first passenger it'll be `passenger0`, for second `passenger1` and so on. If mod manages to find respective bone, it'll attempt to move passenger's model relative to position of named bone (and yes, it'll follow animated bones too).
To see example models, you can check example packs:
### In Nether Dragons:
- `isleofberk/geo/dragons/nightmare/soul.geo.json` - Model of Monstrous Nightmare of "Soul" variant
- `isleofberk/geo/dragons/deadly_nadder/fungus.geo.json` - Model of Deadly Nadder of "Warped" and "Crimson" variant
### Sakura's Mixed Dragons:
- `sakura/geo/deathgripper.geo.json` - Model of Triple Stryke remade to look like Deathgripper
- `sakura/geo/deathly_galeslash.geo.json` - Model of Deadly Nadder remade to look like Deathly Galeslash