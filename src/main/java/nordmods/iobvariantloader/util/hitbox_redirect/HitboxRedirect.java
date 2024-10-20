package nordmods.iobvariantloader.util.hitbox_redirect;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HitboxRedirect(@Nullable Pair<Float, Float> hitbox, @Nullable Pair<Float, Float> attackBox, @Nullable Vec3 attackBoxPos, List<Vec3> passengerPositions) {
}
