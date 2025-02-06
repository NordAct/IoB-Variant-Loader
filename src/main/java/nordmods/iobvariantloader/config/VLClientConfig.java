package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class VLClientConfig {
    public ConfigHelper.ConfigValueListener<Boolean> disableGlowing;
    public ConfigHelper.ConfigValueListener<Boolean> disableNamedVariants;
    public ConfigHelper.ConfigValueListener<Boolean> logModelRedirects;
    public ConfigHelper.ConfigValueListener<Boolean> generateTranslations;
    public ConfigHelper.ConfigValueListener<List<String>> ignoredByGenerator;
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

        builder.push("Log Model Redirects");
        logModelRedirects = subscriber.subscribe(builder
                .comment("Logs any added model redirect in console for easier debugging")
                .define("log_model_redirects", false));
        builder.pop();

        builder.push("Generate Translations");
        generateTranslations = subscriber.subscribe(builder
                .comment("Automatically generates translations for all variants for english language based on presented model redirects for dragons." +
                        "\nAll generated lines will be printed to log file" +
                        "\nUse this only if you're pack developer for making your life easier")
                .define("generate_translations", false));
        builder.pop();

        builder.push("Variants Ignored by Generator");
        ignoredByGenerator = subscriber.subscribe(builder
                .comment("Prints for variants that have same name as any value in this list will be skipped when translation key generator is used")
                .define("ignored_by_generator", defaultIgnored));
        builder.pop();
    }

    private static List<String> defaultIgnored = new ArrayList<>();

    static {
                defaultIgnored.add("stinger_color_layer");
                defaultIgnored.add("barklethorn");
                defaultIgnored.add("barklethorn_membranes");
                defaultIgnored.add("bork_week");
                defaultIgnored.add("bork_week_membranes");
                defaultIgnored.add("deadly_nadder");
                defaultIgnored.add("deadly_nadder_membranes");
                defaultIgnored.add("equipment");
                defaultIgnored.add("flystorm");
                defaultIgnored.add("flystorm_membranes");
                defaultIgnored.add("hjaldr");
                defaultIgnored.add("hjaldr_membranes");
                defaultIgnored.add("hjarta");
                defaultIgnored.add("hjarta_membranes");
                defaultIgnored.add("kingstail");
                defaultIgnored.add("kingstail_membranes");
                defaultIgnored.add("lethal_lancebeak");
                defaultIgnored.add("lethal_lancebeak_membranes");
                defaultIgnored.add("scardian");
                defaultIgnored.add("scardian_membranes");
                defaultIgnored.add("seedling_stormpest");
                defaultIgnored.add("seedling_stormpest_membranes");
                defaultIgnored.add("springshedder");
                defaultIgnored.add("springshedder_membranes");
                defaultIgnored.add("stormfly");
                defaultIgnored.add("stormfly_membranes");
                defaultIgnored.add("barn");
                defaultIgnored.add("cheesemonger");
                defaultIgnored.add("crubble");
                defaultIgnored.add("exiled");
                defaultIgnored.add("gronckle");
                defaultIgnored.add("hjarta");
                defaultIgnored.add("junior_tuffnut_junior");
                defaultIgnored.add("meatlug");
                defaultIgnored.add("rubblegrubber");
                defaultIgnored.add("yawnckle");
                defaultIgnored.add("blue_glow");
                defaultIgnored.add("drottinn");
                defaultIgnored.add("drottinn_grogaldr");
                defaultIgnored.add("grogaldr");
                defaultIgnored.add("light_fury");
                defaultIgnored.add("purple_glow");
                defaultIgnored.add("sveinn");
                defaultIgnored.add("sveinn_grogaldr");
                defaultIgnored.add("albino");
                defaultIgnored.add("arsian");
                defaultIgnored.add("karma");
                defaultIgnored.add("night_fury");
                defaultIgnored.add("night_fury_glow");
                defaultIgnored.add("sentinel");
                defaultIgnored.add("sentinel_glow");
                defaultIgnored.add("svartr");
                defaultIgnored.add("toothless");
                defaultIgnored.add("appaloosa");
                defaultIgnored.add("ashwing");
                defaultIgnored.add("dart");
                defaultIgnored.add("dusk");
                defaultIgnored.add("harlequeen");
                defaultIgnored.add("iridescence");
                defaultIgnored.add("leopard");
                defaultIgnored.add("nightdancer");
                defaultIgnored.add("pouncer");
                defaultIgnored.add("ruffrunner");
                defaultIgnored.add("skydancer");
                defaultIgnored.add("bloodroot");
                defaultIgnored.add("burlystorm");
                defaultIgnored.add("cardinal");
                defaultIgnored.add("carnation");
                defaultIgnored.add("fanghook");
                defaultIgnored.add("fangmaster");
                defaultIgnored.add("fire");
                defaultIgnored.add("hellebore");
                defaultIgnored.add("hookfang");
                defaultIgnored.add("mountain");
                defaultIgnored.add("rainbow");
                defaultIgnored.add("sunfyre");
                defaultIgnored.add("tsukarion");
                defaultIgnored.add("wolfsbane");
                defaultIgnored.add("albino");
                defaultIgnored.add("crimson");
                defaultIgnored.add("fryrir");
                defaultIgnored.add("icebane");
                defaultIgnored.add("lightning");
                defaultIgnored.add("moonshock");
                defaultIgnored.add("nemesis");
                defaultIgnored.add("pickle");
                defaultIgnored.add("skrill");
                defaultIgnored.add("spark");
                defaultIgnored.add("spring_storm");
                defaultIgnored.add("stormshadow");
                defaultIgnored.add("tempest");
                defaultIgnored.add("zen");
                defaultIgnored.add("floutscout");
                defaultIgnored.add("ice_breaker");
                defaultIgnored.add("speed_stinger");
                defaultIgnored.add("speed_stinger_titan");
                defaultIgnored.add("sweet_sting");
                defaultIgnored.add("badlands");
                defaultIgnored.add("chest");
                defaultIgnored.add("coastal_predator");
                defaultIgnored.add("collar");
                defaultIgnored.add("egg");
                defaultIgnored.add("kingly");
                defaultIgnored.add("mudsmasher");
                defaultIgnored.add("sandscorcher");
                defaultIgnored.add("titanstinger");
                defaultIgnored.add("wildroar");
                defaultIgnored.add("blar");
                defaultIgnored.add("blar_sleeping");
                defaultIgnored.add("chomp");
                defaultIgnored.add("chomp_sleeping");
                defaultIgnored.add("head");
                defaultIgnored.add("head_sleeping");
                defaultIgnored.add("iggy");
                defaultIgnored.add("iggy_sleeping");
                defaultIgnored.add("pain");
                defaultIgnored.add("pain_sleeping");
                defaultIgnored.add("sharpshot");
                defaultIgnored.add("sharpshot_sleeping");
                defaultIgnored.add("sneaky");
                defaultIgnored.add("sneaky_sleeping");
                defaultIgnored.add("terrible_terror");
                defaultIgnored.add("terrible_terror_sleeping");
                defaultIgnored.add("terror");
                defaultIgnored.add("terror_sleeping");
                defaultIgnored.add("titan_wing");
                defaultIgnored.add("titan_wing_sleeping");
                defaultIgnored.add("blue");
                defaultIgnored.add("boreas");
                defaultIgnored.add("champion");
                defaultIgnored.add("eclipser");
                defaultIgnored.add("nikora_triple_stryke");
                defaultIgnored.add("rosethorn");
                defaultIgnored.add("sleuther");
                defaultIgnored.add("spyro");
                defaultIgnored.add("starstreak");
                defaultIgnored.add("stinger_color_layer");
                defaultIgnored.add("triple_stryke");
                defaultIgnored.add("fart_n_sniff");
                defaultIgnored.add("hamfeist");
                defaultIgnored.add("hjarta");
                defaultIgnored.add("hodd");
                defaultIgnored.add("kandy_n_kane");
                defaultIgnored.add("leaf_n_bark");
                defaultIgnored.add("pistill");
                defaultIgnored.add("purple_n_nurple");
                defaultIgnored.add("sandr");
                defaultIgnored.add("whip_n_lash");
    }
}
