package nordmods.iobvariantloader.jade;

import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;

public class VLVariantNameProvider implements IEntityComponentProvider{
    public static final VLVariantNameProvider INSTANCE = new VLVariantNameProvider();
    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (!(entityAccessor.getEntity() instanceof VariantNameHelper variantNameHelper && variantNameHelper instanceof DragonSpeciesHelper dragonSpeciesHelper)) return;
        iTooltip.add(ResourceUtil.getVariantNameTooltip(variantNameHelper.getVariantName(), dragonSpeciesHelper));
    }
}
