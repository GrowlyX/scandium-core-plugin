package vip.potclub.core.menu.extend.punish;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.AbstractMenuItem;
import vip.potclub.core.player.punishment.PunishmentDuration;
import vip.potclub.core.player.punishment.PunishmentType;

@Getter
@Setter
public class PunishSelectConfirmMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;
    private Player target;
    private String reason;
    private PunishmentType punishmentType;
    private PunishmentDuration punishmentDuration;

    public PunishSelectConfirmMenu(Player player, Player target, String reason, PunishmentType punishmentType, PunishmentDuration punishmentDuration) {
        super("Punishment - Confirm", 9*3);
        this.player = player;
        this.target = target;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.punishmentDuration = punishmentDuration;
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
