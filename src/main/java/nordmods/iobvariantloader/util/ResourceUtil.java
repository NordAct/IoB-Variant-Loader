package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.IsleofBerk;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {
    //check if resource reload is finished because Minecraft acknowledges new resources before Geckolib does, which leads to bad stuff
    public static boolean isResourceReloadFinished;

    public static ResourceLocation getCustomTexturePath(ADragonBase dragon, String id) {
        return getCustomTexturePath(dragon, id, "");
    }

    public static ResourceLocation getCustomTexturePath(ADragonBase dragon, String id, String suffix) {
        return new ResourceLocation(IsleofBerk.MOD_ID, "textures/dragons/"+ id + "/" + parseName(dragon) + suffix +".png");
    }

    public static ResourceLocation getVariantTexturePath(String variant, String id) {
        return getVariantTexturePath(variant, id, "");
    }

    public static ResourceLocation getVariantTexturePath(String variant, String id, String suffix) {
        return new ResourceLocation(IsleofBerk.MOD_ID, "textures/dragons/"+ id + "/" + variant + suffix +".png");
    }

    public static String parseName(ADragonBase dragon) {
        if (!dragon.hasCustomName()) return "";
        String name = dragon.getName().getString().toLowerCase();
        name = name.replace(" ", "_");
        name = replaceCyrillic(name);
        if (!name.matches("^[a-zA-Z0-9_]+$")) name = "";
        return name;
    }

    public static boolean isValid(ResourceLocation id) {
        return Minecraft.getInstance().getResourceManager().hasResource(id) && id.getPath().endsWith(".json");
    }

    private static final Map<String, String> letters = new HashMap<>();
    static {
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "yo");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "j");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "c");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "shch");
        letters.put("ь", "");
        letters.put("ы", "y");
        letters.put("ъ", "");
        letters.put("э", "e");
        letters.put("ю", "yu");
        letters.put("я", "ya");
    }

    private static String replaceCyrillic(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            String l = text.substring(i, i+1);
            sb.append(letters.getOrDefault(l, l));
        }
        return sb.toString();
    }
}
