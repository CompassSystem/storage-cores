package compass_system.storagecores.base.data.tiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TierEntry(int capacity, BlockProperties blockProperties, ItemProperties itemProperties) {
    public static final Codec<TierEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.fieldOf("capacity").forGetter(TierEntry::capacity),
                            BlockProperties.CODEC.fieldOf("block_properties").forGetter(TierEntry::blockProperties),
                            ItemProperties.CODEC.fieldOf("item_properties").forGetter(TierEntry::itemProperties)
                    )
                    .apply(instance, TierEntry::new)
    );
}
