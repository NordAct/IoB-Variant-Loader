package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariant;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
            else if (IoBVariantLoader.config.assignEggVariantOnPlaced.get()) {
                List<DragonVariant> variants = DragonVariantUtil.getVariantsFor(getSpecies());
                DragonVariantUtil.assignVariantFromList(serverLevelAccessor, entity, false, variants);
            }
        }
        return entity;
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"))
    private void getVariant(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = pContext.getItemInHand();
        if (itemStack.hasTag()) variant = itemStack.getTag().getString("VariantName");
        else variant = "";
    }

    @Unique
    @SuppressWarnings("DataFlowIssue")
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

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", at = @At("TAIL"))
    private void addVariantTooltip(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, CallbackInfo ci) {
        String variant = pStack.hasTag() ? pStack.getTag().getString("VariantName") : "";
        if (!variant.isEmpty()) {
            String key = "tooltip.iobvariantloader.variant." + variant;
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
}
