package nordmods.iobvariantloader.util;

import net.minecraft.resources.ResourceLocation;

public interface DeadlyNadderModelCacheHelper extends DragonModelCacheHelper {
    ResourceLocation getWingGlowLayerLocationCache();
    ResourceLocation getWingLayerLocationCache();
    void setWingGlowLayerLocationCache(ResourceLocation state);
    void setWingLayerLocationCache(ResourceLocation state);
    boolean shouldPreventWingGlowLayerRenderer();
    void setPreventWingGlowLayer(boolean state);
}
