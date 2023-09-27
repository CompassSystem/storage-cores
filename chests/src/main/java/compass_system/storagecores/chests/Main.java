package compass_system.storagecores.chests;

import compass_system.storagecores.base.Constants;
import compass_system.storagecores.base.addon.StorageCoreAddon;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

public class Main implements StorageCoreAddon {
    @Override
    public Collection<ResourceLocation> getBases() {
        return List.of(Constants.resloc("chest"));
    }
}
