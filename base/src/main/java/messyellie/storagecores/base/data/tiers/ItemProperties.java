package messyellie.storagecores.base.data.tiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

// todo: determine final set of properties
public record ItemProperties(boolean fireResistant) {
    public static final Codec<ItemProperties> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf("fire_resistant", Boolean.FALSE).forGetter(ItemProperties::fireResistant)
                    )
                    .apply(instance, ItemProperties::new)
    );
}
