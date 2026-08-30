package nordmods.iobvariantloader.util.alt_navigation;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class AltFollowGoal extends FollowOwnerGoal {
    private ADragonBase tamable;
    private int timeToRecalcPath;
    private final PathNavigation navigation;
    private double speedModifier;

    public AltFollowGoal(ADragonBase pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        super(pTamable, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
        this.tamable = pTamable;
        this.speedModifier = pSpeedModifier;
        this.navigation = pTamable.getNavigation();

    }

    @Override
    public boolean canUse() {
        return tamable.isDragonFollowing() && super.canUse();
    }

    @Override
    public void tick() {
        var owner = this.tamable.getOwner();
        if (owner == null) {
            this.navigation.stop();
            return;
        }

        this.tamable.getLookControl().setLookAt(owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                this.navigation.moveTo(owner, this.speedModifier);
            }
        }
    }
}
