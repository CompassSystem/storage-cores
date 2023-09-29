package compass_system.storagecores.base.data

import net.minecraft.resources.ResourceLocation

interface ReplacingMap<T> {
    fun replaceExistingEntries(): Boolean
    fun entries(): Map<ResourceLocation, T>
}