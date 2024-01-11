package nordmods.iobvariantloader.util;

import net.minecraft.resources.ResourceLocation;

public interface ModelCacheHelper {
    ResourceLocation getModelLocationCache();
    ResourceLocation getAnimationLocationCache();
    ResourceLocation getTextureLocationCache();
    void setModelLocationCache(ResourceLocation state);
    void setAnimationLocationCache(ResourceLocation state);
    void setTextureLocationCache(ResourceLocation state);
}
