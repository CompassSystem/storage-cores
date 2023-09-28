package compass_system.storagecores.base.data

import net.minecraft.resources.ResourceLocation

interface ReplacingMap<T> {
    fun shouldReplace(): Boolean
    fun values(): Map<ResourceLocation, T>
}