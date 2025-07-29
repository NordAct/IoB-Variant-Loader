package nordmods.iobvariantloader.util;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VLCodecs {
    //Hey, look, this little thing EXISTS IN VANILLA in newer versions
    //That's part of why you shouldn't make mods on old crap like 1.18
    public static final Codec<Vec3> VEC_3 = Codec.DOUBLE.listOf().comapFlatMap(
                    coordinates -> Util.fixedSize(coordinates, 3).map(list -> new Vec3(list.get(0), list.get(1), list.get(2))),
                    vec -> List.of(vec.x(), vec.y(), vec.z())
            );
}
