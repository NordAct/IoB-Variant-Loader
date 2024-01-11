package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.skrill.Skrill;
import com.GACMD.isleofberk.entity.dragons.skrill.SkrillModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkrillModel.class)
public abstract class SkrillModelMixin extends BaseDragonModelMixin<Skrill>{
    @Override
    public String getDragonFolder() {
        return "skrill";
    }

    @Override
    public String getDefaultTexture() {
        return "skrill";
    }

    @Override
    public ResourceLocation getModelLocation(Skrill entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(Skrill entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Skrill entity) {
        return super.getAnimationFileLocation(entity);
    }
}
