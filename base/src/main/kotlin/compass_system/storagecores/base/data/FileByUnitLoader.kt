package compass_system.storagecores.base.data

import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import compass_system.storagecores.base.Constants
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller

abstract class FileByUnitLoader<File : ReplacingMap<Entry>, Entry>(
    private val directory: String,
    private val unitName: String,
    private val knownUnits: Set<ResourceLocation>,
    private val fileCodec: Codec<File>
) : SimplePreparableReloadListener<Map<ResourceLocation, Map<ResourceLocation, Entry>>>(), IdentifiableResourceReloadListener {

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller): Map<ResourceLocation, Map<ResourceLocation, Entry>> {
        val filesByUnit = mutableMapOf<ResourceLocation, MutableList<File>>()
        val fileToIdConverter = FileToIdConverter.json(directory)

        for ((resourceKey, resources) in fileToIdConverter.listMatchingResourceStacks(resourceManager)) {
            val unitId = fileToIdConverter.fileToId(resourceKey)

            if (unitId !in knownUnits) {
                for (resource in resources) {
                    Constants.LOGGER.info("Ignoring $resourceKey from pack ${resource.sourcePackId()} as $unitName $unitId is not loaded.")
                }

                continue
            }

            val files = filesByUnit.computeIfAbsent(unitId) { mutableListOf() }

            for (resource in resources) {
                try {
                    val reader = resource.openAsReader()
                    val element = JsonParser.parseReader(reader)
                    val file = fileCodec.parse(JsonOps.INSTANCE, element).getOrThrow(false, Constants.LOGGER::error)

                    if (file.replaceExistingEntries()) {
                        files.clear()
                    }

                    files.add(file)
                } catch (e: Exception) {
                    Constants.LOGGER.error("Couldn't read entries {} from {} in data pack {}", unitId, resourceKey, resource.sourcePackId(), e)
                }
            }
        }

        val entriesByUnit = mutableMapOf<ResourceLocation, MutableMap<ResourceLocation, Entry>>()

        filesByUnit.forEach { (unit, files) ->
            files.forEach { file ->
                file.entries().forEach { (id, value) ->
                    entriesByUnit.computeIfAbsent(unit) { mutableMapOf() }[id] = value
                }
            }
        }

        return entriesByUnit.mapValues { it.value.toMap() }
    }
}