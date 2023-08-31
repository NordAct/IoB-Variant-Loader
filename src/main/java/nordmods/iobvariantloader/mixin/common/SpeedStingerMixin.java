package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import nordmods.iobvariantloader.util.DragonVariantUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(SpeedStinger.class)
public abstract class SpeedStingerMixin {
    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/speedstinger/SpeedStinger;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"), remap = false)
    private ADragonEggBase assignVariant(SpeedStinger instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {
            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    if (Objects.equals(parent1.getVariantName(), parent2.getVariantName())) helper.setVariantName(parent1.getVariantName());
                    else DragonVariantUtil.assignVariantFrom(world, helper, DragonVariantUtil.getVariants(parent1));
                    return egg;
                }
            }
        }
        return null;
    }
}
