package compass_system.storagecores.base.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import compass_system.storagecores.base.Constants;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.Reader;
import java.util.*;

public class UnitPropertyLoader<PropFile extends ReplacingMap<Props>, Props> {
    private final String directory;
    private final Codec<PropFile> propertiesFileCodec;
    private final String unitName;
    private final Set<ResourceLocation> knownUnits;

    public UnitPropertyLoader(String directory, Codec<PropFile> propertiesFileCodec, String unitName, Set<ResourceLocation> knownUnits) {
        this.directory = directory;
        this.propertiesFileCodec = propertiesFileCodec;
        this.unitName = unitName;
        this.knownUnits = knownUnits;
    }

    // todo: honestly this is hard to read, even for me who coded it, should try and make it easier to understand.
    public Map<ResourceLocation, Map<ResourceLocation, Props>> load(ResourceManager resourceManager) {
        Map<ResourceLocation, List<PropFile>> propertiesFilePerUnit = new HashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(directory);

        for (Map.Entry<ResourceLocation, List<Resource>> entry : fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation unitId = fileToIdConverter.fileToId(entry.getKey());

            // if base is not loaded, print a warning, todo: hide behind config
            if (!knownUnits.contains(unitId)) {
                for (Resource resource : entry.getValue()) {
                    Constants.LOGGER.info("Ignoring " + entry.getKey() + " from pack " + resource.sourcePackId() + " as " + unitName +" " + unitId + " is not loaded.");
                }

                continue;
            }

            List<PropFile> propertiesFiles = propertiesFilePerUnit.computeIfAbsent(unitId, (id) -> new ArrayList<>());

            // Load Properties from files replacing the list if required
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement element = JsonParser.parseReader(reader);
                    PropFile propertiesFile = propertiesFileCodec.parse(JsonOps.INSTANCE, element).getOrThrow(false, Constants.LOGGER::error);

                    if (propertiesFile.shouldReplace()) {
                        propertiesFiles.clear();
                    }

                    propertiesFiles.add(propertiesFile);
                } catch (Exception e) {
                    Constants.LOGGER.error("Couldn't read properties {} from {} in data pack {}", unitId, entry.getKey(), resource.sourcePackId(), e);
                }
            }
        }

        Map<ResourceLocation, Map<ResourceLocation, Props>> propertiesPerUnit = new HashMap<>();

        // Go through each properties file and add all properties to a map depending on what base it's a property for.
        for (Map.Entry<ResourceLocation, List<PropFile>> propertiesFiles : propertiesFilePerUnit.entrySet()) {
            for (PropFile propertiesFile : propertiesFiles.getValue()) {
                for (Map.Entry<ResourceLocation, Props> keyedProperties : propertiesFile.values().entrySet()) {
                    propertiesPerUnit.computeIfAbsent(propertiesFiles.getKey(), (id) -> new HashMap<>()).put(keyedProperties.getKey(), keyedProperties.getValue());
                }
            }
        }

        return propertiesPerUnit;
    }
}
