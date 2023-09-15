package nordmods.iobvariantloader.util.modelRedirect;

import com.GACMD.isleofberk.IsleofBerk;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;

import java.util.HashMap;
import java.util.Map;

public final class ModelRedirectUtil {
    //key - dragon id
    //value - redirects per name/variant
    public static final Map<String, Map<String, ModelRedirect>> dragonModelRedirects = new HashMap<>();

    public static ResourceLocation getCustomModelPath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getModel(dragonID, ResourceUtil.parseName(dragon));
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "geo/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantModelPath(ADragonBase dragon, String dragonID) {
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "geo/dragons/" + dragonID + "/" + ModelRedirectUtil.getModel(dragonID, ((VariantNameHelper)dragon).getVariantName()));
    }

    public static String getModel(String dragon, String name) {
       if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name))
           return dragonModelRedirects.get(dragon).get(name).model() == null ? "" : dragonModelRedirects.get(dragon).get(name).model();
       else return "";
    }

    public static ResourceLocation getCustomAnimationPath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getAnimation(dragonID, ResourceUtil.parseName(dragon));
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "animations/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantAnimationPath(ADragonBase dragon, String dragonID) {
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "animations/dragons/" + dragonID + "/" + ModelRedirectUtil.getAnimation(dragonID, ((VariantNameHelper)dragon).getVariantName()));
    }

    public static String getAnimation(String dragon, String name) {
        if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name))
            return dragonModelRedirects.get(dragon).get(name).animation() == null ? "" : dragonModelRedirects.get(dragon).get(name).animation();
        else return "";
    }

    public static synchronized void add(String dragon, Map<String, ModelRedirect> redirects) {
        Map<String, ModelRedirect> content = dragonModelRedirects.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            dragonModelRedirects.put(dragon, content);
        } else dragonModelRedirects.put(dragon, redirects);
    }

    public static void debugPrint() {
        for (Map.Entry<String, Map<String, ModelRedirect>> entry : dragonModelRedirects.entrySet()) {
            for ( Map.Entry<String, ModelRedirect> redirects : entry.getValue().entrySet()) {
                    IoBVariantLoader.LOGGER.debug("{}: variant/name {} was redirected to model {}", entry.getKey(), redirects.getKey(), redirects.getValue());
            }
        }
    }
}
