package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TerribleTerror.class)
public abstract class TerribleTerrorMixin extends ADragonBaseMixin{

    protected TerribleTerrorMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected String getFromBaseVariant() {
        if (isTitanWing()) return "titan_wing";
        return switch (getDragonVariant()) {
            case 1 -> "blar";
            case 2 -> "sneaky";
            case 3 -> "terror";
            case 4 -> "sharpshot";
            case 5 -> "iggy";
            case 6 -> "pain";
            case 7 -> "head";
            case 8 -> "chomp";
            default -> "terrible_terror";
        };
    }
}
