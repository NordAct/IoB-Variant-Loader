package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpeedStinger.class)
public abstract class SpeedStingerMixin {
    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/speedstinger/SpeedStinger;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"))
    private ADragonEggBase assignVariant(SpeedStinger instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {
            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    String parent1Variant = parent1.getVariantName();
                    String parent2Variant = parent2.getVariantName();

                    if (instance.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
                        if (instance.getRandom().nextBoolean()) helper.setVariantName(parent1Variant);
                        else helper.setVariantName(parent2Variant);
                    }
                    else DragonVariantUtil.assignVariant(world, egg, false, parent1);
                    return egg;
                }
            }
        }
        return null;
    }
}
