package compass_system.storagecores.base;

import compass_system.storagecores.base.api.addon.AddonRegistry;
import compass_system.storagecores.base.api.addon.DuplicateEntryException;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class AddonRegistryImpl implements AddonRegistry {
    private final HashSet<ResourceLocation> knownBases = new HashSet<>();
    private final HashSet<ResourceLocation> requiredCores = new HashSet<>();

    @Override
    public void registerBase(ResourceLocation id) throws DuplicateEntryException {
        if (!knownBases.add(id)) {
            throw new DuplicateEntryException("Base \"" + id + "\" has already been registered.");
        }
    }

    @Override
    public void requireCore(ResourceLocation id) {
        requiredCores.add(id);
    }

    public Set<ResourceLocation> getKnownBases() {
        return Set.copyOf(knownBases);
    }
    public Set<ResourceLocation> getRequiredCores() {
        return Set.copyOf(requiredCores);
    }
}
