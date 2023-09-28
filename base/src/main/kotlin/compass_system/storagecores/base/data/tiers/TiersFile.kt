package compass_system.storagecores.base.data.tiers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.ReplacingMap
import net.minecraft.resources.ResourceLocation

data class TiersFile(val entries: Map<ResourceLocation, TierEntry>, val replace: Boolean) : ReplacingMap<TierEntry> {
    override fun shouldReplace() = replace

    override fun values() = entries

    companion object {
        val codec : Codec<TiersFile> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(Constants.normalisedResourceLocationCodec(), TierEntry.codec).fieldOf("values").forGetter(TiersFile::entries),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(TiersFile::replace)
            ).apply(instance, ::TiersFile)
        }
    }
}
