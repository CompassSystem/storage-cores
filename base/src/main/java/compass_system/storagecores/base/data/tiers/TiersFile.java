package compass_system.storagecores.base.data.tiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import compass_system.storagecores.base.Constants;
import compass_system.storagecores.base.data.ReplacingMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record TiersFile(Map<ResourceLocation, TierEntry> entries, boolean replace) implements ReplacingMap<TierEntry> {
    public static final Codec<TiersFile> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.unboundedMap(Constants.normalisedResourceLocationCodec(), TierEntry.CODEC).fieldOf("values").forGetter(TiersFile::entries),
                            Codec.BOOL.optionalFieldOf("replace", Boolean.FALSE).forGetter(TiersFile::replace)
                    )
                    .apply(instance, TiersFile::new)
    );

    @Override
    public boolean shouldReplace() {
        return replace;
    }

    @Override
    public Map<ResourceLocation, TierEntry> values() {
        return entries;
    }
}
