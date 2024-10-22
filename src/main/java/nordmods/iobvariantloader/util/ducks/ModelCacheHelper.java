package nordmods.iobvariantloader.util.ducks;

import net.minecraft.resources.ResourceLocation;

public interface ModelCacheHelper {
    ResourceLocation getModelLocationCache();
    ResourceLocation getTextureLocationCache();
    ResourceLocation getGlowLayerLocationCache();
    void setModelLocationCache(ResourceLocation state);
    void setTextureLocationCache(ResourceLocation state);
    void setGlowLayerLocationCache(ResourceLocation state);
    boolean shouldPreventGlowLayerRenderer();
    void setPreventGlowLayer(boolean state);
    void resetTranslationName();
    default void resetCache() {
        setGlowLayerLocationCache(null);
        setModelLocationCache(null);
        setTextureLocationCache(null);
        setPreventGlowLayer(false);
        resetTranslationName();
    }
}
