package nordmods.iobvariantloader.mixin.common.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.SpeedStingerEgg;
import com.GACMD.isleofberk.items.DragonEggItem;
import com.GACMD.isleofberk.registery.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpeedStingerEgg.class)
public abstract class SpeedStingerEggMixin extends ADragonEggBaseMixin implements VariantNameHelper{
    @Shadow @Final public static ResourceLocation SPEED_STINGER;

    @Shadow @Final public static ResourceLocation FLOUTSCOUT;

    @Shadow @Final public static ResourceLocation ICE_BREAKER;

    @Shadow @Final public static ResourceLocation SWEET_STING;

    @Shadow @Final private int hatchTime;

    @Shadow @Final private int sweetStingHatchTime;

    @Shadow @Final private int iceBreakerHatchTime;

    @Shadow @Final private int floutscoutHatchTime;

    protected SpeedStingerEggMixin(EntityType<? extends AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Redirect(method = "hatch()V", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean assignVariant(Level world, Entity entity) {
        if (world instanceof ServerLevelAccessor serverLevelAccessor) {
            if (getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(serverLevelAccessor, (LivingEntity) entity, false);
            else ((VariantNameHelper)entity).setVariantName(getVariantName());
        }
        return world.addFreshEntity(entity);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected DragonEggItem getItemVersion() {
        return ModItems.SPEED_STINGER_EGG.get();
    }

    @Override
    public ItemStack getPickResult() {
        return super.getPickResult();
    }

    @Override
    public ResourceLocation getTextureLocation(ADragonEggBase dragonBase) {
        return switch (getVariantName()) {
            default -> SPEED_STINGER;
            case "floutscout" -> FLOUTSCOUT;
            case "ice_breaker" -> ICE_BREAKER;
            case "sweet_sting" -> SWEET_STING;
        };
    }

    @Override
    public Block getBlockParticle() {
        Block block;
        block = switch (getVariantName()) {
            default -> Blocks.BEE_NEST;
            case "floutscout" -> Blocks.BASALT;
            case "ice_breaker" -> Blocks.PACKED_ICE;
            case "sweet_sting" -> Blocks.PUMPKIN;
        };
        return block;
    }

    @Override
    protected int getHatchTime() {
        return switch (getVariantName()) {
            default -> hatchTime;
            case "floutscout" -> floutscoutHatchTime;
            case "ice_breaker" -> iceBreakerHatchTime;
            case "sweet_sting" -> sweetStingHatchTime;
        };
    }

    @Override
    protected Component getDefaultTypeName() {
        String key = switch (getVariantName()) {
            default -> "item.isleofberk.speed_stinger_egg";
            case "floutscout" -> "item.isleofberk.speed_stinger_egg_floutscout";
            case "ice_breaker" -> "item.isleofberk.speed_stinger_egg_ice_breaker";
            case "sweet_sting" -> "item.isleofberk.speed_stinger_egg_sweet_sting";
        };
        return new TranslatableComponent(key);
    }
}
