package compass_system.storagecores.base.data.tiers

import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.FileByUnitLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller

class TiersLoader(knownCores: Set<ResourceLocation>) : FileByUnitLoader<TiersFile, TierEntry>("${Constants.MOD_ID}/tiers", "storage core", knownCores, TiersFile.codec) {
    lateinit var tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>>
        private set

    override fun apply(
        values: Map<ResourceLocation, Map<ResourceLocation, TierEntry>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        tiers = values
    }

    override fun getFabricId() = Constants.resloc("tiers_loader")
}