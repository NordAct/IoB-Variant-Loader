package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariant;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DragonEggItem.class)
public abstract class DragonEggItemMixin extends Item {
    @Unique
    protected String variant = "";

    public DragonEggItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @ModifyArg(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity setVariant(Entity entity) {
        if (entity instanceof VariantNameHelper helper && entity.level instanceof ServerLevelAccessor serverLevelAccessor) {
            if (!variant.isEmpty()) helper.setVariantName(variant);
            else {
                List<DragonVariant> variants = DragonVariantUtil.getVariantsFor(getSpecies());
                DragonVariantUtil.assignVariantFromList(serverLevelAccessor, entity, false, variants);
            }
        }
        return entity;
    }

    @Inject(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"))
    private void getVariant(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = pContext.getItemInHand();
        if (itemStack.hasTag() && itemStack.getTag().contains("VariantName")) variant = itemStack.getTag().getString("VariantName");
        else variant = "";
    }

    private String getSpecies() {
        ResourceLocation resourcelocation = getRegistryName();
        String dragonID = resourcelocation.getPath().replace("_egg", "");
        //this inconsistency in names just kills me
        return switch (dragonID) {
            default -> dragonID;
            case "m_nightmare" -> "monstrous_nightmare";
            case "nadder" -> "deadly_nadder";
        };
    }
}
