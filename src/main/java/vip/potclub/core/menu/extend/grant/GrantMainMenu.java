package vip.potclub.core.menu.extend.grant;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.WoolUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class GrantMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private final Player player;
    private final Document document;

    public GrantMainMenu(Player player, Document document) {
        super("Grants - Select", 9*3);
        this.player = player;
        this.document = document;
        this.update();
    }

    private void update() {
        AtomicInteger i = new AtomicInteger(0);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        getSortedRanks().forEach(rank -> {
            this.inventory.setItem(i.get(), new InventoryMenuItem(Material.WOOL, ((rank.getColor() != null) ? (ChatColor.getByChar(Color.translate(rank.getColor().replace("&", "").replace("§", ""))) != null) ? WoolUtil.getByColor(ChatColor.getByChar(Color.translate(rank.getColor().replace("&", "").replace("§", "")))) : 0 : 0))
                    .addLore(Arrays.asList(
                            network.getMainColor() + "&m--------------------------------",
                            network.getSecondaryColor() + "Priority: " + network.getMainColor() + rank.getWeight(),
                            network.getSecondaryColor() + "Prefix: " + network.getMainColor() + rank.getPrefix(),
                            network.getSecondaryColor() + "Suffix: " + network.getMainColor() + rank.getSuffix(),
                            network.getSecondaryColor() + "Visible: " + network.getMainColor() + rank.isHidden(),
                            network.getSecondaryColor() + "Color: " + network.getMainColor() + rank.getColor() + "Example",
                            "",
                            ChatColor.GREEN + "Left-Click to grant the " + rank.getColor() + rank.getName() + ChatColor.GREEN + " rank!",
                            ChatColor.GREEN + "Right-Click to grant with scope selection.",
                            network.getMainColor() + "&m--------------------------------"
                    ))
                    .setDisplayName(rank.getColor() + rank.getName())
                    .create()
            );

            i.getAndIncrement();
        });
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

            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName() != null) {
                    String display = ChatColor.stripColor(Color.translate(item.getItemMeta().getDisplayName()));
                    Rank rank = Rank.getByName(display);
                    PotPlayer potPlayer = PotPlayer.getPlayer(this.player);

                    if (event.getClick() == ClickType.RIGHT) {
                        this.player.closeInventory();
                        this.player.sendMessage(Color.translate("&cThis feature is currently in development."));
                    } else {
                        if (rank != null) {
                            if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && !player.isOp()) {
                                new GrantSelectDurationMenu(this.player, this.document, rank).open(player);
                            } else if ((potPlayer.getActiveGrant().getRank().getWeight() >= rank.getWeight()) && player.isOp()) {
                                new GrantSelectDurationMenu(this.player, this.document, rank).open(player);
                            } else {
                                this.player.sendMessage(Color.translate("&cYou cannot grant a rank weight a weight that is higher than yours."));
                                this.player.closeInventory();
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Rank> getSortedRanks() {
        return Rank.getRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).collect(Collectors.toList());
    }
}
