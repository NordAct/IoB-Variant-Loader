package nordmods.iobvariantloader.util.layer;

import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.DeadlyNadderModelCacheHelper;
import nordmods.iobvariantloader.util.layer.VLGlowLayer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DeadlyNadderWingGlowLayer extends VLGlowLayer<DeadlyNadder> {
    public DeadlyNadderWingGlowLayer(IGeoRenderer<DeadlyNadder> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    protected ResourceLocation getGlowLayerLocation(DeadlyNadder dragon) {
        if (((DeadlyNadderModelCacheHelper)dragon).getWingGlowLayerLocationCache() != null) return ((DeadlyNadderModelCacheHelper)dragon).getWingGlowLayerLocationCache();

        BaseRenderer<DeadlyNadder> baseRenderer = (BaseRenderer<DeadlyNadder>) getRenderer();
        String namespace = baseRenderer.getTextureLocation(dragon).getNamespace();
        String path = baseRenderer.getTextureLocation(dragon).getPath().replace(".png", "_membranes_glowing.png");
        ResourceLocation id = new ResourceLocation(namespace, path);
        ((DeadlyNadderModelCacheHelper)dragon).setWingGlowLayerLocationCache(id);

        return id;
    }

    @Override
    protected void resetCache(DeadlyNadder dragon) {
        super.resetCache(dragon);
        ((DeadlyNadderModelCacheHelper)dragon).setWingGlowLayerLocationCache(null);
    }
}
