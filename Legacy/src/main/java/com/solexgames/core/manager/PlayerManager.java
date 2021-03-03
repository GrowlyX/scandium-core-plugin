package com.solexgames.core.manager;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.extend.ModSuiteBoard;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StaffUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@NoArgsConstructor
public class PlayerManager {

    public final Map<UUID, PotPlayer> allProfiles = new HashMap<>();
    public final Map<String, String> allSyncCodes = new HashMap<>();
    public final Map<String, String> all2FACodes = new HashMap<>();

    public final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("freeze-message");

    public PotPlayer getPlayer(Player player) {
        return this.allProfiles.getOrDefault(player.getUniqueId(), null);
    }

    public PotPlayer getPlayer(UUID uuid) {
        return this.allProfiles.getOrDefault(uuid, null);
    }

    public PotPlayer getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            return null;
        } else {
            return this.allProfiles.getOrDefault(player.getUniqueId(), null);
        }
    }

    public void vanishPlayer(Player player) {
        PotPlayer vanishedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getActiveGrant().getRank().getWeight() < vanishedPotPlayer.getActiveGrant().getRank().getWeight())
                .forEach(potPlayer -> potPlayer.getPlayer().hidePlayer(player));

        CorePlugin.getInstance().getNMS().removeExecute(player);

        vanishedPotPlayer.setVanished(true);
        vanishedPotPlayer.setupPlayerTag();

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondColor = serverType.getSecondaryColor();

        player.sendMessage(Color.translate(secondColor + "You are now vanished to all online players with a priority less than " + mainColor + vanishedPotPlayer.getActiveGrant().getRank().getWeight() + secondColor + "."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);
    }

    public void vanishPlayerRaw(Player player) {
        PotPlayer vanishedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getActiveGrant().getRank().getWeight() < vanishedPotPlayer.getActiveGrant().getRank().getWeight())
                .forEach(potPlayer -> potPlayer.getPlayer().hidePlayer(player));

        CorePlugin.getInstance().getNMS().removeExecute(player);

        vanishedPotPlayer.setVanished(true);
        vanishedPotPlayer.setupPlayerTag();

        CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);
    }

    public void modModePlayer(Player player) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        ModSuiteBoard modSuiteBoard = new ModSuiteBoard(player);

        potPlayer.setStaffMode(true);
        potPlayer.setupPlayerTag();
        potPlayer.setArmorHistory(player.getInventory().getArmorContents());
        potPlayer.setItemHistory(player.getInventory().getContents());
        potPlayer.setModModeBoard(modSuiteBoard);

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new ItemBuilder(Material.SKULL_ITEM, 1).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new ItemBuilder(Material.NETHER_STAR).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random Player").create());

        player.getInventory().setItem(6, new ItemBuilder(Material.BOOK).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new ItemBuilder(Material.PACKED_ICE).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK, (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

        player.updateInventory();

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondColor = serverType.getSecondaryColor();

        player.sendMessage(Color.translate(secondColor + "You are now in moderation mode."));

        StaffUtil.sendAlert(player, "modmoded");
    }

    public void modModeRaw(Player player) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        ModSuiteBoard modSuiteBoard = new ModSuiteBoard(player);

        potPlayer.setStaffMode(true);
        potPlayer.setupPlayerTag();
        potPlayer.setArmorHistory(player.getInventory().getArmorContents());
        potPlayer.setItemHistory(player.getInventory().getContents());
        potPlayer.setModModeBoard(modSuiteBoard);

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.COMPASS).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new ItemBuilder(Material.SKULL_ITEM, 1).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new ItemBuilder(Material.NETHER_STAR).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random Player").create());

        player.getInventory().setItem(6, new ItemBuilder(Material.BOOK).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new ItemBuilder(Material.PACKED_ICE).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK, (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

        player.updateInventory();
    }

    public void unModModePlayer(Player player) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        player.getInventory().clear();
        player.getInventory().setContents(potPlayer.getItemHistory());
        player.getInventory().setArmorContents(potPlayer.getArmorHistory());

        potPlayer.setStaffMode(false);
        potPlayer.setupPlayerTag();
        potPlayer.getModModeBoard().deleteBoard();
        potPlayer.setModModeBoard(null);

        player.sendMessage(Color.translate(ChatColor.RED + "You have exited in moderation mode."));

        StaffUtil.sendAlert(player, "unmodmoded");
    }

    public Optional<Document> getDocumentByName(String name) {
        return Optional.ofNullable(CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("name", name)).first());
    }

    public void unVanishPlayer(Player player) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .forEach(p -> p.showPlayer(player));

        CorePlugin.getInstance().getNMS().addExecute(player);

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        potPlayer.setVanished(false);
        potPlayer.setupPlayerTag();

        player.sendMessage(Color.translate("&aYou are now visible to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(player);
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b") + message;
    }

    public void sendDisconnectFreezeMessage(Player target) {
        this.sendToNetworkStaff("&3[S] &7[" + CorePlugin.getInstance().getServerName() + "&7] &3" + target.getDisplayName() + " &cdisconnected while frozen!");
    }

    public void sendToNetworkStaff(String... strings) {
        for (String string : strings) {
            RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate(string), "scandium.staff"));
        }
    }

    public void sendFreezeMessage(Player player) {
        freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
