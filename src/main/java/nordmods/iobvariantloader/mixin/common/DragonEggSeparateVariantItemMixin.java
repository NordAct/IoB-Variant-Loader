package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.items.DragonEggSeparateVariantItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DragonEggSeparateVariantItem.class)
public abstract class DragonEggSeparateVariantItemMixin extends DragonEggItemMixin {
    public DragonEggSeparateVariantItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        return super.useOn(pContext);
    }
}
