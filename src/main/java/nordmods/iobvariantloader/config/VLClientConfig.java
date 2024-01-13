package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class VLClientConfig {
    public ConfigHelper.ConfigValueListener<Boolean> disableGlowing;
    public ConfigHelper.ConfigValueListener<Boolean> disableNamedVariants;
    public ConfigHelper.ConfigValueListener<Boolean> generateTranslations;
    public ConfigHelper.ConfigValueListener<List<String>> ignoredByGenerator;
    public ConfigHelper.ConfigValueListener<List<String>> ignoredByGeneratorEndings;
    public VLClientConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
    {
        builder.push("Disable Glowing");
        disableGlowing = subscriber.subscribe(builder
                .comment("Disables glowing layer on textures")
                .define("disable_glowing", false));
        builder.pop();

        builder.push("Disable Named Variants");
        disableNamedVariants = subscriber.subscribe(builder
                .comment("Disables variant display via nametag")
                .define("disable_named_variants", false));
        builder.pop();

        builder.push("Generate Translations");
        generateTranslations = subscriber.subscribe(builder
                .comment("Automatically generates translations for all variants for english language based on presented textures for dragons." +
                        "\nAll generated lines will be printed to log file" +
                        "\nUse this only if you're pack developer for making your life easier")
                .define("generate_translations", false));
        builder.pop();

        builder.push("Textures Ignored by Generator");
        ignoredByGenerator = subscriber.subscribe(builder
                .comment("Prints for textures that have same name as any value in this list will be skipped when translation key generator is used")
                .define("ignored_by_generator",
                        Arrays.stream(new String[]{
                                "stinger_color_layer",
                                "barklethorn",
                                "barklethorn_membranes",
                                "bork_week",
                                "bork_week_membranes",
                                "deadly_nadder",
                                "deadly_nadder_membranes",
                                "equipment",
                                "flystorm",
                                "flystorm_membranes",
                                "hjaldr",
                                "hjaldr_membranes",
                                "hjarta",
                                "hjarta_membranes",
                                "kingstail",
                                "kingstail_membranes",
                                "lethal_lancebeak",
                                "lethal_lancebeak_membranes",
                                "scardian",
                                "scardian_membranes",
                                "seedling_stormpest",
                                "seedling_stormpest_membranes",
                                "springshedder",
                                "springshedder_membranes",
                                "stormfly",
                                "stormfly_membranes",
                                "barn",
                                "cheesemonger",
                                "crubble",
                                "exiled",
                                "gronckle",
                                "hjarta",
                                "junior_tuffnut_junior",
                                "meatlug",
                                "rubblegrubber",
                                "yawnckle",
                                "blue_glow",
                                "drottinn",
                                "drottinn_grogaldr",
                                "grogaldr",
                                "light_fury",
                                "purple_glow",
                                "sveinn",
                                "sveinn_grogaldr",
                                "albino",
                                "arsian",
                                "karma",
                                "night_fury",
                                "night_fury_glow",
                                "sentinel",
                                "sentinel_glow",
                                "svartr",
                                "toothless",
                                "appaloosa",
                                "ashwing",
                                "dart",
                                "dusk",
                                "harlequeen",
                                "iridescence",
                                "leopard",
                                "nightdancer",
                                "pouncer",
                                "ruffrunner",
                                "skydancer",
                                "bloodroot",
                                "burlystorm",
                                "cardinal",
                                "carnation",
                                "fanghook",
                                "fangmaster",
                                "fire",
                                "hellebore",
                                "hookfang",
                                "mountain",
                                "rainbow",
                                "sunfyre",
                                "tsukarion",
                                "wolfsbane",
                                "albino",
                                "crimson",
                                "fryrir",
                                "icebane",
                                "lightning",
                                "moonshock",
                                "nemesis",
                                "pickle",
                                "skrill",
                                "spark",
                                "spring_storm",
                                "stormshadow",
                                "tempest",
                                "zen",
                                "floutscout",
                                "ice_breaker",
                                "speed_stinger",
                                "speed_stinger_titan",
                                "sweet_sting",
                                "badlands",
                                "chest",
                                "coastal_predator",
                                "collar",
                                "egg",
                                "kingly",
                                "mudsmasher",
                                "sandscorcher",
                                "titanstinger",
                                "wildroar",
                                "blar",
                                "blar_sleeping",
                                "chomp",
                                "chomp_sleeping",
                                "head",
                                "head_sleeping",
                                "iggy",
                                "iggy_sleeping",
                                "pain",
                                "pain_sleeping",
                                "sharpshot",
                                "sharpshot_sleeping",
                                "sneaky",
                                "sneaky_sleeping",
                                "terrible_terror",
                                "terrible_terror_sleeping",
                                "terror",
                                "terror_sleeping",
                                "titan_wing",
                                "titan_wing_sleeping",
                                "blue",
                                "boreas",
                                "champion",
                                "eclipser",
                                "nikora_triple_stryke",
                                "rosethorn",
                                "sleuther",
                                "spyro",
                                "starstreak",
                                "stinger_color_layer",
                                "triple_stryke",
                                "fart_n_sniff",
                                "hamfeist",
                                "hjarta",
                                "hodd",
                                "kandy_n_kane",
                                "leaf_n_bark",
                                "pistill",
                                "purple_n_nurple",
                                "sandr",
                                "whip_n_lash"
                        }).toList()));
        builder.pop();

        builder.push("Textures Endings Ignored by Generator");
        ignoredByGeneratorEndings = subscriber.subscribe(builder
                .comment("Similar to previous one, but used only when texture ends with listed values")
                .define("ignored_by_generator_endings",
                        Arrays.stream(new String[]{
                                "_leader",
                                "_glowing",
                                "_membranes"
                        }).toList()));
        builder.pop();
    }
}
