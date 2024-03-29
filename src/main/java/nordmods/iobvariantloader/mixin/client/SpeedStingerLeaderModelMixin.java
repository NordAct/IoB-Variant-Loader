package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader;
import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeaderModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpeedStingerLeaderModel.class)
public abstract class SpeedStingerLeaderModelMixin extends BaseDragonModelMixin<SpeedStingerLeader>{
    @Override
    public String getDragonFolder() {
        return "speed_stinger";
    }

    @Override
    public String getDefaultTexture() {
        return "speed_stinger_leader";
    }

    @Override
    public ResourceLocation getModelLocation(SpeedStingerLeader entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(SpeedStingerLeader entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SpeedStingerLeader entity) {
        return super.getAnimationFileLocation(entity);
    }

    @Override
    public ResourceLocation getCustomTexture(SpeedStingerLeader entity) {
        return ResourceUtil.getCustomTexturePath(entity, getDragonFolder(),"_leader");
    }
    @Override
    public ResourceLocation getVariantTexture(SpeedStingerLeader entity) {
        return ResourceUtil.getVariantTexturePath(((VariantNameHelper)entity).getVariantName(), getDragonFolder(), "_leader");
    }
}
