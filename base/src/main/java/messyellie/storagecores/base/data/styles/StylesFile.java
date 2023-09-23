package messyellie.storagecores.base.data.styles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import messyellie.storagecores.base.data.ReplacingMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record StylesFile(Map<ResourceLocation, StyleEntry> entries, boolean replace) implements ReplacingMap<StyleEntry> {
    public static final Codec<StylesFile> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.unboundedMap(ResourceLocation.CODEC, StyleEntry.CODEC).fieldOf("values").forGetter(StylesFile::entries),
                            Codec.BOOL.optionalFieldOf("replace", Boolean.FALSE).forGetter(StylesFile::replace)
                    )
                    .apply(instance, StylesFile::new)
    );

    @Override
    public boolean shouldReplace() {
        return replace;
    }

    @Override
    public Map<ResourceLocation, StyleEntry> values() {
        return entries;
    }
}
