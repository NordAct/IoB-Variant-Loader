package nordmods.iobvariantloader.config;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class VLClientConfig {
    public ConfigHelper.ConfigValueListener<Boolean> disableGlowing;
    public ConfigHelper.ConfigValueListener<Boolean> disableNamedVariants;
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
    }
}
