package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DeadlyNadderWingGlowLayer extends VLGlowLayer<DeadlyNadder>{
    public DeadlyNadderWingGlowLayer(IGeoRenderer<DeadlyNadder> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    protected ResourceLocation getGlowLayerLocation(DeadlyNadder dragon) {
        BaseRenderer<DeadlyNadder> baseRenderer = (BaseRenderer<DeadlyNadder>) getRenderer();
        String namespace = baseRenderer.getTextureLocation(dragon).getNamespace();
        String path = baseRenderer.getTextureLocation(dragon).getPath().replace(".png", "_membranes_glowing.png");
        return new ResourceLocation(namespace, path);
    }
}
