package compass_system.storagecores.base.data

import com.mojang.serialization.JsonOps
import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.styles.StyleEntry
import compass_system.storagecores.base.data.tiers.TierEntry
import net.minecraft.resources.ResourceLocation

object SyncedDataHolder {
    private var styles: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> = emptyMap()
    private var tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>> = emptyMap()

    fun receiveReloadableData(
        styles: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>>,
        tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>>
    ) {
        this.styles = styles
        this.tiers = tiers

        if (Constants.IS_DEBUG) {
            Constants.LOGGER.info(buildString {
                append("Received the following styles:\n")
                styles.forEach { (baseId, entries) ->
                    append("  Styles for \"$baseId\":\n")
                    entries.forEach { (styleId, style) ->
                        append("    $styleId = ${StyleEntry.codec.encodeStart(JsonOps.INSTANCE, style).getOrThrow(false, Constants.LOGGER::warn)}\n")
                    }
                }
            })

            Constants.LOGGER.info(buildString {
                append("Received the following tiers:\n")
                tiers.forEach { (coreId, entries) ->
                    append("  Tiers for \"$coreId\":\n")
                    entries.forEach { (tierId, tier) ->
                        append("    $tierId = ${TierEntry.codec.encodeStart(JsonOps.INSTANCE, tier).getOrThrow(false, Constants.LOGGER::warn)}\n")
                    }
                }
            })
        }
    }
}