package me.growlyx.core.profile.punishments.freeze.managers;

import me.growlyx.core.Core;
import me.growlyx.core.profile.Manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager extends Manager {
    private Set<UUID> frozenUUIDs;

    public FreezeManager(final Core plugin) {
        super(plugin);
        this.frozenUUIDs = new HashSet<UUID>();
    }

    public void freezeUUID(final UUID uuid) {
        this.frozenUUIDs.add(uuid);
    }

    public void unfreezeUUID(final UUID uuid) {
        this.frozenUUIDs.remove(uuid);
    }

    public boolean isFrozen(final UUID uuid) {
        return this.frozenUUIDs.contains(uuid);
    }
}
