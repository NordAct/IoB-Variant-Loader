package nordmods.iobvariantloader.util.ducks;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

public interface HitboxRedirectHelper {
    void resetHitboxData();
    EntityDimensions getAttackBox();
    Vec3 getAttackBoxPos();
}
