package nordmods.iobvariantloader.mixin.common.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawner;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin(DragonEggItem.class)
public abstract class DragonEggItemMixin extends Item implements DragonSpeciesHelper {
    @Shadow private Supplier<? extends EntityType<? extends LivingEntity>> eggSpecies;

    public DragonEggItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @SuppressWarnings("DataFlowIssue")
    public String getSpecies(boolean isClient) {
        ResourceLocation resourcelocation = getRegistryName();
        String dragonID = resourcelocation.getPath().replace("_egg", "");
        //this inconsistency in names just kills me
        return switch (dragonID) {
            default -> dragonID;
            case "monstrous_nightmare" -> isClient ? "nightmare" : dragonID;
            case "nadder" -> "deadly_nadder";
        };
    }


    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", at = @At("TAIL"))
    private void addVariantTooltip(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, CallbackInfo ci) {
        String variant = pStack.hasTag() ? pStack.getTag().getString("VariantName") : "";
        if (!variant.isEmpty()) {
            String key = "tooltip.iobvariantloader." + getSpecies(true) + "." + variant;
            if (Language.getInstance().has(key)) {
                pTooltipComponents.add(new TranslatableComponent("tooltip.iobvariantloader.variant", new TranslatableComponent(key).withStyle(ChatFormatting.GOLD)));
            } else {
                variant = parseName(variant);
                pTooltipComponents.add(new TranslatableComponent("tooltip.iobvariantloader.variant", new TextComponent(variant).withStyle(ChatFormatting.GOLD)));
            }
        } else pTooltipComponents.add(new TranslatableComponent("tooltip.iobvariantloader.variant",
                new TextComponent("unknown").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withObfuscated(true))));
    }

    @Unique
    private String parseName(String name) {
        while (name.contains("_")) {
            int index = name.indexOf("_");
            if (index + 1 < name.length()) {
                String toReplace = String.valueOf(name.charAt(index + 1));
                name = name.replaceFirst("_" + toReplace, " " + toReplace.toUpperCase());
                continue;
            }
            name = name.replace("_", " ");
        }
        name = name.replace(" N ", "'n'");
        String firstLetter = String.valueOf(name.charAt(0));
        return name.replaceFirst(firstLetter, firstLetter.toUpperCase());
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            String variant = itemStack.getTag().getString("VariantName");
            if (ModelRedirectUtil.dragonModelRedirects.containsKey(getSpecies(true))
                    && ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).containsKey(variant)
                    && ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).get(variant).eggItemName() != null)
                return ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).get(variant).eggItemName();
        }
        return super.getName(itemStack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        ItemStack playerHeldItem = pContext.getItemInHand();
        Level level = pContext.getLevel();
        ADragonEggBase eggEntity = (ADragonEggBase)((EntityType<?>)eggSpecies.get()).create(level);

        if (eggEntity != null) {
        eggEntity.moveTo(pContext.getClickLocation());
        if (!level.isClientSide()) {
            String variant = "";
            if (playerHeldItem.hasTag()) variant = playerHeldItem.getTag().getString("VariantName");
            if (eggEntity instanceof VariantNameHelper helper && level instanceof ServerLevelAccessor serverLevelAccessor) {
                if (!variant.isEmpty()) helper.setVariantName(variant);
                else if (IoBVariantLoader.config.assignEggVariantOnPlaced.get()) {
                    List<DragonVariantSpawner> variants = DragonVariantSpawnerUtil.getVariantsFor(getSpecies(false));
                    DragonVariantSpawnerUtil.assignVariantFromList(serverLevelAccessor, eggEntity, false, variants);
                }
            }
            if (playerHeldItem.hasCustomHoverName()) eggEntity.setCustomName(playerHeldItem.getDisplayName());
            level.addFreshEntity(eggEntity);
        }
        }

        playerHeldItem.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
