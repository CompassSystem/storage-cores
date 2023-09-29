package compass_system.storagecores.base.data.tiers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.ReplacingMap
import net.minecraft.resources.ResourceLocation

data class TiersFile(val values: Map<ResourceLocation, TierEntry>, val replace: Boolean) : ReplacingMap<TierEntry> {
    override fun replaceExistingEntries() = replace

    override fun entries() = values

    companion object {
        val codec : Codec<TiersFile> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(Constants.normalisedResourceLocationCodec(), TierEntry.codec).fieldOf("values").forGetter(TiersFile::values),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(TiersFile::replace)
            ).apply(instance, ::TiersFile)
        }
    }
}
