package compass_system.storagecores.base.api.addon;

import net.minecraft.resources.ResourceLocation;

public interface AddonRegistry {
    void registerBase(ResourceLocation id) throws DuplicateEntryException;

    void requireCore(ResourceLocation id);
}
