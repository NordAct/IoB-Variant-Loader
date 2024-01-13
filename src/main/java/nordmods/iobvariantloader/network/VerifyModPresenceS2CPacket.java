package nordmods.iobvariantloader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.function.Supplier;

public class VerifyModPresenceS2CPacket {
    private static final ResourceLocation ID = new ResourceLocation("iobvariantloader", "mod_presence_check");
    private static final String VERSION = ModList.get().getModContainerById("iobvariantloader").map(ModContainer::getModInfo).map(IModInfo::getVersion).map(ArtifactVersion::toString).orElse("[UNKNOWN]");
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ID)
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    public VerifyModPresenceS2CPacket() {}

    public static VerifyModPresenceS2CPacket read(FriendlyByteBuf byteBuf) {
        return new VerifyModPresenceS2CPacket();
    }

    @SuppressWarnings("EmptyMethod")
    public void write(FriendlyByteBuf byteBuf) {}

    @SuppressWarnings("SameReturnValue")
    public static boolean handle(VerifyModPresenceS2CPacket packet, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
        return true;
    }

    public static void init() {
        INSTANCE.messageBuilder(VerifyModPresenceS2CPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(VerifyModPresenceS2CPacket::read)
                .encoder(VerifyModPresenceS2CPacket::write)
                .consumer(VerifyModPresenceS2CPacket::handle)
                .add();
        MinecraftForge.EVENT_BUS.addListener(VerifyModPresenceS2CPacket::onJoinedEvent);
    }

    private static void onJoinedEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer && VerifyModPresenceS2CPacket.INSTANCE.isRemotePresent(serverPlayer.connection.getConnection()) && !serverPlayer.connection.getConnection().isMemoryConnection())
            VerifyModPresenceS2CPacket.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new VerifyModPresenceS2CPacket());
    }
}
