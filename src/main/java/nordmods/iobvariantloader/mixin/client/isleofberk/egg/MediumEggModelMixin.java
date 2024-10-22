package nordmods.iobvariantloader.mixin.client.isleofberk.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.medium.ADragonMediumEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.medium.MediumEggModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ducks.DragonEggModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(MediumEggModel.class)
public abstract class MediumEggModelMixin extends AnimatedGeoModel<ADragonMediumEggBase> implements DragonEggModelHelper<ADragonMediumEggBase> {
    @Override
    public ResourceLocation getModelLocation(ADragonMediumEggBase entity) {
        return getModel(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(ADragonMediumEggBase entity) {
        return getTexture(entity);
    }

    public String defaultEggModel() {
        return "medium_egg_model";
    }
}
