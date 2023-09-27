package compass_system.storagecores.base;

import com.mojang.serialization.Codec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Constants {
    public static final String MOD_ID = "storagecores";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment() || System.getProperties().contains(MOD_ID + "debug");

    public static final ResourceLocation WOOD_TIER = new ResourceLocation(MOD_ID, "wood");
    public static final ResourceLocation COPPER_TIER = new ResourceLocation(MOD_ID, "copper");
    public static final ResourceLocation IRON_TIER = new ResourceLocation(MOD_ID, "iron");
    public static final ResourceLocation GOLD_TIER = new ResourceLocation(MOD_ID, "gold");
    public static final ResourceLocation DIAMOND_TIER = new ResourceLocation(MOD_ID, "diamond");
    public static final ResourceLocation NETHERITE_TIER = new ResourceLocation(MOD_ID, "netherite");

    public static final ResourceLocation DEFAULT_TYPE = new ResourceLocation(MOD_ID, "default");
    public static final ResourceLocation VANILLA_TYPE = new ResourceLocation(MOD_ID, "vanilla");

    public static final ResourceLocation DEFAULT_STYLE = new ResourceLocation(MOD_ID, "default");

    private static final Map<ResourceLocation, ResourceLocation> normalisedResourceLocations;

    static  {
        normalisedResourceLocations = new HashMap<>();

        normalisedResourceLocations.put(WOOD_TIER, WOOD_TIER);
        normalisedResourceLocations.put(COPPER_TIER, COPPER_TIER);
        normalisedResourceLocations.put(IRON_TIER, IRON_TIER);
        normalisedResourceLocations.put(GOLD_TIER, GOLD_TIER);
        normalisedResourceLocations.put(DIAMOND_TIER, DIAMOND_TIER);
        normalisedResourceLocations.put(NETHERITE_TIER, NETHERITE_TIER);

        normalisedResourceLocations.put(DEFAULT_TYPE, DEFAULT_TYPE);
        normalisedResourceLocations.put(VANILLA_TYPE, VANILLA_TYPE);

        normalisedResourceLocations.put(DEFAULT_STYLE, DEFAULT_STYLE);
    }

    // todo: might be premature optimization
    /**
     * Ensures the resource location given always returns the same object.
     *
     * @return A {@link ResourceLocation} such that {@code in.equals(out)} but {@code in == out} may be false.
     */
    public static ResourceLocation normalise(ResourceLocation in) {
        return normalisedResourceLocations.computeIfAbsent(in, rl -> rl);
    }

    public static Codec<ResourceLocation> normalisedResourceLocationCodec() {
        return ResourceLocation.CODEC.xmap(Constants::normalise, Function.identity());
    }

    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private Constants() {
        throw new IllegalStateException("This class cannot be instantiated.");
    }
}
