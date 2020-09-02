package me.growlyx.core.profile.punishments.freeze.handlers;

import me.growlyx.core.Core;
import me.growlyx.core.profile.punishments.freeze.managers.FreezeManager;
import me.growlyx.core.profile.punishments.freeze.managers.InventoryManager;
import me.growlyx.core.profile.punishments.freeze.managers.PlayerManager;

public class ManagerHandler {

    private Core plugin;
    private InventoryManager inventoryManager;
    private FreezeManager frozenManager;
    private PlayerManager playerSnapshotManager;

    public ManagerHandler(final Core plugin) {
        this.plugin = plugin;
        this.loadManagers();
    }

    private void loadManagers() {
        this.inventoryManager = new InventoryManager(this.plugin);
        this.frozenManager = new FreezeManager(this.plugin);
        this.playerSnapshotManager = new PlayerManager(this.plugin);
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public FreezeManager getFrozenManager() {
        return this.frozenManager;
    }

    public PlayerManager getPlayerSnapshotManager() {
        return this.playerSnapshotManager;
    }
}

