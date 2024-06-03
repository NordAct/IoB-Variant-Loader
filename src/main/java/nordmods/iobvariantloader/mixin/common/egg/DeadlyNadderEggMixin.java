package nordmods.iobvariantloader.mixin.common.egg;

import com.GACMD.isleofberk.entity.eggs.entity.eggs.DeadlyNadderEgg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeadlyNadderEgg.class)
public abstract class DeadlyNadderEggMixin extends ADragonEggBaseMixin{
    protected DeadlyNadderEggMixin(EntityType<? extends AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public ItemStack getPickResult() {
        return super.getPickResult();
    }

    @Override
    protected Component getDefaultTypeName() {
        return new TranslatableComponent("item.isleofberk.nadder_egg");
    }
}
