package compass_system.storagecores.base.data;

import com.mojang.serialization.JsonOps;
import compass_system.storagecores.base.Constants;
import compass_system.storagecores.base.data.tiers.TierEntry;
import compass_system.storagecores.base.data.styles.StyleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReloadableDataHolder {
    static Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> styles = Map.of();
    static Map<ResourceLocation, Map<ResourceLocation, TierEntry>> tiers = Map.of();

    public static void sendValuesToPlayer(ServerPlayer player, boolean playerJoining) {
        if (Constants.IS_DEBUG) {
            Constants.LOGGER.info("Sending reloadable data to: " + player.getName().getString() + ", is join: " + playerJoining);
        }
    }

    public static void setStyles(Map<ResourceLocation, Map<ResourceLocation, StyleEntry>> values) {
        if (Constants.IS_DEBUG) {
            StringBuilder builder = new StringBuilder();

            builder.append("Loading Styles:\n");
            for (Map.Entry<ResourceLocation, Map<ResourceLocation, StyleEntry>> stylesForBase : values.entrySet()) {
                builder.append("  Styles for \"").append(stylesForBase.getKey()).append("\":\n");
                for (Map.Entry<ResourceLocation, StyleEntry> style : stylesForBase.getValue().entrySet()) {
                    builder.append("    ").append(style.getKey()).append(" = ").append(StyleEntry.CODEC.encodeStart(JsonOps.INSTANCE, style.getValue()).getOrThrow(false, Constants.LOGGER::warn)).append("\n");
                }
            }

            Constants.LOGGER.info(builder.toString());
        }

        styles = values;
    }

    public static void setTiers(Map<ResourceLocation, Map<ResourceLocation, TierEntry>> values) {
        if (Constants.IS_DEBUG) {
            StringBuilder builder = new StringBuilder();

            builder.append("Loading Tiers:\n");
            for (Map.Entry<ResourceLocation, Map<ResourceLocation, TierEntry>> tiersForCore : values.entrySet()) {
                builder.append("  Tiers for \"").append(tiersForCore.getKey()).append("\":\n");
                for (Map.Entry<ResourceLocation, TierEntry> tier : tiersForCore.getValue().entrySet()) {
                    builder.append("    ").append(tier.getKey()).append(" = ").append(TierEntry.CODEC.encodeStart(JsonOps.INSTANCE, tier.getValue()).getOrThrow(false, Constants.LOGGER::warn)).append("\n");
                }
            }

            Constants.LOGGER.info(builder.toString());
        }

        tiers = values;
    }

    @Nullable
    public static Map<ResourceLocation, StyleEntry> getStyles(ResourceLocation id) {
        return styles.get(id);
    }
}
