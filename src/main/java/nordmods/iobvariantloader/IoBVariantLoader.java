package nordmods.iobvariantloader;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import nordmods.iobvariantloader.config.VLClientConfig;
import nordmods.iobvariantloader.config.VLConfig;
import nordmods.iobvariantloader.network.PlayDragonSoundS2CPacket;
import nordmods.iobvariantloader.network.SyncSoundRedirectsWithClientS2CPacket;
import nordmods.iobvariantloader.network.VerifyModPresenceS2CPacket;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerReloadListener;
import nordmods.iobvariantloader.util.hitbox_redirect.HitboxRedirectReloadListener;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectReloadListener;
import org.slf4j.Logger;

@Mod(IoBVariantLoader.MOD_ID)
public class IoBVariantLoader { //I'll do anything but use forge's registries because this is legit porn
    public static final String MOD_ID = "iobvariantloader";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static VLConfig config = null;
    public static VLClientConfig clientConfig = null;

    public IoBVariantLoader() {
        VerifyModPresenceS2CPacket.init();
        SyncSoundRedirectsWithClientS2CPacket.init();
        PlayDragonSoundS2CPacket.init();
        MinecraftForge.EVENT_BUS.register(this);
        config = ConfigHelper.register(ModConfig.Type.COMMON, VLConfig::new, "iob_variant_loader.toml");
        clientConfig = ConfigHelper.register(ModConfig.Type.CLIENT, VLClientConfig::new, "iob_variant_loader-client.toml");
    }

    @SubscribeEvent
    void reloadVariants(final AddReloadListenerEvent event) {
        event.addListener(new DragonVariantSpawnerReloadListener());
        event.addListener(new HitboxRedirectReloadListener());
        event.addListener(new SoundRedirectReloadListener());
    }
}
