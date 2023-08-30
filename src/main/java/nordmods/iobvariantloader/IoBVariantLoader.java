package nordmods.iobvariantloader;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nordmods.iobvariantloader.util.DragonVariantReloadListener;
import org.slf4j.Logger;

@Mod("iobvariantloader")
public class IoBVariantLoader {
    public static final Logger LOGGER = LogUtils.getLogger();

    public IoBVariantLoader() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::reloadVariants);
    }

    @SubscribeEvent
    void reloadVariants(final AddReloadListenerEvent event) {
        event.addListener(new DragonVariantReloadListener());
    }
}
