package compass_system.storagecores.base.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface ReplacingMap<T> {
    boolean shouldReplace();
    Map<ResourceLocation, T> values();
}
