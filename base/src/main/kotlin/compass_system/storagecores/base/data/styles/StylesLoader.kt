package compass_system.storagecores.base.data.styles

import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.FileByUnitLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller

class StylesLoader(knownBases: Set<ResourceLocation>) : FileByUnitLoader<StylesFile, StyleEntry>("${Constants.MOD_ID}/styles", "base", knownBases, StylesFile.codec) {
    lateinit var styles: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>>
        private set

    override fun apply(
        values: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        styles = values
    }

    override fun getFabricId() = Constants.resloc("styles_loader")
}