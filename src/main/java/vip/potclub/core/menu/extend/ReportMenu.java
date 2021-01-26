package vip.potclub.core.menu.extend;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

@Getter
@Setter
public class ReportMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;

    public ReportMenu(Player player, Player target) {
        super("Report", 9);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        this.inventory.setItem(2, new InventoryMenuItem(Material.DIAMOND_SWORD)
                .setDisplayName("&3Combat Hacks")
                .addLore(
                        "",
                        "&8&l● &fKillAura",
                        "&8&l● &fReach",
                        "&8&l● &fAim Assist"
                )
                .create()
        );
        this.inventory.setItem(3, new InventoryMenuItem(Material.DIAMOND_BOOTS)
                .setDisplayName("&3Movement Hacks")
                .addLore(
                        "",
                        "&8&l● &fSpeed",
                        "&8&l● &fBunny Hop",
                        "&8&l● &fFly"
                )
                .create()
        );
        this.inventory.setItem(4, new InventoryMenuItem(Material.SLIME_BALL)
                .setDisplayName("&3Velocity Hacks")
                .addLore(
                        "",
                        "&8&l● &fVelocity",
                        "&8&l● &fReduced KB",
                        "&8&l● &fAnti KB"
                )
                .create()
        );
        this.inventory.setItem(5, new InventoryMenuItem(Material.BED)
                .setDisplayName("&3Gameplay Sabotage")
                .addLore(
                        "",
                        "&8&l● &fCamping",
                        "&8&l● &fRunning",
                        "&8&l● &fStalling"
                )
                .create()
        );
        this.inventory.setItem(6, new InventoryMenuItem(Material.NAME_TAG)
                .setDisplayName("&3Chat Violation")
                .addLore(
                        "",
                        "&8&l● &fToxicity",
                        "&8&l● &fSpam",
                        "&8&l● &fAbuse"
                )
                .create()
        );
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            PotPlayer potPlayer = PotPlayer.getPlayer(player);

            if (item == null || item.getType() == Material.AIR) return;
            switch (event.getRawSlot()) {
                case 2:
                    player.closeInventory();
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onReport(player, target, "Combat Hacks")));
                        player.sendMessage(Color.translate("&aYour report has been sent to all online staff!"));
                        potPlayer.setCanReport(false);

                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                    break;
                case 3:
                    player.closeInventory();
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onReport(this.player, this.target, "Movement Hacks")));
                        player.sendMessage(Color.translate("&aYour report has been sent to all online staff!"));
                        potPlayer.setCanReport(false);

                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                    break;
                case 4:
                    player.closeInventory();
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onReport(this.player, this.target, "Velocity Hacks")));
                        player.sendMessage(Color.translate("&aYour report has been sent to all online staff!"));
                        potPlayer.setCanReport(false);

                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                    break;
                case 5:
                    player.closeInventory();
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onReport(this.player, this.target, "Gameplay Sabotage")));
                        player.sendMessage(Color.translate("&aYour report has been sent to all online staff!"));
                        potPlayer.setCanReport(false);

                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                    break;
                case 6:
                    player.closeInventory();
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onReport(this.player, this.target, "Chat Violation")));
                        player.sendMessage(Color.translate("&aYour report has been sent to all online staff!"));
                        potPlayer.setCanReport(false);

                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.closeInventory();
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                    break;
            }
        }
    }
}
