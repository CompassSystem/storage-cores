package compass_system.storagecores.base.data.tiers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class BlockProperties(val hardness: Int, val resistance: Int) {
    companion object {
        val codec: Codec<BlockProperties> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("hardness").forGetter(BlockProperties::hardness),
                Codec.INT.fieldOf("resistance").forGetter(BlockProperties::resistance)
            ).apply(instance, ::BlockProperties)
        }
    }
}
