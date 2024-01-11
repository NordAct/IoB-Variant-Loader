package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStingerModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpeedStingerModel.class)
public abstract class SpeedStingerModelMixin extends BaseDragonModelMixin<SpeedStinger>{
    @Override
    public String getDragonFolder() {
        return "speed_stinger";
    }

    @Override
    public String getDefaultTexture() {
        return "speed_stinger";
    }

    @Override
    public ResourceLocation getModelLocation(SpeedStinger entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(SpeedStinger entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SpeedStinger entity) {
        return super.getAnimationFileLocation(entity);
    }
}