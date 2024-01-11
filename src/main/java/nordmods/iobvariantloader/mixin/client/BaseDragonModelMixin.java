package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.render.model.BaseDragonModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.modelRedirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(BaseDragonModel.class)
public abstract class BaseDragonModelMixin <T extends ADragonBase & IAnimatable> extends AnimatedGeoModel<T>{

    @Override
    public ResourceLocation getModelLocation(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) {
            setModelLocationCache(entity,null);
            return getDefaultModel();
        }

        if (getModelLocationCache(entity) != null) return getModelLocationCache(entity);

        ResourceLocation id = ModelRedirectUtil.getCustomModelPath(entity, getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            setModelLocationCache(entity, id);
            return id;
        }

        id = ModelRedirectUtil.getVariantModelPath(entity, getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            setModelLocationCache(entity, id);
            return id;
        }

        return getDefaultModel();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) {
            setTextureLocationCache(entity, null);
            return getDefaultVariant();
        }

        if (getTextureLocationCache(entity) != null) return getTextureLocationCache(entity);

        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            setTextureLocationCache(entity, id);
            return id;
        }

        if (entity instanceof VariantNameHelper helper) {
            id = ResourceUtil.getVariantTexturePath(helper.getVariantName(), getDragonFolder());
            if (ResourceUtil.isValid(id)) {
                setTextureLocationCache(entity, id);
                return id;
            }
        }

        return getDefaultVariant();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) {
            setAnimationLocationCache(entity,null);
            return getDefaultAnimation();
        }

        if (getAnimationLocationCache(entity) != null) return getAnimationLocationCache(entity);

        ResourceLocation id = ModelRedirectUtil.getCustomAnimationPath(entity, getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            setAnimationLocationCache(entity, id);
            return id;
        }

        id = ModelRedirectUtil.getVariantAnimationPath(entity, getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            setAnimationLocationCache(entity, id);
            return id;
        }

        return getDefaultAnimation();
    }

    public ResourceLocation getDefaultModel() {
        return new ResourceLocation("isleofberk", "geo/dragons/"+ getDragonFolder() +".geo.json");
    }

    public ResourceLocation getDefaultVariant() {
        return new ResourceLocation("isleofberk", "textures/dragons/"+ getDragonFolder() +"/"+ getDefaultTexture() +".png");
    }

    public ResourceLocation getDefaultAnimation() {
        return new ResourceLocation("isleofberk", "animations/dragons/"+ getDragonFolder() +".animation.json");
    }

    public abstract String getDragonFolder();
    public abstract String getDefaultTexture();
    public ResourceLocation getModelLocationCache(T entity) {
        return ((ModelCacheHelper)entity).getModelLocationCache();
    }
    public ResourceLocation getAnimationLocationCache(T entity) {
        return ((ModelCacheHelper)entity).getAnimationLocationCache();
    }
    public ResourceLocation getTextureLocationCache(T entity) {
        return ((ModelCacheHelper)entity).getTextureLocationCache();
    }
    public void setModelLocationCache(T entity, ResourceLocation state) {
        ((ModelCacheHelper)entity).setModelLocationCache(state);
    }
    public void setAnimationLocationCache(T entity, ResourceLocation state) {
        ((ModelCacheHelper)entity).setAnimationLocationCache(state);
    }
    public void setTextureLocationCache(T entity, ResourceLocation state) {
        ((ModelCacheHelper)entity).setTextureLocationCache(state);
    }
}
