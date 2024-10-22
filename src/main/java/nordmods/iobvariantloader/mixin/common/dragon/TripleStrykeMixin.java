package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TripleStryke.class)
public abstract class TripleStrykeMixin extends ADragonBaseMixin {

    protected TripleStrykeMixin(EntityType<? extends ADragonBase> animal, Level world) {
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
    protected String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "sandr";
            case 2 -> "fart_n_sniff";
            case 3 -> "hodd";
            case 4 -> "hjarta";
            case 5 -> "exiled";
            case 6 -> "whip_n_lash";
            case 7 -> "leaf_n_bark";
            case 8 -> "purple_n_nurple";
            case 9 -> "hamfeist";
            case 10 -> "kandy_n_kane";
            default -> "pistill";
        };
    }
}
