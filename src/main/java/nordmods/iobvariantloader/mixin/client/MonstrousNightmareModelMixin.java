package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare;
import com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmareModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MonstrousNightmareModel.class)
public abstract class MonstrousNightmareModelMixin extends BaseDragonModelMixin<MonstrousNightmare>{
    @Override
    public String getDragonFolder() {
        return "nightmare";
    }

    @Override
    public String getDefaultTexture() {
        return "hookfang";
    }

    @Override
    public ResourceLocation getModelLocation(MonstrousNightmare entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(MonstrousNightmare entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(MonstrousNightmare entity) {
        return super.getAnimationFileLocation(entity);
    }
}
