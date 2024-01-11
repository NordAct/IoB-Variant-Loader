package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.nightfury.NightFury;
import com.GACMD.isleofberk.entity.dragons.nightfury.NightFuryModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NightFuryModel.class)
public abstract class NightFuryModelMixin extends  BaseDragonModelMixin<NightFury>{
    @Override
    public String getDragonFolder() {
        return "night_fury";
    }

    @Override
    public String getDefaultTexture() {
        return "night_fury";
    }

    @Override
    public ResourceLocation getModelLocation(NightFury entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(NightFury entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(NightFury entity) {
        return super.getAnimationFileLocation(entity);
    }
}
