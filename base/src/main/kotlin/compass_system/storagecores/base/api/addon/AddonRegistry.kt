package compass_system.storagecores.base.api.addon

import net.minecraft.resources.ResourceLocation

interface AddonRegistry {
    @Throws(DuplicateEntryException::class)
    fun registerBase(id: ResourceLocation)

    fun requireCore(id: ResourceLocation)
}