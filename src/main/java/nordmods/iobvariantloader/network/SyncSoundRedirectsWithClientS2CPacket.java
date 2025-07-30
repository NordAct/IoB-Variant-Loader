package nordmods.iobvariantloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirect;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

//ffs, I really hate bs Forge made people do to make packets. This is atrocious
public class SyncSoundRedirectsWithClientS2CPacket { //todo
    private final Map<String, Map<String, List<SoundRedirect>>> soundRedirectMap;
    private static final ResourceLocation ID = new ResourceLocation(IoBVariantLoader.MOD_ID, "sync_sound_redirects");
    private static final String VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ID)
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    public SyncSoundRedirectsWithClientS2CPacket(Map<String, Map<String, List<SoundRedirect>>> soundRedirectMap) {
        this.soundRedirectMap = soundRedirectMap;
    }

    public static SyncSoundRedirectsWithClientS2CPacket read(FriendlyByteBuf byteBuf) {
        Map<String, Map<String, List<SoundRedirect>>> map = byteBuf.readMap(
                SyncSoundRedirectsWithClientS2CPacket::readString,
                byteBuf1 ->
                        byteBuf1.readMap(
                                SyncSoundRedirectsWithClientS2CPacket::readString,
                                SyncSoundRedirectsWithClientS2CPacket::readSoundRedirectList

                )
        );

        return new SyncSoundRedirectsWithClientS2CPacket(map);
    }

    public void write(FriendlyByteBuf byteBuf) {
        byteBuf.writeMap(SoundRedirectUtil.soundRedirectMap,
                SyncSoundRedirectsWithClientS2CPacket::writeString,
                (byteBuf1, variantSoundRedirects) ->
                        byteBuf1.writeMap(variantSoundRedirects,
                                SyncSoundRedirectsWithClientS2CPacket::writeString,
                                SyncSoundRedirectsWithClientS2CPacket::writeSoundRedirectList
                        )
        );
    }

    @SuppressWarnings("SameReturnValue")
    public static boolean handle(SyncSoundRedirectsWithClientS2CPacket packet, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
        if (context.getDirection().getReceptionSide().isClient()) {
            SoundRedirectUtil.soundRedirectMap.clear();
            SoundRedirectUtil.soundRedirectMap.putAll(packet.soundRedirectMap);
        }
        return true;
    }

    public static void init() {
        INSTANCE.messageBuilder(SyncSoundRedirectsWithClientS2CPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSoundRedirectsWithClientS2CPacket::read)
                .encoder(SyncSoundRedirectsWithClientS2CPacket::write)
                .consumer(SyncSoundRedirectsWithClientS2CPacket::handle)
                .add();
        MinecraftForge.EVENT_BUS.addListener(SyncSoundRedirectsWithClientS2CPacket::onJoinedEvent);
    }

    private static void onJoinedEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer && SyncSoundRedirectsWithClientS2CPacket.INSTANCE.isRemotePresent(serverPlayer.connection.getConnection()) && !serverPlayer.connection.getConnection().isMemoryConnection())
            SyncSoundRedirectsWithClientS2CPacket.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncSoundRedirectsWithClientS2CPacket(SoundRedirectUtil.soundRedirectMap));
    }

    private static void writeString(ByteBuf buf, String text) {
        byte[] temp = text.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(temp.length);
        buf.writeBytes(temp);
    }

    private static String readString(ByteBuf buf) {
        int size = buf.readInt();
        byte[] temp = new byte[size];
        buf.readBytes(temp);

        return new String(temp, StandardCharsets.UTF_8);
    }

    private static void writeSoundRedirectList(ByteBuf buf, List<SoundRedirect> list) {
        buf.writeInt(list.size());
        list.forEach(soundRedirect -> {
            writeString(buf, soundRedirect.name());
            writeString(buf, soundRedirect.sound());
            buf.writeFloat(soundRedirect.volume());
            buf.writeFloat(soundRedirect.pitch());
        });
    }

    private static List<SoundRedirect> readSoundRedirectList(ByteBuf buf) {
        int size = buf.readInt();
        List<SoundRedirect> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String name = readString(buf);
            String sound = readString(buf);
            float volume = buf.readFloat();
            float pitch = buf.readFloat();
            list.add(new SoundRedirect(name, sound, volume, pitch));
        }
        return list;
    }
}
