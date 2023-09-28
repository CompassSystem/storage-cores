package compass_system.storagecores.base.data

import com.mojang.serialization.JsonOps
import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.styles.StyleEntry
import compass_system.storagecores.base.data.tiers.TierEntry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object ReloadableDataHolder {
    private var styles: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> = mapOf()
    private var tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>> = mapOf()

    fun sendValuesToPlayer(player: ServerPlayer, playerJoining: Boolean) {
        if (Constants.IS_DEBUG) {
            Constants.LOGGER.info("Sending reloadable data to: ${player.name.string}, is join: $playerJoining")
        }
    }

    fun setStyles(values: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>>) {
        if (Constants.IS_DEBUG) {
            Constants.LOGGER.info(buildString {
                append("Loading Styles:\n")
                values.forEach { (baseId, entries) ->
                    append("  Styles for \"$baseId\":\n")
                    entries.forEach { (styleId, style) ->
                        append("    $styleId = ${StyleEntry.codec.encodeStart(JsonOps.INSTANCE, style).getOrThrow(false, Constants.LOGGER::warn)}\n")
                    }
                }
            })
        }

        styles = values
    }

    fun setTiers(values: Map<ResourceLocation, Map<ResourceLocation, TierEntry>>) {
        Constants.LOGGER.info(buildString {
            append("Loading Tiers:\n")
            values.forEach { (coreId, entries) ->
                append("  Tiers for \"$coreId\":\n")
                entries.forEach { (tierId, tier) ->
                    append("    $tierId = ${TierEntry.codec.encodeStart(JsonOps.INSTANCE, tier).getOrThrow(false, Constants.LOGGER::warn)}\n")
                }
            }
        })

        tiers = values
    }

    fun getStyles(id: ResourceLocation): Map<ResourceLocation, StyleEntry>? {
        return styles[id]
    }
}