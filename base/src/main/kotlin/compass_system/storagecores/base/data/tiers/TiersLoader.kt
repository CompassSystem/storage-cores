package compass_system.storagecores.base.data.tiers

import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.UnitPropertyLoader
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class TiersLoader(private val knownCores: Set<ResourceLocation>) : IdentifiableResourceReloadListener {
    lateinit var tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>>
        private set

    override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        preparationsProfiler: ProfilerFiller,
        reloadProfiler: ProfilerFiller,
        backgroundExecutor: Executor,
        gameExecutor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.supplyAsync( { UnitPropertyLoader("${Constants.MOD_ID}/tiers", TiersFile.codec, "storage core", knownCores).load(resourceManager) }, backgroundExecutor)
            .thenCompose(preparationBarrier::wait)
            .thenAcceptAsync({ values -> tiers = values }, gameExecutor)
    }

    override fun getFabricId() = Constants.resloc("tiers_loader")
}