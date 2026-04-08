package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class VLConfig {
    public ConfigHelper.ConfigValueListener<Double> inheritanceChance;
    public ConfigHelper.ConfigValueListener<Boolean> alternativeLandNavigation;
    public ConfigHelper.ConfigValueListener<Boolean> assignEggVariantOnBreeding;
    public ConfigHelper.ConfigValueListener<Boolean> assignEggVariantOnPlaced;
    public ConfigHelper.ConfigValueListener<Boolean> ignoreBreedingListsInheritanceChance;
    public ConfigHelper.ConfigValueListener<Boolean> ignoreBreedingLists;
    public ConfigHelper.ConfigValueListener<Boolean> enforceBreedingLists;
    public ConfigHelper.ConfigValueListener<Boolean> logDragonVariantSpawns;
    public ConfigHelper.ConfigValueListener<Boolean> logHitboxRedirects;
    public ConfigHelper.ConfigValueListener<Boolean> logSoundRedirects;
    public ConfigHelper.ConfigValueListener<Boolean> logExtras;
    public ConfigHelper.ConfigValueListener<Boolean> logVariantLists;
    public ConfigHelper.ConfigValueListener<Boolean> logBreedingLists;
    public VLConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
    {
        builder.push("Inheritance Chance");
        inheritanceChance = subscriber.subscribe(builder
                .comment("Defines the chance of dragon inheriting variant of their parents. 1 means variant will always be the same as their parents one")
                .defineInRange("inheritance_chance", 0.7, 0, 1));
        builder.pop();

        builder.push("Alternative Land Navigation");
        alternativeLandNavigation = subscriber.subscribe(builder
                .comment("Uses alternative improved navigator for dragons when they're on land. If false, default one will be used")
                .define("alternative_land_navigation", true));
        builder.pop();

        builder.push("Assign Egg Variant on Breeding");
        assignEggVariantOnBreeding = subscriber.subscribe(builder
                .comment("Enables variant assignment on breeding. If false, all eggs always will have no variant assigned during breeding")
                .define("assign_egg_variant_on_breeding", true));
        builder.pop();

        builder.push("Assign Egg Variant on Placed");
        assignEggVariantOnPlaced = subscriber.subscribe(builder
                .comment("Enables variant assignment when egg is placed and has empty or invalid variant tag. If false, all eggs always will have no variant assigned when placed")
                .define("assign_egg_variant_on_placed", false));
        builder.pop();

        builder.push("Log Variant Spawns");
        logDragonVariantSpawns = subscriber.subscribe(builder
                .comment("Logs any added variant spawn in console for easier debugging")
                .define("log_variant_spawns", false));
        builder.pop();

        builder.push("Log Hitbox Redirects");
        logHitboxRedirects = subscriber.subscribe(builder
                .comment("Logs any added hitbox redirects in console for easier debugging")
                .define("log_hitbox_redirects", false));
        builder.pop();

        builder.push("Log Sound Redirects");
        logSoundRedirects = subscriber.subscribe(builder
                .comment("Logs any added sound redirects in console for easier debugging")
                .define("log_sound_redirects", false));
        builder.pop();

        builder.push("Log Extras");
        logExtras = subscriber.subscribe(builder
                .comment("Logs any added extras in console for easier debugging")
                .define("log_extras", false));
        builder.pop();

        builder.push("Log Variant Lists");
        logVariantLists = subscriber.subscribe(builder
                .comment("Logs any added variant lists in console for easier debugging")
                .define("log_variant_lists", false));
        builder.pop();

        builder.push("Log Breeding Lists");
        logBreedingLists = subscriber.subscribe(builder
                .comment("Logs any added breeding lists in console for easier debugging")
                .define("log_breeding_lists", false));
        builder.pop();

        builder.push("Ignore Breeding Lists");
        ignoreBreedingLists = subscriber.subscribe(builder
                .comment("Ignores breeding lists during dragon breeding")
                .define("ignore_breeding_lists", false));
        builder.pop();

        builder.push("Enforce Breeding Lists");
        enforceBreedingLists = subscriber.subscribe(builder
                .comment("Enforces breeding lists during dragon breeding, making it impossible for dragons that don't included in any breeding list to breed")
                .define("enforce_breeding_lists", false));
        builder.pop();

    }
}
