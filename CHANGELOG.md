## Warning: this update contains some changes that may be breaking for some people or packs without proper configuration of the mod or adjustment to packs

### This update initially released as Beta until stability of it is ensured

- Added alternative land navigator that should make dragon movement less janky, especially for bigger ones (>1 block wide)
  - Yes, I straight up ripped it out of Useless Reptile
  - You can disable it in config

- Added Variant Collections
  - Variant collections are collections of lists of dragon variants. Each list can have different base dragon
  - Collections can include other collections
  - There's quite a few build-in collections within mod itself, you can check them out in `data/isleofberk/variant_collections`
  - Variant Collections are very much inspired by vanilla's tag system (which cannot be used in this scenario due to how Variant Loader works)

- Added new advancement criteria triggers
  - `iobvariantloader:tame_variant_from_collection` - triggers when dragon from specified collections is tamed by player
  - `iobvariantloader:kill_variant_from_collection` - triggers when dragon from specified collections is killed by player

- Added Breeding Lists
  - Breeding lists allow you to specify pair of dragons in form of either variant lists or/and collections that can produce specified in entries variants
  - Each entry accepts specification of either variant lists or/and collection and weight it has
  - When entry is chosen, dragon variant from entry will be chosen randomly
  - By default, breeding lists are prioritized by mod (can be changed in config)
  - Parent dragons and dragons from entries can have different base dragons (yes, you officially can make hybrids now)
  - If config setting set to `PRIORITIZED`, dragons will first look for dragons that have a match from available to them breeding lists. If there's none, old behavior will be used as fallback
  - If config setting set to `ENFORCED`, dragons will breed only using breeding lists (without fallback to old behavior)
  - If you don't want breeding lists to be used at all, you can set said setting to `IGNORED`
  - Mod comes with build-in breeding lists that can be found in `data/isleofberk/breeding_lists`

- Dragon Variant Spawners, Extras, Hitbox Redirects and Sound Redirects now support specifying variant collections as entries
  - If collection is used, `dragon` field and file name are ignored for dragon species specification. Instead, dragon species will be taken directly from variant lists
  - `name` field in each one now can also be specified as list, meaning you can specify multiple variants without using collections
  - Entries specified by `name` are added after entries added by collection, so they can override them

- Most of build-in Dragon Variant Spawners (in `data/isleofberk/dragon_variants`) have been redone to use variant collections
  - If you're pack maker and add variants for dragons that do not require special conditions, please check out the changes and consider moving those variants to variant collections of respective rarities, since those are also used in breeding lists

- Added Custom Item Interactions to Extras
  - Custom item interactions are specified as list in field `custom_item_interactions` in extras entry
  - They allow you to specify items, required amount of items, if it can be used on untamed dragon, if it can be used on not owned dragon, if should items be consumed on use and commands to be executed on interaction
  - Commands are specified as list of objects that contain command itself and executor (can be either dragon, user (interacting player) or server)
  - For security reasons, all executors have permission level of 2. So no, you cannot stop the server or ban someone by giving your dragon a fish

- If there's several entries for same dragon species and variant, all Extras entries for this variant of the dragon will be merged
  - Variant group and loot table redirect still will be overridden by last entry added