package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;

@Getter
@Setter
public class PunishMainMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;

    public PunishMainMenu(Player player, Player target) {
        super("Punishment - Main", 9*3);
        this.player = player;
        this.target = target;
        this.update();
    }

    private void update() {
        while (this.inventory.firstEmpty() != -1) {
            this.inventory.setItem(this.inventory.firstEmpty(), new AbstractMenuItem(Material.STAINED_GLASS_PANE, 7).setDisplayname(" ").create());
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }
}
