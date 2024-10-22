package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.skrill.Skrill;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Skrill.class)
public abstract class SkrillMixin extends ADragonBaseMixin{
    protected SkrillMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "stormshadow";
            case 2 -> "icebane";
            case 3 -> "fryrir";
            case 4 -> "crimson";
            case 5 -> "nemesis";
            case 6 -> "tempest";
            case 7 -> "pickle";
            case 8 -> "zen";
            case 9 -> "spark";
            case 10 -> "albino";
            case 11 -> "moonshock";
            case 12 -> "spring_storm";
            default -> "skrill";
        };
    }
}
