package compass_system.storagecores.base.data.tiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

// todo: determine final set of properties
public record BlockProperties(int hardness, int resistance) {
    public static final Codec<BlockProperties> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.fieldOf("hardness").forGetter(BlockProperties::hardness),
                            Codec.INT.fieldOf("resistance").forGetter(BlockProperties::resistance)
                    )
                    .apply(instance, BlockProperties::new)
    );
}
