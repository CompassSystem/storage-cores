package messyellie.storagecores.base;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import messyellie.storagecores.base.commands.StylesCommands;
import messyellie.storagecores.base.data.ReloadableDataHolder;
import messyellie.storagecores.base.data.styles.StylesLoader;
import messyellie.storagecores.base.data.tiers.TiersLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Set;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        Set<ResourceLocation> knownBases = Set.of();
        StylesLoader stylesLoader = new StylesLoader(knownBases);
        TiersLoader tiersLoader = new TiersLoader(knownBases);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(stylesLoader);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(tiersLoader);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ReloadableDataHolder.setStyles(stylesLoader.getStyles());
            ReloadableDataHolder.setTiers(tiersLoader.getTiers());

        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                ReloadableDataHolder.setStyles(stylesLoader.getStyles());
                ReloadableDataHolder.setTiers(tiersLoader.getTiers());
            }
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ReloadableDataHolder::sendValuesToPlayer);

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Constants.MOD_ID);

            StylesCommands.register(builder);

            dispatcher.register(builder);
        });
    }
}
