package nordmods.iobvariantloader.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class NetworkUtil {
    public static void writeString(ByteBuf buf, String text) {
        byte[] temp = text.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(temp.length);
        buf.writeBytes(temp);
    }

    public static String readString(ByteBuf buf) {
        int size = buf.readInt();
        byte[] temp = new byte[size];
        buf.readBytes(temp);

        return new String(temp, StandardCharsets.UTF_8);
    }
}
