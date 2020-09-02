package me.growlyx.core.profile.punishments.freeze.managers;

import me.growlyx.core.Core;
import me.growlyx.core.profile.Manager;
import me.growlyx.core.utils.Snapshot;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager extends Manager {
    private Map<UUID, Snapshot> playerSnapshotMap;

    public PlayerManager(final Core plugin) {
        super(plugin);
        this.playerSnapshotMap = new HashMap<UUID, Snapshot>();
    }

    public void takeSnapshot(final Player player) {
        this.playerSnapshotMap.put(player.getUniqueId(), new Snapshot(player));
    }

    public void restorePlayer(final Player player) {
        final Snapshot playerSnapshot = this.getSnapshot(player);
        if (playerSnapshot != null) {
            player.getInventory().clear();
            player.getInventory().setContents(playerSnapshot.getMainContent());
            player.getInventory().setArmorContents(playerSnapshot.getArmorContent());
            player.setWalkSpeed(playerSnapshot.getWalkSpeed());
            player.addPotionEffects((Collection)playerSnapshot.getPotionEffects());
            player.updateInventory();
            this.removeSnapshot(player);
        }
    }

    private void removeSnapshot(final Player player) {
        this.playerSnapshotMap.remove(player.getUniqueId());
    }

    private Snapshot getSnapshot(final Player player) {
        return this.playerSnapshotMap.get(player.getUniqueId());
    }
}
