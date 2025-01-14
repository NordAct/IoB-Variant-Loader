- Added hitbox redirects. **Note: hitbox redirects are applied via checking base variant and will not be applied if you use nametag.**
  - Hitbox redirects should go in `hitbox_redirects` folder in your data pack under any namespace. 
  - For each species file should be named respectively (i.e. `triple_stryke` for Triple Stryke and `monstrous_nightmare` for Monstrous Nightmare)
    - Each record in hitbox redirects file can have the following fields: 
      - `name` - name of the variant to which will be affected, mandatory
      - `passenger_positions` - overrides passenger positions relative to the entity, optional. Presented in form of an array that contains relative positions, which are itself are arrays with size of 3, for each passenger in order. I.e. writing like this: 

      ```json
              "passenger_positions": [
                  [-1, 2, 0],
                  [2, 3, -3]
              ]
      ```
      means that first passenger (aka controlling passenger or rider) will be offset on -1, 2 and 0 on x, y and z axis respectively relative to vehicle (aka dragon in our case) entity's center. Second passenger will be offset on 2, 3 and -3 on x, y and z axis respectively relative to the center. If there's no redirect for passenger position is found, passenger will be positioned according to how base mod does it.

      - `hitbox` - overrides entity's hitbox, optional. Has the following fields: `width` and `height`. Both are mandatory. If not specified, default hitbox will be used
      - `attack_box` - overrides attack box sizes for dragons that have it (Gronkle, Stinger, Triple Stryke), optional. Just like `hitbox`, has `width` and `height` fields and both are mandatory
      - `attack_box_position` - position of attack box relative to the entity, written as array of 3 numbers, which are x, y and z coordinates relative to the entity. Optional. If not specified, the box will be positioned like in base mod
  

- Added possibility to make passenger model follow movement of specific bones
  - This means that Variant Loader will automatically look up for bones called `passengerN` to position passenger model relative to this bone. `N` here represents passenger's number, where count starts from 0 (first passenger will have number 0, thus mod will look for bone `passenger0`, second will have number 1 and mod will look for `passenger1` and so on).
  - If specific bone is not present, passenger will be rendered normally
  

- Fixed bug that caused variants that applied via nametags to be applied incorrectly if those variants didn't have redirects that base variant has
- Fixed issue that caused model caches to be reset incorrectly, which caused all sorts of weird visual issues with models
- Mod now will automatically assign corresponding Variant Loader variant according to variant id from base mod if it can't find. This means you no longer have to fix dragon variants manually when adding Variant Loader to existing worlds
- Added new `surface_restriction` field to spawn conditions that allows to define if dragon has or has not to be on surface in order to spawn
- If place has dragon spawn, but no variants, dragon will now simply not spawn. If it still manages to spawn, mod will set variant to default and warn in logs instead of throwing exception