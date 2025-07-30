package nordmods.iobvariantloader;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;
import nordmods.iobvariantloader.network.PlayDragonSoundS2CPacket;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectReloadListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class IoBVariantLoaderClient {
    public static final Set<UUID> PASSENGERS = new HashSet<>();

    @SuppressWarnings("SameReturnValue")
    public static boolean handlePlayDragonSoundPacket(PlayDragonSoundS2CPacket packet, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
        if (context.getDirection().getReceptionSide().isClient() && Minecraft.getInstance().level != null) {
            Entity dragon = Minecraft.getInstance().level.getEntity(packet.dragonId());
            if (dragon != null) Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, dragon, new SoundEvent(packet.id()), dragon.getSoundSource(), packet.volume(), packet.pitch());
        }
        return true;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void reloadRedirects(final RegisterClientReloadListenersEvent event) {
            if (FMLLoader.getDist() == Dist.CLIENT) event.registerReloadListener(new ModelRedirectReloadListener());
        }
    }
}
