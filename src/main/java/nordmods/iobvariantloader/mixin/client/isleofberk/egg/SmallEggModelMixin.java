package nordmods.iobvariantloader.mixin.client.isleofberk.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.DragonEggModelHelper;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(SmallEggModel.class)
public abstract class SmallEggModelMixin extends AnimatedGeoModel<ADragonLargeEggBase> implements DragonEggModelHelper<ADragonLargeEggBase> {
    @Override
    public ResourceLocation getModelLocation(ADragonLargeEggBase entity) {
        return getModel(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(ADragonLargeEggBase entity) {
        return getTexture(entity);
    }

    public String defaultEggModel() {
        return "small_egg_model";
    }
}
