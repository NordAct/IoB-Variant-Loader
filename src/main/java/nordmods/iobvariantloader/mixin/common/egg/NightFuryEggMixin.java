package nordmods.iobvariantloader.mixin.common.egg;

import com.GACMD.isleofberk.entity.eggs.entity.eggs.NightFuryEgg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NightFuryEgg.class)
public abstract class NightFuryEggMixin extends ADragonEggBaseMixin {
    protected NightFuryEggMixin(EntityType<? extends AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public ItemStack getPickResult() {
        return super.getPickResult();
    }

    @Override
    protected Component getDefaultTypeName() {
        return new TranslatableComponent("item.isleofberk.night_fury_egg");
    }
}
