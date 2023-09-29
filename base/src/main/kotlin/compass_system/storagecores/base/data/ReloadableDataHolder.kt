package compass_system.storagecores.base.data

import com.mojang.serialization.JsonOps
import compass_system.storagecores.base.Constants
import compass_system.storagecores.base.data.styles.StyleEntry
import compass_system.storagecores.base.data.tiers.TierEntry
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object ReloadableDataHolder {
    private var styles: Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> = emptyMap()
    private var tiers: Map<ResourceLocation, Map<ResourceLocation, TierEntry>> = emptyMap()

    fun sendValuesToPlayer(player: ServerPlayer, playerJoining: Boolean) {
        if (Constants.IS_DEBUG) {
            Constants.LOGGER.info("Sending reloadable data to: ${player.name.string}, is join: $playerJoining")
        }

        if (ServerPlayNetworking.canSend(player, Constants.SYNC_ID)) {
            val buffer = FriendlyByteBuf(Unpooled.buffer())

            buffer.writeInt(styles.size)
            for ((baseId, styleEntries) in styles) {
                buffer.writeResourceLocation(baseId)
                buffer.writeInt(styleEntries.size)

                for ((id, style) in styleEntries) {
                    buffer.writeResourceLocation(id)
                    buffer.writeJsonWithCodec(StyleEntry.codec, style)
                }
            }

            buffer.writeInt(tiers.size)
            for ((coreId, tierEntries) in tiers) {
                buffer.writeResourceLocation(coreId)
                buffer.writeInt(tierEntries.size)

                for ((id, tier) in tierEntries) {
                    buffer.writeResourceLocation(id)
                    buffer.writeJsonWithCodec(TierEntry.codec, tier)
                }
            }

            ServerPlayNetworking.send(player, Constants.SYNC_ID, buffer)
        } else {
            player.connection.disconnect(Component.literal("Please rejoin with Storage Cores installed."))
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

    fun getStyles(id: ResourceLocation): Map<ResourceLocation, StyleEntry>? = styles[id]

    fun getTiers(id: ResourceLocation): Map<ResourceLocation, TierEntry>? = tiers[id]
}