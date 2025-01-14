package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.nightfury.NightFury;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NightFury.class)
public abstract class NightFuryMixin extends ADragonBaseMixin{

    protected NightFuryMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "sentinel";
            case 2 -> "karma";
            case 3 -> "arsian";
            case 4 -> "svartr";
            case 5 -> "albino";
            case 101 -> "toothless";
            default -> "night_fury";
        };
    }
}
