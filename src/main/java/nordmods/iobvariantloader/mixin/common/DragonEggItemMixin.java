package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonEggItem.class)
public abstract class DragonEggItemMixin {
    @Unique
    protected String variant = "";

    @ModifyArg(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity setVariant(Entity entity) {
        if (entity instanceof VariantNameHelper helper && !variant.isEmpty()) helper.setVariantName(variant);
        return entity;
    }

    @Inject(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"))
    private void getVariant(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = pContext.getItemInHand();
        if (itemStack.hasTag() && itemStack.getTag().contains("VariantName")) variant = itemStack.getTag().getString("VariantName");
    }
}
