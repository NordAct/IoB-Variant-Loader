package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.nightlight.NightLight;
import com.GACMD.isleofberk.entity.dragons.nightlight.NightLightModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NightLightModel.class)
public abstract class NightLightModelMixin extends BaseDragonModelMixin<NightLight>{
    @Override
    public String getDragonFolder() {
        return "night_light";
    }

    @Override
    public String getDefaultTexture() {
        return "dart";
    }

    @Override
    public ResourceLocation getModelLocation(NightLight entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(NightLight entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(NightLight entity) {
        return super.getAnimationFileLocation(entity);
    }

    @Override
    public ResourceLocation getDefaultAnimation() {
        return new ResourceLocation("isleofberk", "animations/dragons/night_fury.animation.json");
    }
}
