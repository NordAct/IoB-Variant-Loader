package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadderModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeadlyNadderModel.class)
public abstract class DeadlyNadderModelMixin extends BaseDragonModelMixin<DeadlyNadder> {
    @Override
    public String getDragonFolder() {
        return "deadly_nadder";
    }

    @Override
    public String getDefaultTexture() {
        return "stormfly";
    }

    @Override
    public ResourceLocation getModelLocation(DeadlyNadder entity) {
        return super.getModelLocation(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(DeadlyNadder entity) {
        return super.getTextureLocation(entity);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DeadlyNadder entity) {
        return super.getAnimationFileLocation(entity);
    }
}
