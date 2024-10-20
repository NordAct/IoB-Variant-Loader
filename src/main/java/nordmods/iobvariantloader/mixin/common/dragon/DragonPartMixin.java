package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.EntityDimensions;
import nordmods.iobvariantloader.util.AttackBoxRedirectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ADragonBase.DragonPart.class)
public abstract class DragonPartMixin implements AttackBoxRedirectHelper {
    @Shadow private ADragonBase parent;

    @Shadow private EntityDimensions size;

    @Override
    public void setAttackBoxOverride(EntityDimensions state) {
        size = state;
    }
}
