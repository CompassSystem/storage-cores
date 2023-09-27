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

public class BasePropertyLoader<PropFile extends ReplacingMap<Props>, Props> {
    private final String directory;
    private final Codec<PropFile> propertiesFileCodec;
    private final Set<ResourceLocation> knownBases;

    public BasePropertyLoader(String directory, Codec<PropFile> propertiesFileCodec, Set<ResourceLocation> knownBases) {
        this.directory = directory;
        this.propertiesFileCodec = propertiesFileCodec;
        this.knownBases = knownBases;
    }

    // todo: honestly this is hard to read, even for me who coded it, should try and make it easier to understand.
    public Map<ResourceLocation, Map<ResourceLocation, Props>> load(ResourceManager resourceManager) {
        Map<ResourceLocation, List<PropFile>> propertiesFilePerBase = new HashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(directory);

        for (Map.Entry<ResourceLocation, List<Resource>> entry : fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation baseId = fileToIdConverter.fileToId(entry.getKey());

            // if base is not loaded, print a warning, todo: hide behind config
            if (!knownBases.contains(baseId)) {
                for (Resource resource : entry.getValue()) {
                    Constants.LOGGER.info("Ignoring " + entry.getKey() + " from pack " + resource.sourcePackId() + " as base " + baseId + " is not loaded.");
                }

                continue;
            }

            List<PropFile> propertiesFiles = propertiesFilePerBase.computeIfAbsent(baseId, (id) -> new ArrayList<>());

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
                    Constants.LOGGER.error("Couldn't read properties {} from {} in data pack {}", baseId, entry.getKey(), resource.sourcePackId(), e);
                }
            }
        }

        Map<ResourceLocation, Map<ResourceLocation, Props>> propertiesPerBase = new HashMap<>();

        // Go through each properties file and add all properties to a map depending on what base it's a property for.
        for (Map.Entry<ResourceLocation, List<PropFile>> propertiesFiles : propertiesFilePerBase.entrySet()) {
            for (PropFile propertiesFile : propertiesFiles.getValue()) {
                for (Map.Entry<ResourceLocation, Props> keyedProperties : propertiesFile.values().entrySet()) {
                    propertiesPerBase.computeIfAbsent(propertiesFiles.getKey(), (id) -> new HashMap<>()).put(keyedProperties.getKey(), keyedProperties.getValue());
                }
            }
        }

        return propertiesPerBase;
    }
}
