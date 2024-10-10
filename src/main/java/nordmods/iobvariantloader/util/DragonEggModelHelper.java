package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;

public interface DragonEggModelHelper<T extends ADragonEggBase> {
    default ResourceLocation getDefaultModel() {
        return new ResourceLocation("isleofberk", "geo/egg/"+ defaultEggModel() +".geo.json");
    }

    default ResourceLocation getDefaultTexture(T entity) {
        return new ResourceLocation("isleofberk", "textures/egg/"+ ModelRedirectUtil.getEggFolder(entity) +"/"+ defaultEggTexture(entity) +".png");
    }

    String defaultEggModel();

    //i give up
    default String defaultEggTexture(T entity) {
        ResourceLocation resourcelocation = EntityType.getKey(entity.getType());
        return switch (resourcelocation.getPath()) {
            default -> "";
            case "nadder_egg" -> "egg_nadder";
            case "gronckle_egg" -> "egg_gronk";
            case "light_fury_egg" -> "light_fury_egg";
            case "m_nightmare_egg" -> "egg_nightmare";
            case "night_fury_egg" -> "egg_night_fury";
            case "night_light_egg" -> "egg_night_light";
            case "skrill_egg" -> "egg_skrill";
            case "speed_stinger_egg" ->
                switch (((VariantNameHelper)entity).getVariantName()) {
                    default -> "egg_speed_stinger_0";
                    case "floutscout" -> "egg_floutscout_1";
                    case "ice_breaker" -> "egg_ice_breaker_2";
                    case "sweet_sting" -> "egg_sweet_sting_3";
                };
            case "stinger_egg" -> "wildroar_1";
            case "terrible_terror_egg" -> "terrible_terror_egg";
            case "triple_stryke_egg" -> "triple_stryke_egg";
            case "zippleback_egg" -> "egg_zippleback";
        };
    }

    default ResourceLocation getModelLocationCache(T entity) {
        return ((ModelCacheHelper)entity).getModelLocationCache();
    }

    default ResourceLocation getTextureLocationCache(T entity) {
        return ((ModelCacheHelper)entity).getTextureLocationCache();
    }

    default void setModelLocationCache(T entity, ResourceLocation state) {
        ((ModelCacheHelper)entity).setModelLocationCache(state);
    }

    default void setTextureLocationCache(T entity, ResourceLocation state) {
        ((ModelCacheHelper)entity).setTextureLocationCache(state);
    }

    default ResourceLocation getCustomTexture(T entity) {
        return ModelRedirectUtil.getEggTexture(entity);
    }

    default ResourceLocation getCustomModel(T entity) {
        return ModelRedirectUtil.getEggModel(entity);
    }

    default ResourceLocation getModel(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel();

        if (getModelLocationCache(entity) != null) return getModelLocationCache(entity);

        ResourceLocation id = getCustomModel(entity);
        if (ResourceUtil.isValid(id)) {
            setModelLocationCache(entity, id);
            return id;
        }

        setModelLocationCache(entity,getDefaultModel());
        return getDefaultModel();
    }

    default ResourceLocation getTexture(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture(entity);

        if (getTextureLocationCache(entity) != null) return getTextureLocationCache(entity);

        ResourceLocation id = getCustomTexture(entity);
        if (ResourceUtil.isValid(id)) {
            setTextureLocationCache(entity, id);
            return id;
        }

        setTextureLocationCache(entity, getDefaultTexture(entity));
        return getDefaultTexture(entity);
    }
}
