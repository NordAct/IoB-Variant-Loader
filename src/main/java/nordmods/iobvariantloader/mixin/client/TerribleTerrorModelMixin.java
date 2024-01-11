package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror;
import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerrorModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TerribleTerrorModel.class)
public abstract class TerribleTerrorModelMixin extends BaseDragonModelMixin<TerribleTerror> {
    @Override
    public String getDragonFolder() {
        return "terrible_terror";
    }

    @Override
    public String getDefaultTexture() {
        return "terrible_terror";
    }

    @Override
    public ResourceLocation getModelLocation(TerribleTerror entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(TerribleTerror entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TerribleTerror entity) {
        return super.getAnimationFileLocation(entity);
    }
}