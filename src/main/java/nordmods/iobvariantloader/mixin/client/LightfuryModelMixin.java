package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.lightfury.LightFury;
import com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LightFuryModel.class)
public abstract class LightfuryModelMixin extends BaseDragonModelMixin<LightFury>{
    @Override
    public String getDragonFolder() {
        return "light_fury";
    }

    @Override
    public String getDefaultTexture() {
        return "light_fury";
    }

    @Override
    public ResourceLocation getModelLocation(LightFury entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(LightFury entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LightFury entity) {
        return super.getAnimationFileLocation(entity);
    }
}
