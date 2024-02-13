package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class VLConfig {
    public ConfigHelper.ConfigValueListener<Double> inheritanceChance;
    public ConfigHelper.ConfigValueListener<Boolean> assignEggVariantOnBreeding;
    public ConfigHelper.ConfigValueListener<Boolean> assignEggVariantOnPlaced;
    public VLConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
    {
        builder.push("Inheritance Chance");
        inheritanceChance = subscriber.subscribe(builder
                .comment("Defines the chance of dragon inheriting variant of their parents. 1 means variant will always be the same as their parents one")
                .defineInRange("inheritance_chance", 0.7, 0, 1));
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
    }
}
