package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.HitboxRedirectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TripleStryke.class)
public abstract class TripleStrykeMixin extends ADragonBase {

    protected TripleStrykeMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 0, argsOnly = true, remap = false)
    private double adjustBox1(double value) {
        return helper().getAttackBoxPos().x * Mth.sin(-getYRot() * Mth.DEG_TO_RAD);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 1, argsOnly = true, remap = false)
    private double adjustBox2(double value) {
        return helper().getAttackBoxPos().y;
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 2, argsOnly = true, remap = false)
    private double adjustBox3(double value) {
        return helper().getAttackBoxPos().z * Mth.cos(getYRot() * Mth.DEG_TO_RAD);
    }

    private HitboxRedirectHelper helper() {
        return (HitboxRedirectHelper) (Object)this;
    }
}
