package compass_system.storagecores.base.data.styles

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import compass_system.storagecores.base.Constants
import net.minecraft.resources.ResourceLocation

data class StyleEntry(val texture: ResourceLocation, val advancement: ResourceLocation) {
    companion object {
        val codec: Codec<StyleEntry> = RecordCodecBuilder.create { instance ->
            instance.group(
                Constants.normalisedResourceLocationCodec().fieldOf("texture").forGetter(StyleEntry::texture),
                Constants.normalisedResourceLocationCodec().fieldOf("advancement").forGetter(StyleEntry::advancement)
            )
                .apply(instance, ::StyleEntry)
        }
    }
}
