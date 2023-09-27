package compass_system.storagecores.base.data.tiers;

import compass_system.storagecores.base.Constants;
import compass_system.storagecores.base.data.UnitPropertyLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TiersLoader implements IdentifiableResourceReloadListener {
    private final Set<ResourceLocation> knownCores;
    private Map<ResourceLocation, Map<ResourceLocation, TierEntry>> tiers;

    public TiersLoader(Set<ResourceLocation> knownCores) {
        this.knownCores = knownCores;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> new UnitPropertyLoader<>(Constants.MOD_ID + "/tiers", TiersFile.CODEC, "storage core", knownCores).load(resourceManager), backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(values -> tiers = values, gameExecutor);
    }

    @Override
    public ResourceLocation getFabricId() {
        return Constants.resloc("tiers_loader");
    }

    public Map<ResourceLocation, Map<ResourceLocation, TierEntry>> getTiers() {
        return tiers;
    }
}
