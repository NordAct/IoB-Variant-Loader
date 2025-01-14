package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MonstrousNightmare.class)
public abstract class MonstrousNightmareMixin extends ADragonBaseMixin{
    protected MonstrousNightmareMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "mountain";
            case 2 -> "bloodroot";
            case 3 -> "fangmaster";
            case 4 -> "burlystorm";
            case 5 -> "wolfsbane";
            case 6 -> "exiled";
            case 7 -> "hellebore";
            case 8 -> "carnation";
            case 9 -> "cardinal";
            case 10 -> "sunfyre";
            case 11 -> "rainbow";
            case 12 -> "tsukarion";
            case 13 -> "fanghook";
            default -> "hookfang";
        };
    }
}
