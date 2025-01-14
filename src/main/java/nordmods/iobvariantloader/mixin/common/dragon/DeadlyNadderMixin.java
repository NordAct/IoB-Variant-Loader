package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.ducks.DeadlyNadderModelCacheHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DeadlyNadder.class)
public abstract class DeadlyNadderMixin extends ADragonBaseMixin implements DeadlyNadderModelCacheHelper {
    @Unique
    private ResourceLocation wingGlowLayerLocationCache;
    @Unique private ResourceLocation wingLayerLocationCache;
    @Unique private boolean preventWingGlowLayer = false;

    protected DeadlyNadderMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
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
    public boolean shouldPreventWingGlowLayerRenderer() {
        return preventWingGlowLayer;
    }

    @Override
    public void setPreventWingGlowLayer(boolean state) {
        preventWingGlowLayer = state;
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "deadly_nadder";
            case 2 -> "kingstail";
            case 3 -> "scardian";
            case 4 -> "springshedder";
            case 5 -> "hjarta";
            case 6 -> "bork_week";
            case 7 -> "flystorm";
            case 8 -> "hjaldr";
            case 9 -> "barklethorn";
            case 10 -> "lethal_lancebeak";
            case 11 -> "seedling_stormpest";
            default -> "stormfly";
        };
    }
}
