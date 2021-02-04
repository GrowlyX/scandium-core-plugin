package vip.potclub.core.command.extend.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentDuration;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.SaltUtil;

import java.util.Date;
import java.util.UUID;

public class AnticheatBanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /acban <player>."));
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    Punishment punishment = new Punishment(PunishmentType.BAN, null, target.getUniqueId(), "Console", "[AC] Unfair Advantage", new Date(System.currentTimeMillis()), PunishmentDuration.MONTH.getDuration(), true, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7));
                    punishment.savePunishment();

                    PotPlayer potPlayer = PotPlayer.getPlayer(target);
                    potPlayer.getPunishments().add(punishment);

                    CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, null, target, true);
                    potPlayer.saveWithoutRemove();

                    sender.sendMessage(Color.translate("&aExecuted punishment."));
                } else {
                    sender.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
            return false;
        }
        return false;
    }
}
