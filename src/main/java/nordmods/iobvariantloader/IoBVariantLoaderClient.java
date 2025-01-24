package nordmods.iobvariantloader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectReloadListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IoBVariantLoaderClient {
    public static final Set<UUID> PASSENGERS = new HashSet<>();

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void reloadRedirects(final RegisterClientReloadListenersEvent event) {
            if (FMLLoader.getDist() == Dist.CLIENT) event.registerReloadListener(new ModelRedirectReloadListener());
        }
    }
}
