package compass_system.storagecores.base.data

import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import compass_system.storagecores.base.Constants
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager

class UnitPropertyLoader<PropFile : ReplacingMap<Props>, Props>(
    private val directory: String,
    private val propertiesFileCodec: Codec<PropFile>,
    private val unitName: String,
    private val knownUnits: Set<ResourceLocation>
) {
    fun load(resourceManager: ResourceManager): Map<ResourceLocation, Map<ResourceLocation, Props>> {
        val propertiesFilePerUnit = mutableMapOf<ResourceLocation, MutableList<PropFile>>()
        val fileToIdConverter = FileToIdConverter.json(directory)

        for ((resourceKey, resources) in fileToIdConverter.listMatchingResourceStacks(resourceManager)) {
            val unitId = fileToIdConverter.fileToId(resourceKey)

            if (unitId !in knownUnits) {
                resources.forEach { resource ->
                    Constants.LOGGER.info("Ignoring $resourceKey from pack ${resource.sourcePackId()} as $unitName $unitId is not loaded.")
                }

                continue
            }

            val propertiesFiles = propertiesFilePerUnit.computeIfAbsent(unitId) { mutableListOf() }

            for (resource in resources) {
                try {
                    val reader = resource.openAsReader()
                    val element = JsonParser.parseReader(reader)
                    val propertiesFile = propertiesFileCodec.parse(JsonOps.INSTANCE, element).getOrThrow(false, Constants.LOGGER::error)

                    if (propertiesFile.shouldReplace()) {
                        propertiesFiles.clear()
                    }

                    propertiesFiles.add(propertiesFile)
                } catch (e: Exception) {
                    Constants.LOGGER.error("Couldn't read properties {} from {} in data pack {}", unitId, resourceKey, resource.sourcePackId(), e)
                }
            }
        }

        val propertiesPerUnit = mutableMapOf<ResourceLocation, MutableMap<ResourceLocation, Props>>()

        propertiesFilePerUnit.forEach { (unit, propertiesFiles) ->
            propertiesFiles.forEach { propertiesFile ->
                propertiesFile.values().forEach { (id, value) ->
                    propertiesPerUnit.computeIfAbsent(unit) { mutableMapOf() }[id] = value
                }
            }
        }

        return propertiesPerUnit
    }
}