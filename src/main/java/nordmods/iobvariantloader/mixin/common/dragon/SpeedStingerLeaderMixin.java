package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpeedStingerLeader.class)
public abstract class SpeedStingerLeaderMixin extends ADragonBaseMixin{
    protected SpeedStingerLeaderMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "floutscout";
            case 2 -> "ice_breaker";
            case 3 -> "sweet_sting";
            default -> "speed_stinger";
        };
    }
}
