package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.DeadlyNadderModelCacheHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(DeadlyNadder.class)
public abstract class DeadlyNadderMixin extends ADragonBaseMixin implements DeadlyNadderModelCacheHelper {
    @Unique
    private ResourceLocation wingGlowLayerLocationCache;
    @Unique private ResourceLocation wingLayerLocationCache;

    protected DeadlyNadderMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public ResourceLocation getWingGlowLayerLocationCache() {
        return wingGlowLayerLocationCache;
    }

    @Override
    public ResourceLocation getWingLayerLocationCache() {
        return wingLayerLocationCache;
    }

    @Override
    public void setWingGlowLayerLocationCache(ResourceLocation state) {
        wingGlowLayerLocationCache = state;
    }

    @Override
    public void setWingLayerLocationCache(ResourceLocation state) {
        wingLayerLocationCache = state;
    }

    @Override
    public void setVariantName(String variantName) {
        super.setVariantName(variantName);
        setWingGlowLayerLocationCache(null);
        setWingLayerLocationCache(null);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        setWingGlowLayerLocationCache(null);
        setWingLayerLocationCache(null);
    }
}
