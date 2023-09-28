package compass_system.storagecores.base

import compass_system.storagecores.base.api.addon.StorageCoreAddon
import compass_system.storagecores.base.commands.StylesCommands
import compass_system.storagecores.base.data.ReloadableDataHolder
import compass_system.storagecores.base.data.styles.StylesLoader
import compass_system.storagecores.base.data.tiers.TiersLoader
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import net.minecraft.server.packs.PackType

object Main : ModInitializer {
    override fun onInitialize() {
        val registry = AddonRegistryImpl

        val addons = FabricLoader.getInstance().getEntrypoints("storagecores:addon", StorageCoreAddon::class.java)

        for (addon in addons) {
            addon.initialize(registry)
        }

        val knownBases = registry.getKnownBases()
        val requiredCores = registry.getRequiredCores()

        val stylesLoader = StylesLoader(knownBases)
        val tiersLoader = TiersLoader(requiredCores)

        ResourceManagerHelper.get(PackType.SERVER_DATA).apply {
            registerReloadListener(stylesLoader)
            registerReloadListener(tiersLoader)
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            ReloadableDataHolder.setStyles(stylesLoader.styles)
            ReloadableDataHolder.setTiers(tiersLoader.tiers)
        }

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { server, resourceManger, success ->
            if (success) {
                ReloadableDataHolder.setStyles(stylesLoader.styles)
                ReloadableDataHolder.setTiers(tiersLoader.tiers)
            }
        }

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, playerJoining ->
            ReloadableDataHolder.sendValuesToPlayer(player, playerJoining)
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            dispatcher.register(Commands.literal(Constants.MOD_ID).apply {
                StylesCommands.register(this)
            })
        }
    }
}