package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StaffUtil;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class PlayerManager {

    public final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("freeze-message");

    public void vanishPlayer(Player player) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .filter(player1 -> !player1.hasPermission("scandium.vanished.see"))
                .forEach(p -> p.hidePlayer(player));

        CorePlugin.getInstance().getNMS().removeExecute(player);

        PotPlayer potPlayer = PotPlayer.getPlayer(player);
        potPlayer.setVanished(true);
        potPlayer.setupPlayerTag();

        player.sendMessage(Color.translate("&aYou are now vanished to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);
    }

    public void modModePlayer(Player player) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        potPlayer.setStaffMode(true);
        potPlayer.setupPlayerTag();
        potPlayer.setAllArmor(player.getInventory().getArmorContents());
        potPlayer.setAllItems(player.getInventory().getContents());

        player.getInventory().clear();

        player.getInventory().setItem(0, new InventoryMenuItem(Material.COMPASS).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new InventoryMenuItem(Material.SKULL_ITEM, 1).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new InventoryMenuItem(Material.NETHER_STAR).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random Player").create());

        player.getInventory().setItem(6, new InventoryMenuItem(Material.BOOK).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new InventoryMenuItem(Material.PACKED_ICE).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new InventoryMenuItem(Material.INK_SACK, (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

        player.updateInventory();

        player.sendMessage(Color.translate("&aYou have enabled Mod Mode."));

        StaffUtil.sendAlert(player, "modmoded");
    }

    public void unModModePlayer(Player player) {
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        player.getInventory().clear();
        player.getInventory().setContents(potPlayer.getAllItems());
        player.getInventory().setArmorContents(potPlayer.getAllArmor());

        potPlayer.setStaffMode(false);
        potPlayer.setupPlayerTag();

        if (CorePlugin.getInstance().getLunarCommand() != null) {

        }

        player.sendMessage(Color.translate("&cYou have exited Mod Mode."));

        StaffUtil.sendAlert(player, "unmodmoded");
    }

    public Document getDocumentByUuid(String name) {
        return CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("name", name)).first();
    }

    public void unVanishPlayer(Player player) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .forEach(p -> p.showPlayer(player));

        CorePlugin.getInstance().getNMS().addExecute(player);

        PotPlayer potPlayer = PotPlayer.getPlayer(player);
        potPlayer.setVanished(false);
        potPlayer.setupPlayerTag();

        player.sendMessage(Color.translate("&aYou are now visible to all online players."));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(player);
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b") + message;
    }

    public void sendDisconnectFreezeMessage(Player target) {
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&c&l" + target.getName() + "&c disconnected while being frozen!"), "scandium.staff"));
        RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("  "), "scandium.staff"));
    }

    public void sendFreezeMessage(Player player) {
        freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
