package compass_system.storagecores.base

import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import org.slf4j.LoggerFactory
import java.util.function.Function

object Constants {
    const val MOD_ID = "storagecores"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment || System.getProperties().contains(MOD_ID + "debug")

    val WOOD_TIER = resloc("wood")
    val COPPER_TIER = resloc("copper")
    val IRON_TIER = resloc("iron")
    val GOLD_TIER = resloc("gold")
    val DIAMOND_TIER = resloc("diamond")
    val NETHERITE_TIER = resloc("netherite")

    val DEFAULT_TYPE = resloc("default")
    val VANILLA_TYPE = resloc("vanilla")

    val DEFAULT_STYLE = resloc("default")

    private val normalisedResourceLocations = mutableMapOf<ResourceLocation, ResourceLocation>().apply {
        put(WOOD_TIER, WOOD_TIER)
        put(COPPER_TIER, COPPER_TIER)
        put(IRON_TIER, IRON_TIER)
        put(GOLD_TIER, GOLD_TIER)
        put(DIAMOND_TIER, DIAMOND_TIER)
        put(NETHERITE_TIER, NETHERITE_TIER)

        put(DEFAULT_TYPE, DEFAULT_TYPE)
        put(VANILLA_TYPE, VANILLA_TYPE)

        put(DEFAULT_STYLE, DEFAULT_STYLE)
    }

    // todo: might be premature optimization
    /**
     * Ensures the resource location given always returns the same object.
     *
     * @return A [ResourceLocation] such that `location.equals(out)` but `location == out` may be false.
     */
    fun normalise(location: ResourceLocation): ResourceLocation {
        return normalisedResourceLocations.computeIfAbsent(location) { it }
    }

    fun normalisedResourceLocationCodec(): Codec<ResourceLocation> {
        return ResourceLocation.CODEC.xmap(Constants::normalise, Function.identity())
    }

    fun resloc(path: String) = ResourceLocation(MOD_ID, path)
}