package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {
    //check if resource reload is finished because Minecraft acknowledges new resources before Geckolib does, which leads to bad stuff
    public static boolean isResourceReloadFinished;

    public static String parseName(ADragonBase dragon) {
        if (!dragon.hasCustomName()) return "";
        String name = dragon.getName().getString().toLowerCase();
        name = name.replace(" ", "_");
        name = replaceCyrillic(name);
        if (!name.matches("^[a-zA-Z0-9_]+$")) name = "";
        return name;
    }

    public static boolean isValid(ResourceLocation id) {
        return id != null && Minecraft.getInstance().getResourceManager().hasResource(id);
    }

    private static String prepareVariantName(String name) {
        while (name.contains("_")) {
            int index = name.indexOf("_");
            if (index + 1 < name.length()) {
                String toReplace = String.valueOf(name.charAt(index + 1));
                name = name.replaceFirst("_" + toReplace, " " + toReplace.toUpperCase());
                continue;
            }
            name = name.replace("_", " ");
        }
        name = name.replace(" N ", "'n'");
        String firstLetter = String.valueOf(name.charAt(0));
        return name.replaceFirst(firstLetter, firstLetter.toUpperCase());
    }

    public static TranslatableComponent getVariantNameTooltip(@NotNull String variant, DragonSpeciesHelper helper) {
        if (IoBVariantLoader.clientConfig.displayOriginalVariantName.get())
            return new TranslatableComponent("tooltip.iobvariantloader.variant", new TranslatableComponent(variant).withStyle(ChatFormatting.GOLD));

        if (!variant.isEmpty()) {
            String key = "tooltip.iobvariantloader." + helper.getSpecies(true) + "." + variant;
            if (Language.getInstance().has(key)) {
                return  new TranslatableComponent("tooltip.iobvariantloader.variant", new TranslatableComponent(key).withStyle(ChatFormatting.GOLD));
            } else {
                variant = prepareVariantName(variant);
                return new TranslatableComponent("tooltip.iobvariantloader.variant", new TextComponent(variant).withStyle(ChatFormatting.GOLD));
            }
        } else return new TranslatableComponent("tooltip.iobvariantloader.variant",
                new TextComponent("unknown").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withObfuscated(true)));
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

    public enum AllowedValues {
        DEADLY_NADDER("deadly_nadder"),
        GRONCKLE("gronckle"),
        LIGHT_FURY("light_fury"),
        MONSTROUS_NIGHTMARE("nightmare", "monstrous_nightmare"),
        NIGHT_FURY("night_fury"),
        NIGHT_LIGHT("night_light"),
        SKRILL("skrill"),
        SPEED_STINGER("speed_stinger"),
        SPEED_STINGER_LEADER("speed_stinger_leader"),
        STINGER("stinger"),
        TERRIBLE_TERROR("terrible_terror"),
        TRIPLE_STRYKE("triple_stryke"),
        ZIPPLEBACK("zippleback");

        private final String client;
        private final String server;

        AllowedValues(String client, String server) {
            this.client = client;
            this.server = server;
        }

        AllowedValues(String val) {
            this(val, val);
        }

        public static boolean isValid(String string, boolean isClient) {
            return Arrays.stream(values()).anyMatch(allowedValues -> string.equals(isClient ? allowedValues.client : allowedValues.server));
        }
    }
}
