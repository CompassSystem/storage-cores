package compass_system.storagecores.base

import compass_system.storagecores.base.api.addon.AddonRegistry
import compass_system.storagecores.base.api.addon.DuplicateEntryException
import net.minecraft.resources.ResourceLocation
import java.util.*

object AddonRegistryImpl : AddonRegistry {
    private val knownBases = mutableSetOf<ResourceLocation>()
    private val requiredCores = mutableSetOf<ResourceLocation>()

    override fun registerBase(id: ResourceLocation) {
        if (!knownBases.add(id)) {
            throw DuplicateEntryException("Base \"$id\" has already been registered.")
        }
    }

    override fun requireCore(id: ResourceLocation) {
        requiredCores.add(id)
    }

    fun getKnownBases(): Set<ResourceLocation> = Collections.unmodifiableSet(knownBases)

    fun getRequiredCores(): Set<ResourceLocation> = Collections.unmodifiableSet(requiredCores)
}