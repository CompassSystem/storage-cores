package messyellie.storagecores.base.data.styles;

import messyellie.storagecores.base.Constants;
import messyellie.storagecores.base.data.BasePropertyLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StylesLoader implements IdentifiableResourceReloadListener {
    private Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> styles = Map.of();

    @NotNull
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> new BasePropertyLoader<>(Constants.MOD_ID + "/styles", StylesFile.CODEC).load(resourceManager), backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(values -> styles = values, gameExecutor);
    }

    @Override
    public ResourceLocation getFabricId() {
        return Constants.resloc("styles_loader");
    }

    public Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> getStyles() {
        return styles;
    }
}