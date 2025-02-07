- Added `baby_texture`, `baby_model`, `baby_animation` and `baby_saddle` model redirect fields
  - They act exactly the same as their counterparts without `baby_` prefix
  - Anything placed in those fields will be applied only if dragon is baby
  - If those fields are not specified, baby models, animations and textures will be applied normally

- Improved readability of model redirect data, hitbox redirect and variant spawn debug prints in logs
  - They also are now printed in normal log instead of debug one

- Added options `log_model_redirects` in client config and `log_variant_spawns` and `log_hitbox_redirects` in common config. Both are set to `false` by default
  - If `log_model_redirects` set to `false`, information about registered model redirects will not be printed in logs
  - If `log_variant_spawns` set to `false`, information about registered variant spawns will not be printed in logs
  - If `log_hitbox_redirects` set to `false`, information about registered hitbox redirects will not be printed in logs
  
- Added `dragon` field for model redirects, hitbox redirects and variant spawns.
  - `dragon` field allows to specify dragon ID to which this file belongs
  - If `dragon` is not specified, it'll attempt to define dragon ID from file name as before
  - Field must be specified outside entry list for both cases, i.e.:
  ```json
  {
    "dragon": "some_dragon",
    "redirects": [
      ...
    ]
  }
  ```

- Now if mod is unable to recognize dragon ID in model redirect, hitbox redirect or variant spawn file, file will be skipped and warning will be printed in console