package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStrykeModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TripleStrykeModel.class)
public abstract class TripleStrykeModelMixin extends BaseDragonModelMixin<TripleStryke> {
    @Override
    public String getDragonFolder() {
        return "triple_stryke";
    }

    @Override
    public String getDefaultTexture() {
        return "triple_stryke";
    }

    @Override
    public ResourceLocation getModelLocation(TripleStryke entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(TripleStryke entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TripleStryke entity) {
        return super.getAnimationFileLocation(entity);
    }
}