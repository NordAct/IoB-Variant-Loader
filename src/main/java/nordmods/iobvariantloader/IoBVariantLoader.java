package nordmods.iobvariantloader;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import nordmods.iobvariantloader.config.VLClientConfig;
import nordmods.iobvariantloader.config.VLConfig;
import nordmods.iobvariantloader.network.VerifyModPresenceS2CPacket;
import nordmods.iobvariantloader.util.dragonVariant.DragonVariantReloadListener;
import nordmods.iobvariantloader.util.modelRedirect.ModelRedirectReloadListener;
import org.slf4j.Logger;

@Mod("iobvariantloader")
public class IoBVariantLoader {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static VLConfig config = null;
    public static VLClientConfig clientConfig = null;

    public IoBVariantLoader() {
        VerifyModPresenceS2CPacket.init();
        MinecraftForge.EVENT_BUS.register(this);
        config = ConfigHelper.register(ModConfig.Type.COMMON, VLConfig::new, "iob_variant_loader.toml");
        clientConfig = ConfigHelper.register(ModConfig.Type.CLIENT, VLClientConfig::new, "iob_variant_loader-client.toml");
    }

    @SubscribeEvent
    void reloadVariants(final AddReloadListenerEvent event) {
        event.addListener(new DragonVariantReloadListener());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void reloadRedirects(final RegisterClientReloadListenersEvent event) {
            if (FMLLoader.getDist() == Dist.CLIENT) event.registerReloadListener(new ModelRedirectReloadListener());
        }
    }

}
