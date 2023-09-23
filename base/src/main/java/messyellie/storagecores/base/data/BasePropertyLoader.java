package messyellie.storagecores.base.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import messyellie.storagecores.base.Constants;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.Reader;
import java.util.*;

public class BasePropertyLoader<T extends ReplacingMap<R>, R> {
    private final String directory;
    private final Codec<T> propertiesFileCodec;

    public BasePropertyLoader(String directory, Codec<T> propertiesFileCodec) {
        this.directory = directory;
        this.propertiesFileCodec = propertiesFileCodec;
    }

    // todo: honestly this is hard to read, even for me who coded it, should try and make it easier to understand.
    public Map<ResourceLocation, Map<ResourceLocation, R>> load(ResourceManager resourceManager) {
        Map<ResourceLocation, List<T>> propertiesFilePerBase = new HashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(directory);

        for (Map.Entry<ResourceLocation, List<Resource>> entry : fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation baseId = fileToIdConverter.fileToId(entry.getKey());
            List<T> propertiesFiles = propertiesFilePerBase.computeIfAbsent(baseId, (id) -> new ArrayList<>());

            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement element = JsonParser.parseReader(reader);
                    T propertiesFile = propertiesFileCodec.parse(JsonOps.INSTANCE, element).getOrThrow(false, Constants.LOGGER::error);

                    if (propertiesFile.shouldReplace()) {
                        propertiesFiles.clear();
                    }

                    propertiesFiles.add(propertiesFile);
                } catch (Exception e) {
                    Constants.LOGGER.error("Couldn't read properties {} from {} in data pack {}", baseId, entry.getKey(), resource.sourcePackId(), e);
                }
            }
        }

        Map<ResourceLocation, Map<ResourceLocation, R>> propertiesPerBase = new HashMap<>();

        for (Map.Entry<ResourceLocation, List<T>> propertiesFiles : propertiesFilePerBase.entrySet()) {
            for (T propertiesFile : propertiesFiles.getValue()) {
                for (Map.Entry<ResourceLocation, R> keyedProperties : propertiesFile.values().entrySet()) {
                    propertiesPerBase.computeIfAbsent(propertiesFiles.getKey(), (id) -> new HashMap<>()).put(keyedProperties.getKey(), keyedProperties.getValue());
                }
            }
        }

        return propertiesPerBase;
    }
}
