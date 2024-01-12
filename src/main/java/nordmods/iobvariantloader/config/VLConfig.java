package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class VLConfig {
    public ConfigHelper.ConfigValueListener<Double> inheritanceChance;
    public VLConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
    {
        builder.push("Inheritance Chance");
        inheritanceChance = subscriber.subscribe(builder
                .comment("Defines the chance of dragon inheriting variant of their parents. 1 means variant will always be the same as their parents one")
                .defineInRange("inheritance_chance", 0.7, 0, 1));
        builder.pop();
    }
}
