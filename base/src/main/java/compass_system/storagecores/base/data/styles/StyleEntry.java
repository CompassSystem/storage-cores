package compass_system.storagecores.base.data.styles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import compass_system.storagecores.base.Constants;
import net.minecraft.resources.ResourceLocation;

public record StyleEntry(ResourceLocation texture, ResourceLocation advancement) {
    public static final Codec<StyleEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Constants.normalisedResourceLocationCodec().fieldOf("texture").forGetter(StyleEntry::texture),
                            Constants.normalisedResourceLocationCodec().fieldOf("advancement").forGetter(StyleEntry::advancement)
                    )
                    .apply(instance, StyleEntry::new)
    );

}
