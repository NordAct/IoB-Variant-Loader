package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZippleBack.class)
public abstract class ZipplebackMixin extends ADragonBaseMixin{

    protected ZipplebackMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected String getFromBaseVariant() {
        if (isTitanWing()) return "titanstinger";
        return switch (getDragonVariant()) {
            case 1 -> "eclipser";
            case 2 -> "nikora_triple_stryke";
            case 3 -> "starstreak";
            case 4 -> "triple_stryke";
            case 5 -> "spyro";
            case 6 -> "blue";
            case 7 -> "boreas";
            case 8 -> "sleuther";
            case 9 -> "rosethorn";
            default -> "champion";
        };
    }
}
