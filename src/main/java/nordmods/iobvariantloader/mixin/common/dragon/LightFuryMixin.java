package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.lightfury.LightFury;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LightFury.class)
public abstract class LightFuryMixin extends ADragonBaseMixin{
    protected LightFuryMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "sveinn";
            case 2 -> "drottinn";
            case 3 -> "grogaldr";
            case 4 -> "drottinn_grogaldr";
            case 5 -> "sveinn_grogaldr";
            default -> "light_fury";
        };
    }

}
