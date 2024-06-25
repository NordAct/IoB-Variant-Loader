package nordmods.iobvariantloader.mixin.client.minecraft;

import com.GACMD.isleofberk.items.DragonEggItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import nordmods.iobvariantloader.util.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;
    @Unique private BakedModel proposedModel;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void getEggModel(ItemStack pItemStack, ItemTransforms.TransformType pTransformType, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay, BakedModel pModel, CallbackInfo ci) {
        if (!(pItemStack.getItem() instanceof DragonEggItem dragonEggItem)) return;
        if (!pItemStack.hasTag() || !pItemStack.getTag().contains("VariantName")) return;

        String name = pItemStack.getTag().getString("VariantName");
        String dragon = ((DragonSpeciesHelper)dragonEggItem).getSpecies(true);
        String modelLocation = ModelRedirectUtil.getEggItemModel(dragon, name);
        if (modelLocation != null) {
            BakedModel bakedModel = itemModelShaper.getModelManager().getModel(new ModelResourceLocation(modelLocation, "inventory"));
            if (bakedModel != itemModelShaper.getModelManager().getMissingModel()) proposedModel = bakedModel;
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;handleCameraTransforms(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Z)Lnet/minecraft/client/resources/model/BakedModel;"), index = 1)
    private BakedModel tryProposedModel(BakedModel model) {
        if (proposedModel != null) {
            BakedModel copy = proposedModel;
            proposedModel = null;
            return copy;
        }
        return model;
    }
}
