package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.breeding_list.BreedingListUtil;

import javax.annotation.Nullable;
import java.util.List;

public class VLDragonBreedGoal extends BreedGoal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    public VLDragonBreedGoal(ADragonBase pAnimal, double pSpeedModifier) {
        super(pAnimal, pSpeedModifier, ADragonBase.class);
    }

    public boolean canUse() {
        if (((ADragonBase)animal).shouldStopMovingIndependently()) return false;
        if (!((ADragonBase)animal).isTame()) return false;
        if (!animal.isInLove()) return false;
        this.partner = this.getFreePartner();
        return this.partner != null;
    }

    @Nullable
    private Animal getFreePartner() {
        List<ADragonBase> list = level.getNearbyEntities(ADragonBase.class, PARTNER_TARGETING, animal, animal.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        ADragonBase partner = null;

        for(ADragonBase animal1 : list) {
            if (canMate(animal1)
                    && !BreedingListUtil.getAvailableLists(((ADragonBase)animal), animal1).isEmpty()
                    && animal.distanceToSqr(animal1) < d0) {
                partner = animal1;
                d0 = partner.distanceToSqr(animal1);
            }
        }

        if (partner == null && IoBVariantLoader.config.breedingListsUse.get().canUseFallback()) {
            d0 = Double.MAX_VALUE;
            for(ADragonBase animal1 : list) {
                if (animal.canMate(animal1) && animal.distanceToSqr(animal1) < d0) {
                    partner = animal1;
                    d0 = partner.distanceToSqr(animal1);
                }
            }
            return partner;
        }
        
        return partner;
    }

    private boolean canMate(Animal partner) {
        if (partner == animal) return false;
        if (!(partner instanceof ADragonBase)) return false;
        return animal.isInLove() && partner.isInLove();
    }
}
