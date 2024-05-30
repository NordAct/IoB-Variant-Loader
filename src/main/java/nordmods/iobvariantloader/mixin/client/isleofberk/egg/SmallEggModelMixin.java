package nordmods.iobvariantloader.mixin.client.isleofberk.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.small.ADragonSmallEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.DragonEggModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(SmallEggModel.class)
public abstract class SmallEggModelMixin extends AnimatedGeoModel<ADragonSmallEggBase> implements DragonEggModelHelper<ADragonSmallEggBase> {
    @Override
    public ResourceLocation getModelLocation(ADragonSmallEggBase entity) {
        return getModel(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(ADragonSmallEggBase entity) {
        return getTexture(entity);
    }

    public String defaultEggModel() {
        return "small_egg_model";
    }
}
