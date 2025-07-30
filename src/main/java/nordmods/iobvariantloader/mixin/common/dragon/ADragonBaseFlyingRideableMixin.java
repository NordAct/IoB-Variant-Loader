package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ADragonBaseFlyingRideable.class)
public abstract class ADragonBaseFlyingRideableMixin extends ADragonRideableUtilityMixin {
    @Shadow public abstract boolean isFlying();

    protected ADragonBaseFlyingRideableMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    //*звуки тихого ахуя*
    @Override
    public void playAmbientSound() {
        if (!isFlying()) super.playAmbientSound();
    }
}
