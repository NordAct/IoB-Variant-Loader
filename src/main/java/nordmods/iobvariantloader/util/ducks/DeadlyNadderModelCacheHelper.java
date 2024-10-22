package nordmods.iobvariantloader.util.ducks;

import net.minecraft.resources.ResourceLocation;

public interface DeadlyNadderModelCacheHelper extends DragonModelCacheHelper {
    ResourceLocation getWingGlowLayerLocationCache();
    ResourceLocation getWingLayerLocationCache();
    void setWingGlowLayerLocationCache(ResourceLocation state);
    void setWingLayerLocationCache(ResourceLocation state);
    boolean shouldPreventWingGlowLayerRenderer();
    void setPreventWingGlowLayer(boolean state);
    @Override
    default void resetCache() {
        DragonModelCacheHelper.super.resetCache();
        setWingGlowLayerLocationCache(null);
        setWingLayerLocationCache(null);
        setPreventWingGlowLayer(false);
    }
}
