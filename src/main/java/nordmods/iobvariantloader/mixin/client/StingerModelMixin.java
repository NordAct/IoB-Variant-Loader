package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.stinger.Stinger;
import com.GACMD.isleofberk.entity.dragons.stinger.StingerModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StingerModel.class)
public abstract class StingerModelMixin extends BaseDragonModelMixin<Stinger> {
    @Override
    public String getDragonFolder() {
        return "stinger";
    }

    @Override
    public String getDefaultTexture() {
        return "wildroar";
    }

    @Override
    public ResourceLocation getModelLocation(Stinger entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(Stinger entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Stinger entity) {
        return super.getAnimationFileLocation(entity);
    }
}