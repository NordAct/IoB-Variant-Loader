package nordmods.iobvariantloader.util.model_redirect;

import com.GACMD.isleofberk.IsleofBerk;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ModelRedirectUtil {
    //key - dragon id
    //value - redirects per name/variant
    public static final Map<String, Map<String, ModelRedirect>> DRAGON_MODEL_REDIRECTS = new HashMap<>();
    //yes, this is necessary
    public static final Map<String, Map<String, String>> EGG_ITEM_MODEL_REDIRECTS = new HashMap<>();

    public static ResourceLocation getCustomTexturePath(ADragonBase dragon, String id) {
        return getCustomTexturePath(dragon, id, "");
    }

    public static ResourceLocation getCustomTexturePath(ADragonBase dragon, String id, String suffix) {
        String model = ModelRedirectUtil.getTexture(id, ResourceUtil.parseName(dragon), suffix, dragon.isBaby());
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID, "textures/dragons/" + id + "/" + model);
    }

    public static ResourceLocation getVariantTexturePath(String variant, String id, boolean isBaby) {
        return getVariantTexturePath(variant, id, "", isBaby);
    }

    public static ResourceLocation getVariantTexturePath(String variant, String id, String suffix, boolean isBaby) {
        String model = ModelRedirectUtil.getTexture(id, variant, suffix, isBaby);
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID, "textures/dragons/" + id + "/" + model);
    }

    public static String getTexture(String dragon, String name, String suffix, boolean isBaby) {
        name = name.toLowerCase();
        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon) && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)) {
            if (isBaby && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyTexture().isPresent()) return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyTexture().get();
            return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).texture().isEmpty() ? name + suffix + ".png" : DRAGON_MODEL_REDIRECTS.get(dragon).get(name).texture().get();
        }
        else return name + suffix + ".png";
    }

    public static ResourceLocation getCustomModelPath(ADragonBase dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String model = ModelRedirectUtil.getModel(dragonID, name, dragon.isBaby());
        if (shouldApplyFix(dragon, dragonID, name, () -> DRAGON_MODEL_REDIRECTS.get(dragonID).get(name).model().orElse(""))) {
            return new ResourceLocation(IsleofBerk.MOD_ID, "geo/dragons/"+ dragonID +".geo.json");
        }
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "geo/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantModelPath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getModel(dragonID, ((VariantNameHelper)dragon).getVariantName(), dragon.isBaby());
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "geo/dragons/" + dragonID + "/" + model);
    }

    public static String getModel(String dragon, String name, boolean isBaby) {
        name = name.toLowerCase();
       if (DRAGON_MODEL_REDIRECTS.containsKey(dragon) && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)) {
           if (isBaby && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyModel().isPresent()) return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyModel().get();
           return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).model().isEmpty() ? ".json" : DRAGON_MODEL_REDIRECTS.get(dragon).get(name).model().get();
       }
       else return ".json";
    }

    public static ResourceLocation getCustomAnimationPath(ADragonBase dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String model = ModelRedirectUtil.getAnimation(dragonID, name, dragon.isBaby());
        if (shouldApplyFix(dragon, dragonID, name, () -> DRAGON_MODEL_REDIRECTS.get(dragonID).get(name).animation().orElse(""))) {
            if (dragonID.equals("night_light")) return new ResourceLocation(IsleofBerk.MOD_ID, "animations/dragons/night_fury.animation.json");
            return new ResourceLocation(IsleofBerk.MOD_ID, "animations/dragons/"+ dragonID+".animation.json");
        }
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "animations/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantAnimationPath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getAnimation(dragonID, ((VariantNameHelper)dragon).getVariantName(), dragon.isBaby());
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "animations/dragons/" + dragonID + "/" + model);
    }

    public static String getAnimation(String dragon, String name, boolean isBaby) {
        name = name.toLowerCase();
        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon) && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)){
            if (isBaby && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyAnimation().isPresent()) return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babyAnimation().get();
            return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).animation().isEmpty() ? ".json" : DRAGON_MODEL_REDIRECTS.get(dragon).get(name).animation().get();
        } else return ".json";
    }

    public static ResourceLocation getCustomSaddlePath(ADragonBase dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String model = ModelRedirectUtil.getSaddle(dragonID, name, dragon.isBaby());
        if (shouldApplyFix(dragon, dragonID, name, () -> DRAGON_MODEL_REDIRECTS.get(dragonID).get(name).saddle().orElse(""))) {
            return new ResourceLocation(IsleofBerk.MOD_ID, "textures/dragons/" + dragonID + "/equipment.png");
        }
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "textures/dragons/" + dragonID + "/" + model);
    }

    public static ResourceLocation getVariantSaddlePath(ADragonBase dragon, String dragonID) {
        String model = ModelRedirectUtil.getSaddle(dragonID, ((VariantNameHelper)dragon).getVariantName(), dragon.isBaby());
        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "textures/dragons/" + dragonID + "/" + model);
    }

    public static String getSaddle(String dragon, String name, boolean isBaby) {
        name = name.toLowerCase();
        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon) && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)){
            if (isBaby && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babySaddle().isPresent()) return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).babySaddle().get();
            return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).saddle().isEmpty() ? ".png" : DRAGON_MODEL_REDIRECTS.get(dragon).get(name).saddle().get();
        } else return ".png";
    }

    @Nullable
    public static String getEggItemModel(String dragon, String name) {
        name = name.toLowerCase();
        if (EGG_ITEM_MODEL_REDIRECTS.containsKey(dragon)) return EGG_ITEM_MODEL_REDIRECTS.get(dragon).get(name);
        return null;
    }

    public static <T extends ADragonEggBase> String getEggFolder(T entity) {
        ResourceLocation resourcelocation = EntityType.getKey(entity.getType());
        String dragonID = resourcelocation.getPath().replace("_egg", "");
        return switch (dragonID) {
            case "m_nightmare" -> "monstrous_nightmare";
            case "nadder" -> "deadly_nadder";
            case "night_fury" -> "nightfury";
            case "speed_stinger" -> "speedstinger";
            case "triple_stryke" -> "triplestryke";
            case "gronckle" -> "gronkle";
            default -> dragonID;
        };
    }

    public static <T extends ADragonEggBase> ResourceLocation getEggTexture(T entity) {
        String dragon = ((DragonSpeciesHelper)entity).getSpecies(true);
        String name = ((VariantNameHelper)entity).getVariantName().toLowerCase();
        String texture;

        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon)
                && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)
                && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).eggTexture().isPresent())
            texture = DRAGON_MODEL_REDIRECTS.get(dragon).get(name).eggTexture().get();
        else return null;

        if (texture.contains(":")) return new ResourceLocation(texture);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "textures/egg/" + getEggFolder(entity) + "/" + texture);
    }

    public static <T extends ADragonEggBase> ResourceLocation getEggModel(T entity) {
        String dragon = ((DragonSpeciesHelper)entity).getSpecies(true);
        String name = ((VariantNameHelper)entity).getVariantName().toLowerCase();
        String model;

        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon)
                && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)
                && DRAGON_MODEL_REDIRECTS.get(dragon).get(name).eggModel().isPresent())
            model = DRAGON_MODEL_REDIRECTS.get(dragon).get(name).eggModel().get();
        else return null;

        if (model.contains(":")) return new ResourceLocation(model);
        return new ResourceLocation(IsleofBerk.MOD_ID,
                "geo/egg/" + dragon + "/" + model);
    }

    public static boolean isNametagAccessible(String dragon, String name) {
        if (DRAGON_MODEL_REDIRECTS.containsKey(dragon) && DRAGON_MODEL_REDIRECTS.get(dragon).containsKey(name)) return DRAGON_MODEL_REDIRECTS.get(dragon).get(name).nametagAccessible();
        else return true;
    }

    public static synchronized void add(String dragon, Map<String, ModelRedirect> redirects) {
        Map<String, ModelRedirect> content = DRAGON_MODEL_REDIRECTS.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            DRAGON_MODEL_REDIRECTS.put(dragon, content);
        } else DRAGON_MODEL_REDIRECTS.put(dragon, redirects);
    }

    public static synchronized void addEggItemModels(String dragon, Map<String, String> redirects) {
        Map<String, String> content = EGG_ITEM_MODEL_REDIRECTS.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            EGG_ITEM_MODEL_REDIRECTS.put(dragon, content);
        } else EGG_ITEM_MODEL_REDIRECTS.put(dragon, redirects);
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.clientConfig.logModelRedirects.get()) return;
        for (Map.Entry<String, Map<String, ModelRedirect>> entry : DRAGON_MODEL_REDIRECTS.entrySet()) {
            for (Map.Entry<String, ModelRedirect> redirects : entry.getValue().entrySet()) {
                ModelRedirect modelRedirect = redirects.getValue();
                StringBuilder modelRedirectInfo = new StringBuilder();
                if (modelRedirect.model().isPresent()) modelRedirectInfo.append("Model: ").append(modelRedirect.model()).append("\n");
                if (modelRedirect.animation().isPresent()) modelRedirectInfo.append("Animation: ").append(modelRedirect.animation()).append("\n");
                if (modelRedirect.texture().isPresent()) modelRedirectInfo.append("Texture: ").append(modelRedirect.texture()).append("\n");
                if (modelRedirect.saddle().isPresent()) modelRedirectInfo.append("Saddle: ").append(modelRedirect.saddle()).append("\n");
                if (modelRedirect.babyModel().isPresent()) modelRedirectInfo.append("Baby Model: ").append(modelRedirect.babyModel()).append("\n");
                if (modelRedirect.babyAnimation().isPresent()) modelRedirectInfo.append("Baby Animation: ").append(modelRedirect.babyAnimation()).append("\n");
                if (modelRedirect.babyTexture().isPresent()) modelRedirectInfo.append("Baby Texture: ").append(modelRedirect.babyTexture()).append("\n");
                if (modelRedirect.babySaddle().isPresent()) modelRedirectInfo.append("Baby Saddle: ").append(modelRedirect.babySaddle()).append("\n");
                if (modelRedirect.eggModel().isPresent()) modelRedirectInfo.append("Egg Model: ").append(modelRedirect.eggModel()).append("\n");
                if (modelRedirect.eggTexture().isPresent()) modelRedirectInfo.append("Egg Texture: ").append(modelRedirect.eggTexture()).append("\n");
                if (modelRedirect.eggItemName().isPresent()) modelRedirectInfo.append("Egg Item Name: ").append(modelRedirect.eggItemName().get()).append("\n");

                if (EGG_ITEM_MODEL_REDIRECTS.containsKey(entry.getKey())
                        && EGG_ITEM_MODEL_REDIRECTS.get(entry.getKey()).containsKey(redirects.getKey())) {
                    modelRedirectInfo.append("Egg Item Model: ").append(EGG_ITEM_MODEL_REDIRECTS.get(entry.getKey()).get(redirects.getKey())).append("\n");
                }

                if (modelRedirect.eggName().isPresent()) modelRedirectInfo.append("Egg Name: ").append(modelRedirect.eggName().get()).append("\n");
                if (modelRedirect.dragonName().isPresent()) modelRedirectInfo.append("Dragon Name: ").append(modelRedirect.dragonName().get()).append("\n");
                modelRedirectInfo.append("Is Accessible via Nametag: ").append(modelRedirect.nametagAccessible()).append("\n");
                IoBVariantLoader.LOGGER.info("{}: variant {} was redirected to:\n{}", entry.getKey(), redirects.getKey(), modelRedirectInfo);
            }
        }

        //translation keys generator
        if (IoBVariantLoader.clientConfig.generateTranslations.get()) {
            System.out.println("==================================================================================");
            System.out.println("ISLE OF BERK VARIANT LOADER TRANSLATION KEY AUTOGENERATOR");
            System.out.println("==================================================================================");
            Map <String, List<String>> dragonVariants = new HashMap<>();

            for (Map.Entry<String, Map<String, ModelRedirect>> entry : DRAGON_MODEL_REDIRECTS.entrySet()) {
                String dragon = entry.getKey();
                for ( Map.Entry<String, ModelRedirect> redirects : entry.getValue().entrySet()) {
                    String key = redirects.getKey();
                    if (IoBVariantLoader.clientConfig.ignoredByGenerator.get().contains(key)) continue;
                    if (!dragonVariants.containsKey(dragon)) dragonVariants.put(dragon, new ArrayList<>());
                    dragonVariants.get(dragon).add(key);
                }
            }

            for (Map.Entry <String, List<String>> entry : dragonVariants.entrySet()) {
                String dragon = entry.getKey();
                for (String key : entry.getValue())
                    System.out.println("\"item.iobvariantloader.egg." + dragon + "." + key + "\": \"" + parseName(key) + " " + parseName(dragon) + " Egg\",");
            }
            System.out.println();

            for (Map.Entry <String, List<String>> entry : dragonVariants.entrySet()) {
                String dragon = entry.getKey();
                for (String key : entry.getValue())
                    System.out.println("\"tooltip.iobvariantloader." + dragon + "." + key + "\": \"" + parseName(key) + "\",");
            }
            System.out.println();

            for (Map.Entry <String, List<String>> entry : dragonVariants.entrySet()) {
                String dragon = entry.getKey();
                for (String key : entry.getValue())
                    System.out.println("\"entity.iobvariantloader.egg." + dragon + "." + key + "\": \"" + parseName(key) + " " + parseName(dragon)  + " Egg\",");
            }
            System.out.println();

            for (Map.Entry <String, List<String>> entry : dragonVariants.entrySet()) {
                String dragon = entry.getKey();
                for (String key : entry.getValue())
                    System.out.println("\"entity.iobvariantloader." + dragon + "." + key + "\": \"" + parseName(key) + " " + parseName(dragon) + "\",");
            }
            System.out.println("==================================================================================");
        }
    }

    private static String parseName(String name) {
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


    public static void registerEggItemModelRedirects(ResourceManager manager) {
        ModelRedirectUtil.EGG_ITEM_MODEL_REDIRECTS.clear();
        Collection<ResourceLocation> resourceCollection = manager.listResources("model_redirects", path -> path.endsWith(".json"));
        for (ResourceLocation id : resourceCollection) {
            String path = id.getPath();
            String dragon = "";

            Map<String, String> redirects = new HashMap<>();
            try (InputStream stream = manager.getResource(id).getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    JsonElement element = JsonParser.parseReader(bufferedReader);
                    dragon = element.getAsJsonObject().has("dragon") ? element.getAsJsonObject().get("dragon").getAsString() : path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));
                    if (!ResourceUtil.AllowedValues.isValid(dragon, true)) continue;
                    JsonArray array = GsonHelper.getAsJsonArray((JsonObject) element, "redirects");
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject input = array.get(i).getAsJsonObject();
                        String eggModel = input.has("egg_item_model") ? input.get("egg_item_model").getAsString() : null;
                        if (eggModel == null) continue;
                        String name = input.get("name").getAsString();
                        redirects.put(name, eggModel);
                    }
                } catch (JsonIOException e) {
                    IoBVariantLoader.LOGGER.error("Failed to read json " + id, e);
                }

            } catch (Exception e) {
                IoBVariantLoader.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
            if (!redirects.isEmpty()) ModelRedirectUtil.addEggItemModels(dragon, redirects);
        }
    }

    //an INCREDIBLY shitty fix for bug that occurs when variant via name tag is applied over base variant with redirects other than base texture
    private static boolean shouldApplyFix(ADragonBase dragon, String dragonID, String name, StringArgument argument) {
        boolean texturePresent = ResourceUtil.isValid(getCustomTexturePath(dragon, dragonID));
        boolean absentRecord = !(DRAGON_MODEL_REDIRECTS.containsKey(dragonID) && DRAGON_MODEL_REDIRECTS.get(dragonID).containsKey(name));
        boolean absentArgument = DRAGON_MODEL_REDIRECTS.containsKey(dragonID) && DRAGON_MODEL_REDIRECTS.get(dragonID).containsKey(name) && argument.getString().isEmpty();
        return texturePresent && (absentRecord || absentArgument);
    }

    private interface StringArgument {
        String getString();
    }
}
