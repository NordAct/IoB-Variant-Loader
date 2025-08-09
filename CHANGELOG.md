- Added sound redirects. Sound redirects allow you to redefine (most of) hardcoded dragon sound, as well as add sound to sound keyframes. This obviously includes ability to change the infamous dragon flapping sound.
  - Due technical limitations, sound redirects needed to be done in data pack
  - Some sound names are hardcoded to be used as redirects for hardcoded sounds
  - For more info on feature [check example datapacks README.md](https://github.com/NordAct/IoB-Variant-Loader/tree/2.5.0/Example%20Data%20Packs)

- Added extras files for various miscellaneous variant-dependent stuff, such as:
  - Loot table redirect - specifies loot table ID that will replace default one and from which loot on death will be dropped
  - Attribute modifiers - specifies variant attribute modifiers, allows you to control variant's health, speed, armor, etc
  - Taming items - items with which dragon can be tamed. Overrides default item(s), so empty field can be used to make dragon unnameable
  - Breeding items - items with which dragon can be bred. Overrides default item(s), so empty field can be used to make dragon impossible to be bred
  - Variant group - adds NBT field `VariantGroup` that has value specified in this field. `VariantGroup` cannot be changed by normal means (via commands), only via extras file. Can be useful for developers who use NBT selectors for easier selection
  - For more info on feature [check example datapacks README.md](https://github.com/NordAct/IoB-Variant-Loader/tree/2.5.0/Example%20Data%20Packs)

Due to amount of changes this update initially released as beta. Please report all issues on either GitHub or in [mod thread](https://discord.com/channels/614526777590546453/1146579340738441316)