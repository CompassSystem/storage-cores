package compass_system.storagecores.base.data.tiers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class TierEntry(val capacity: Int, val blockProperties: BlockProperties, val itemProperties: ItemProperties) {
    companion object {
        val codec: Codec<TierEntry> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("capacity").forGetter(TierEntry::capacity),
                BlockProperties.codec.fieldOf("block_properties").forGetter(TierEntry::blockProperties),
                ItemProperties.codec.fieldOf("item_properties").forGetter(TierEntry::itemProperties)
            ).apply(instance, ::TierEntry)
        }
    }
}
