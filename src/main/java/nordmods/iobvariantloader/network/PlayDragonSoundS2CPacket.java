package nordmods.iobvariantloader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.IoBVariantLoaderClient;

import java.util.function.Supplier;

//for reason 1.18.2 sucking ass
public record PlayDragonSoundS2CPacket(int dragonId, ResourceLocation id, float volume, float pitch) {
    private static final ResourceLocation ID = new ResourceLocation(IoBVariantLoader.MOD_ID, "play_dragon_sound");
    private static final String VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ID)
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    public static PlayDragonSoundS2CPacket read(FriendlyByteBuf byteBuf) {
        int dragonID = byteBuf.readInt();
        ResourceLocation id = new ResourceLocation(NetworkUtil.readString(byteBuf));
        float volume = byteBuf.readFloat();
        float pitch = byteBuf.readFloat();

        return new PlayDragonSoundS2CPacket(dragonID, id, volume, pitch);
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(dragonId());
        NetworkUtil.writeString(byteBuf, id().toString());
        byteBuf.writeFloat(volume());
        byteBuf.writeFloat(pitch());
    }

    public static boolean handleServer(PlayDragonSoundS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
        return true;
    }

    public static void init() {
        SimpleChannel. MessageBuilder<PlayDragonSoundS2CPacket> builder = INSTANCE.messageBuilder(PlayDragonSoundS2CPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayDragonSoundS2CPacket::read)
                .encoder(PlayDragonSoundS2CPacket::write);
        if (FMLLoader.getDist() == Dist.CLIENT) builder.consumer(IoBVariantLoaderClient::handlePlayDragonSoundPacket);
        else builder.consumer(PlayDragonSoundS2CPacket::handleServer);
        builder.add();
    }

}
