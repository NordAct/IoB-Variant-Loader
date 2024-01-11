package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack;
import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZippleBackModel.class)
public abstract class ZippleBackModelMixin extends BaseDragonModelMixin<ZippleBack> {
    @Override
    public String getDragonFolder() {
        return "zippleback";
    }

    @Override
    public String getDefaultTexture() {
        return "pistill";
    }

    @Override
    public ResourceLocation getModelLocation(ZippleBack entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(ZippleBack entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ZippleBack entity) {
        return super.getAnimationFileLocation(entity);
    }
}