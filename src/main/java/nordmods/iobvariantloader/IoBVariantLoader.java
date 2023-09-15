package nordmods.iobvariantloader;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import nordmods.iobvariantloader.util.dragonVariant.DragonVariantReloadListener;
import nordmods.iobvariantloader.util.modelRedirect.ModelRedirectReloadListener;
import org.slf4j.Logger;

@Mod("iobvariantloader")
public class IoBVariantLoader {
    public static final Logger LOGGER = LogUtils.getLogger();

    public IoBVariantLoader() {
        MinecraftForge.EVENT_BUS.addListener(this::reloadVariants);
        MinecraftForge.EVENT_BUS.register(this);
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
