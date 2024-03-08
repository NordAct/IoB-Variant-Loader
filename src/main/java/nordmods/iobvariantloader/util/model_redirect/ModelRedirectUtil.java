package nordmods.iobvariantloader.util.model_redirect;

import com.GACMD.isleofberk.IsleofBerk;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;

import java.util.Collection;
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
           return dragonModelRedirects.get(dragon).get(name).model() == null ? ".json" : dragonModelRedirects.get(dragon).get(name).model();
       else return ".json";
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
            return dragonModelRedirects.get(dragon).get(name).animation() == null ? ".json" : dragonModelRedirects.get(dragon).get(name).animation();
        else return ".json";
    }

    public static ResourceLocation getCustomSaddlePath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getSaddle(dragonID, ResourceUtil.parseName(dragon));
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "textures/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantSaddlePath(ADragonBase dragon, String dragonID) {
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "textures/dragons/" + dragonID + "/" + ModelRedirectUtil.getSaddle(dragonID, ((VariantNameHelper)dragon).getVariantName()));
    }

    public static String getSaddle(String dragon, String name) {
        if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name))
            return dragonModelRedirects.get(dragon).get(name).saddle() == null ? ".png" : dragonModelRedirects.get(dragon).get(name).saddle();
        else return ".png";
    }

    public static boolean isNametagAccessible(String dragon, String name) {
        if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name)) return dragonModelRedirects.get(dragon).get(name).nametagAccessible();
        else return true;
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

        if (IoBVariantLoader.clientConfig.generateTranslations.get()) {
            Collection<ResourceLocation> list = Minecraft.getInstance().getResourceManager().listResources("textures/dragons/", s ->  s.endsWith(".png"));
            for (ResourceLocation resource : list) {
                String path = resource.getPath();
                String key = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".png"));

                boolean skip = false;
                for (String ending : IoBVariantLoader.clientConfig.ignoredByGeneratorEndings.get()) {
                    if (key.endsWith(ending)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) continue;

                if (!IoBVariantLoader.clientConfig.ignoredByGenerator.get().contains(key)) System.out.println("\"tooltip.iobvariantloader.variant." + key + "\": \"" + parseName(key) + "\",");
            }
        }
    }

    public static String parseName(String name) {
        while (name.contains("_")) {
            int index = name.indexOf("_");
            if (index + 1 < name.length()) {
                String toReplace = String.valueOf(name.charAt(index + 1));
                name = name.replaceFirst("_" + toReplace, " " + toReplace.toUpperCase());
                continue;
            }
            name = name.replace("_", " ");
        }
        name = name.replace(" N ", " & ");
        String firstLetter = String.valueOf(name.charAt(0));
        return name.replaceFirst(firstLetter, firstLetter.toUpperCase());
    }
}
