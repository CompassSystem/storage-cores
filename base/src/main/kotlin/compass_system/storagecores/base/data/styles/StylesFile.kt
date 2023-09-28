package compass_system.storagecores.base.data.styles

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import compass_system.storagecores.base.data.ReplacingMap
import net.minecraft.resources.ResourceLocation

data class StylesFile(val entries: Map<ResourceLocation, StyleEntry>, val replace: Boolean) : ReplacingMap<StyleEntry> {
    override fun shouldReplace() = replace

    override fun values() = entries

    companion object {
        val codec: Codec<StylesFile> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(ResourceLocation.CODEC, StyleEntry.codec).fieldOf("values").forGetter(StylesFile::entries),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(StylesFile::replace)
            ).apply(instance, ::StylesFile)
        }
    }
}
