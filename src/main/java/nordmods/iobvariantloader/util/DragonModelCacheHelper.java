package nordmods.iobvariantloader.util;

import net.minecraft.resources.ResourceLocation;

public interface DragonModelCacheHelper extends ModelCacheHelper{
    ResourceLocation getAnimationLocationCache();
    ResourceLocation getSaddleTextureLocationCache();
    void setAnimationLocationCache(ResourceLocation state);
    void setSaddleTextureLocationCache(ResourceLocation state);

    @Override
    default void resetCache() {
        ModelCacheHelper.super.resetCache();
        setAnimationLocationCache(null);
        setSaddleTextureLocationCache(null);
    }
}
