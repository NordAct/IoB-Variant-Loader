package nordmods.iobvariantloader.util;

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
}
