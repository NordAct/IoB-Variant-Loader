package nordmods.iobvariantloader;

import com.GACMD.isleofberk.config.util.ConfigHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import nordmods.iobvariantloader.config.VLClientConfig;
import nordmods.iobvariantloader.config.VLConfig;
import nordmods.iobvariantloader.network.PlayDragonSoundS2CPacket;
import nordmods.iobvariantloader.network.SyncSoundRedirectsWithClientS2CPacket;
import nordmods.iobvariantloader.network.VerifyModPresenceS2CPacket;
import nordmods.iobvariantloader.util.SetVariantFromCollectionsFunction;
import nordmods.iobvariantloader.util.breeding_list.BreedingListReloadListener;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerReloadListener;
import nordmods.iobvariantloader.util.extras.ExtrasReloadListener;
import nordmods.iobvariantloader.util.hitbox_redirect.HitboxRedirectReloadListener;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectReloadListener;
import nordmods.iobvariantloader.util.variant_collections.KillVariantFromCollectionTrigger;
import nordmods.iobvariantloader.util.variant_collections.TameVariantFromCollectionTrigger;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsReloadListener;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(IoBVariantLoader.MOD_ID)
public class IoBVariantLoader {
    //I'll do anything but use forge's registries because this is legit porn
    // 08.08.26 - even if I have to, I do not retract my statement
    public static final String MOD_ID = "iobvariantloader";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static VLConfig config = null;
    public static VLClientConfig clientConfig = null;
    public static KillVariantFromCollectionTrigger KILL_VARIANT_FROM_GROUP_TRIGGER = CriteriaTriggers.register(new KillVariantFromCollectionTrigger());
    public static TameVariantFromCollectionTrigger TAME_VARIANT_FROM_GROUP_TRIGGER = CriteriaTriggers.register(new TameVariantFromCollectionTrigger());

    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY.location(), MOD_ID);
    public static final Supplier<LootItemFunctionType> SET_VARIANT_FROM_COLLECTION = LOOT_FUNCTIONS.register(
            "set_variant_from_collection",
            () -> new LootItemFunctionType(new  SetVariantFromCollectionsFunction.Serializer())
    );

    public IoBVariantLoader() {
        VerifyModPresenceS2CPacket.init();
        SyncSoundRedirectsWithClientS2CPacket.init();
        PlayDragonSoundS2CPacket.init();
        MinecraftForge.EVENT_BUS.register(this);
        config = ConfigHelper.register(ModConfig.Type.COMMON, VLConfig::new, "iob_variant_loader.toml");
        clientConfig = ConfigHelper.register(ModConfig.Type.CLIENT, VLClientConfig::new, "iob_variant_loader-client.toml");
        LOOT_FUNCTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    void reloadVariants(final AddReloadListenerEvent event) {
        event.addListener(new VariantCollectionsReloadListener());
        event.addListener(new BreedingListReloadListener());
        event.addListener(new DragonVariantSpawnerReloadListener());
        event.addListener(new HitboxRedirectReloadListener());
        event.addListener(new SoundRedirectReloadListener());
        event.addListener(new ExtrasReloadListener());
    }
}
