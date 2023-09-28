package compass_system.storagecores.base.data.tiers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class ItemProperties(val fireResistant: Boolean) {
    companion object {
        val codec: Codec<ItemProperties> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("fire_resistant", false).forGetter(ItemProperties::fireResistant)
            ).apply(instance, ::ItemProperties)
        }
    }
}
