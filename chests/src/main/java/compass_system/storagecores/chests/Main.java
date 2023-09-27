package compass_system.storagecores.chests;

import compass_system.storagecores.base.Constants;
import compass_system.storagecores.base.api.addon.AddonRegistry;
import compass_system.storagecores.base.api.addon.DuplicateEntryException;
import compass_system.storagecores.base.api.addon.StorageCoreAddon;

public class Main implements StorageCoreAddon {
    @Override
    public void initialize(AddonRegistry registry) {
        try {
            registry.registerBase(Constants.resloc("chest"));
        } catch (DuplicateEntryException e) {
            Constants.LOGGER.error("Failed to initialise chest module, another module is already declaring our content.");
            throw e;
        }

        registry.requireCore(Constants.VANILLA_TYPE);
    }
}
