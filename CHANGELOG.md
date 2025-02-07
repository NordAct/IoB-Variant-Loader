- Added `baby_texture`, `baby_model`, `baby_animation` and `baby_saddle` model redirect fields
  - They act exactly the same as their counterparts without `baby_` prefix
  - Anything placed in those fields will be applied only if dragon is baby
  - If those fields are not specified, baby models, animations and textures will be applied normally

- Improved readability of model redirect data and variant spawn debug prints in logs
- Added options `log_model_redirects` in client config and `log_variant_spawns` in common config. Both are set to `false` by default
  - If `log_model_redirects` set to `false`, information about registered model redirects will not be printed in logs
  - If `log_variant_spawns` set to `false`, information about registered variant spawns will not be printed in logs