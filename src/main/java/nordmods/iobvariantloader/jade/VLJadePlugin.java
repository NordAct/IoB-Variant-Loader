package nordmods.iobvariantloader.jade;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import nordmods.iobvariantloader.IoBVariantLoader;

@WailaPlugin(value = "iobvariantloader")
public class VLJadePlugin implements IWailaPlugin {
    public VLJadePlugin() {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        IoBVariantLoader.LOGGER.info("Loading Jade plugin");
        registration.registerComponentProvider(VLVariantNameProvider.INSTANCE, TooltipPosition.BODY, ADragonBase.class);
        registration.registerComponentProvider(VLVariantNameProvider.INSTANCE, TooltipPosition.BODY, ADragonEggBase.class);
    }
}
