package compass_system.storagecores.base

import compass_system.storagecores.base.data.SyncedDataHolder
import compass_system.storagecores.base.data.styles.StyleEntry
import compass_system.storagecores.base.data.tiers.TierEntry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.resources.ResourceLocation

object ClientMain : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.SYNC_ID) { client, handler, buffer, responseSender ->
            val bases = buffer.readInt()
            val styles: MutableMap<ResourceLocation, MutableMap<ResourceLocation, StyleEntry>> = mutableMapOf()

            for (i in 0 until bases) {
                val baseId = buffer.readResourceLocation()
                val numStyles = buffer.readInt()
                val baseStyles = styles.computeIfAbsent(baseId) { mutableMapOf() }
                for (k in 0 until numStyles) {
                    val styleId = buffer.readResourceLocation()
                    val style = buffer.readJsonWithCodec(StyleEntry.codec)
                    baseStyles[styleId] = style
                }
            }

            val cores = buffer.readInt()
            val tiers: MutableMap<ResourceLocation, MutableMap<ResourceLocation, TierEntry>> = mutableMapOf()

            for (i in 0 until cores) {
                val coreId = buffer.readResourceLocation()
                val numTiers = buffer.readInt()
                val coreTiers = tiers.computeIfAbsent(coreId) { mutableMapOf() }
                for (k in 0 until numTiers) {
                    val tierId = buffer.readResourceLocation()
                    val tier = buffer.readJsonWithCodec(TierEntry.codec)
                    coreTiers[tierId] = tier
                }
            }

            client.execute {
                SyncedDataHolder.receiveReloadableData(styles, tiers)
            }
        }
    }
}