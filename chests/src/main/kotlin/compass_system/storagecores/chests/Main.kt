package compass_system.storagecores.chests

import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.Constants.resloc
import compass_system.storagecores.base.api.addon.AddonRegistry
import compass_system.storagecores.base.api.addon.DuplicateEntryException
import compass_system.storagecores.base.api.addon.StorageCoreAddon

object Main : StorageCoreAddon {
    override fun initialize(registry: AddonRegistry) {
        try {
            registry.registerBase(resloc("chest"))
        } catch (e: DuplicateEntryException) {
            Constants.LOGGER.error("Failed to initialise chest module, another module is already declaring our content.")
            throw e
        }

        registry.requireCore(Constants.VANILLA_TYPE)
    }
}