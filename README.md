## Very Important
Mod requires Forge 40.2.10 or above! Forge may not tell you about this and throw an exception with different error.
Mod also is incompatible with Optifine

## Description
Adds ability to add new variants with condition support or configure existing ones for [Isle of Berk](https://modrinth.com/mod/isle-of-berk) dragons via resource and data packs and change textures, animations or models via nametags. Also adds support adds ability to add glowing layer to the texture.
**Note:** using this mod to add own variants already considers that user is familiar with making resource and data packs (no matter the complexity).

## Changes to original mod
This addon changes how some stuff in original mod works gameplay wise:
- Variant inheritance works for all dragons (can be configured or turned off completely by setting `assign_egg_variant_on_breeding` and `assign_egg_variant_on_placed` to `false` in config)
- All eggs now can hold variant as NBT that will determine variant of the dragon that will be hatched

## Example Packs
- [Example data pack](https://github.com/NordAct/IoB-Variant-Loader/tree/2.4.0/Example%20Data%20Packs)
- [Example resource packs](https://github.com/NordAct/IoB-Variant-Loader/tree/2.4.0/Example%20Resource%20Packs)


## Adding custom variant spawns/editing existing via datapack, editing hit and attack boxes, changing passenger positions
Variant Loader allows adding their own variants for spawning, as well as changing hitboxes and even passenger offsets via data packs.
Full explanation on how to use those can be found [here](https://github.com/NordAct/IoB-Variant-Loader/blob/2.4.0/Example%20Data%20Packs/README.md)

## Model redirects, glowing layer and making passenger follow movement of the model
Model redirects is a tool that allows you to replace models, animations and saddle textures per variant for dragons and models with textures for eggs. Example declaration can be found in example resource pack.
Full explanation on how to use those can be found [here](https://github.com/NordAct/IoB-Variant-Loader/blob/2.4.0/Example%20Resource%20Packs/README.md)

## Config
Mod has client and server config with options that can affect your gameplay experience
### Client
- `disable_glowing` - Disables glowing layer on textures. Off by default
- `disable_named_variants` - Disables variant display via nametag completely, ignoring `nametag_accessible` field. Off by default
- `display_original_variant_name` - Forces actual variant name to be displayed in tooltip (aka one in `VariantName` NBT)
- `generate_translations` - Automatically generates translations for all variants for english language based on presented model redirects for dragons. All generated lines will be printed to log file. Use this only if you're pack developer for making your life easier
- `ignored_by_generator` - Prints for variants that have same name as any value in this list will be skipped when translation key generator is used
- `log_model_redirects` - Logs any added model redirect in console for easier debugging

### Common
- `inheritance_chance` - Defines the chance of dragon inheriting variant of their parents. 1 means variant will always be the same as their parents one
- `assign_egg_variant_on_breeding` - Enables variant assignment on breeding. If false, all eggs always will have no variant assigned during breeding
- `assign_egg_variant_on_placed` - Enables variant assignment when egg is placed and has empty or invalid variant tag. If false, all eggs always will have no variant assigned when placed
- `log_variant_spawns` - Logs any added variant spawn in console for easier debugging
- `log_hitbox_redirects` - Logs any added hitbox redirects in console for easier debugging

## FAQ
**Q:** Is there any video guide on how to use Variant Loader?\
**A:** Yes - https://youtu.be/Ettk4RiZnx0

**Q:** Do I need to add default textures to resource pack?\
**A:** No! This is only adding to the size of the pack and also may cause conflicts with other packs that rely on texture replacement. Such practice has no benefit

**Q:** Does adding variant spawn also makes dragon spawn in specific biome(s)?\
**A:** No, mod does not automatically add dragon spawns in specific biomes. Restricting variant to specific biome only makes variant obtainable in this biome. If dragon can't naturally spawn in specified biome, only way to obtain it would be via hatching/breeding in this biome.

**Q:** Why my added variants of Speed Stingers are not spawning?\
**A:** Speed Stinger spawns are tied to structures purely. Either you add them to same biomes where those structures can spawn, add your own structures where they can spawn or try to obtain them via breeding

**Q:** How do I make variant nametag only?\
**A:** Just don't add variant spawn via data pack


**Q:** Can I make variant impossible to be applied via nametag?\
**A:** Set `nametag_accessible` in model redirect file to false for your variant


**Q:** Can I make variant obtainable via only hatching(breeding)/only natural spawn?\
**A:** If you want variant to only appear via natural spawn, set breeding_weight to 0. If you want it to be obtainable only via breeding, set weight to 0


**Q:** How can I summon specific variant with Variant Loader?\
**A:** Use `/summon ~ ~ ~ isleofberk:dragon_id {VariantName:name}` where name is the name of the variant


**Q:** Can I add new animations with Variant Loader?\
**A:** No. Animation names and conditions for them are hardcoded for each dragon individually


**Q:** My pack is not working/breaks stuff. What went wrong?\
**A:** Most common mistakes are using upper case letters in file names or paths to the files in packs (Minecraft is very sensitive to this) and malforming .json files. To validate syntax in .json files, you can use online services.


**Q:** How do I make resource/data pack?\
**A:** Search engines and YouTube are your best friends. I'm not going to explain this myself, this is out my scope

## Troubleshooting
Still have questions? You can either ask on GitHub or hop on [New Berk's Dawn Discord Server](https://discord.gg/r7CzMyajm7) and ask questions in the [mod thread](https://discord.com/channels/614526777590546453/1146579340738441316) in the extras channel