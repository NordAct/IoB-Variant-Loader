package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle;
import com.GACMD.isleofberk.entity.dragons.gronckle.GronckleModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GronckleModel.class)
public abstract class GronckleModelMixin extends BaseDragonModelMixin<Gronckle> {
    @Override
    public String getDragonFolder() {
        return "gronckle";
    }

    @Override
    public String getDefaultTexture() {
        return "meatlug";
    }

    @Override
    public ResourceLocation getModelLocation(Gronckle entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(Gronckle entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Gronckle entity) {
        return super.getAnimationFileLocation(entity);
    }
}
