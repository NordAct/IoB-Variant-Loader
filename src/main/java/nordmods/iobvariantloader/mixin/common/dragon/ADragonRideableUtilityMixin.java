package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.dragon.ADragonRideableUtility;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nordmods.iobvariantloader.util.extras.Extras;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.hitbox_redirect.HitboxRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ADragonRideableUtility.class)
public abstract class ADragonRideableUtilityMixin extends ADragonBaseMixin {
    @Shadow protected abstract void setAnimalRotations(Entity pPassenger);
    @Unique private List<Vec3> passengerPositions;

    protected ADragonRideableUtilityMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    @Inject(method = "positionRider", at = @At("HEAD"), cancellable = true)
    private void overridePosition(Entity passenger, CallbackInfo ci) {
        if (passengerPositions == null) passengerPositions = HitboxRedirectUtil.getPassengerPositions((ADragonRideableUtility)(Object)this);
        if (passengerPositions.isEmpty()) return;
        Vec3 offset = null;
        for (int i = 0; i < getPassengers().size(); i++) {
            if (i >= passengerPositions.size()) return;
            if (getPassengers().get(i) == passenger) {
                offset = passengerPositions.get(i);
                break;
            }
        }
        if (offset == null) return;

        offset = offset.yRot(-getYRot() * Mth.DEG_TO_RAD);
        passenger.setPos(getX() + offset.x, getY() + offset.y, getZ() + offset.z);
        if (getControllingPassenger() != passenger) setAnimalRotations(passenger);
        ci.cancel();
    }

    @Override
    public void resetHitboxData() {
        passengerPositions = null;
        super.resetHitboxData();
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void checkCustomItemInteraction(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        checkCustomItemInteractions(pPlayer, pHand, cir);
    }

    @Unique
    protected void checkCustomItemInteractions(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean success = false;
        for (Extras.CustomItemInteraction interaction : ExtrasUtil.getCustomItemInteractions(getSpecies(false), getVariantName(), itemstack)) {
            if (itemstack.getCount() < interaction.requiredAmount()) {
                if (itemstack.isEmpty()) break;
                continue;
            }
            if (!isTame() && !interaction.canInteractWithUntamed()) continue;
            if (isTame() && !isOwnedBy(pPlayer) && !interaction.canInteractIfNotOwner()) continue;
            interaction.commands().forEach(command -> command.execute(pPlayer, (ADragonBase) (Object) this));
            if (interaction.consumeOnUse()) itemstack.shrink(interaction.requiredAmount());
            success = true;
        }
        if (success) cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
