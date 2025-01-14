package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.stinger.Stinger;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Stinger.class)
public abstract class StingerMixin extends ADragonBaseMixin {

    protected StingerMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 0, argsOnly = true, remap = false)
    private double adjustBox1(double value) {
        return getAttackBoxPos().x * Mth.sin(-getYRot() * Mth.DEG_TO_RAD);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 1, argsOnly = true, remap = false)
    private double adjustBox2(double value) {
        return getAttackBoxPos().y;
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 2, argsOnly = true, remap = false)
    private double adjustBox3(double value) {
        return getAttackBoxPos().z * Mth.cos(getYRot() * Mth.DEG_TO_RAD);
    }

    @Override
    public String getFromBaseVariant() {
        if (isTitanWing()) return "titanstinger";
        return switch (getDragonVariant()) {
            case 1 -> "mudsmasher";
            case 2 -> "sandscorcher";
            case 3 -> "badlands";
            case 4 -> "kingly";
            case 5 -> "coastal_predator";
            default -> "wildroar";
        };
    }
}
