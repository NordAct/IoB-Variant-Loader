package nordmods.iobvariantloader.mixin.client.isleofberk.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.medium.MediumEggModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.DragonEggModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(MediumEggModel.class)
public abstract class MediumEggModelMixin extends AnimatedGeoModel<ADragonLargeEggBase> implements DragonEggModelHelper<ADragonLargeEggBase> {
    @Override
    public ResourceLocation getModelLocation(ADragonLargeEggBase entity) {
        return getModel(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(ADragonLargeEggBase entity) {
        return getTexture(entity);
    }

    public String defaultEggModel() {
        return "medium_egg_model";
    }
}
