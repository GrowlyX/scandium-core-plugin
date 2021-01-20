package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishSelectReasonMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private PunishmentType punishmentType;

    public PunishSelectReasonMenu(Player player, Player target, PunishmentType punishmentType) {
        super("Punishment - Reason", 9*3);
        this.player = player;
        this.target = target;
        this.punishmentType = punishmentType;
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
