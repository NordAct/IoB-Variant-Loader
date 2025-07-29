package nordmods.iobvariantloader.util.hitbox_redirect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import nordmods.iobvariantloader.util.VLCodecs;

import java.util.List;
import java.util.Optional;

public record HitboxRedirect(
        Optional<Pair<Float, Float>> hitbox,
        Optional<Pair<Float, Float>>attackBox,
        Optional<Vec3> attackBoxPos,
        List<Vec3> passengerPositions
) {
    public static Codec<HitboxRedirect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.pair(
                    Codec.FLOAT.fieldOf("width").codec(),
                    Codec.FLOAT.fieldOf("height").codec()
            ).optionalFieldOf("hitbox").forGetter(HitboxRedirect::hitbox),
            Codec.pair(Codec.FLOAT.fieldOf("width").codec(),
                    Codec.FLOAT.fieldOf("height").codec()
            ).optionalFieldOf("hitbox").forGetter(HitboxRedirect::attackBox),
            VLCodecs.VEC_3.optionalFieldOf("attack_box_position").forGetter(HitboxRedirect::attackBoxPos),
            VLCodecs.VEC_3.listOf().optionalFieldOf("passenger_positions", List.of()).forGetter(HitboxRedirect::passengerPositions)
    ).apply(instance, HitboxRedirect::new));
}
