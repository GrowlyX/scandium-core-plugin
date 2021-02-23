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
import vip.potclub.core.util.DateUtil;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.SaltUtil;

import java.util.Date;
import java.util.UUID;

public class AnticheatBanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /acban <player> <duration>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        Punishment punishment = new Punishment(PunishmentType.BAN, null, target.getUniqueId(), "Console", "[AC] Unfair Advantage", new Date(System.currentTimeMillis()), PunishmentDuration.MONTH.getDuration(), true, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7), true);
                        punishment.savePunishment();

                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                        potPlayer.getPunishments().add(punishment);

                        CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, null, target.getName(), true);
                        potPlayer.saveWithoutRemove();

                        RedisUtil.writeAsync(RedisUtil.executePunishment(PunishmentType.BAN, null, target.getUniqueId(), "Console", "[AC] Unfair Advantage", new Date(System.currentTimeMillis()), PunishmentDuration.MONTH.getDuration(), true, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7), false));
                    } else {
                        sender.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
                if (args.length == 2) {
                    try {
                        Player target = Bukkit.getPlayerExact(args[0]);
                        if (target != null) {
                            Punishment punishment = new Punishment(PunishmentType.BAN, null, target.getUniqueId(), "Console", "[AC] Unfair Advantage", new Date(System.currentTimeMillis()), System.currentTimeMillis() - DateUtil.parseDateDiff(args[1], false), true, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7), true);
                            punishment.savePunishment();

                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                            potPlayer.getPunishments().add(punishment);

                            CorePlugin.getInstance().getPunishmentManager().handlePunishment(punishment, null, target.getName(), true);
                            potPlayer.saveWithoutRemove();

                            RedisUtil.writeAsync(RedisUtil.executePunishment(PunishmentType.BAN, null, target.getUniqueId(), "Console", "[AC] Unfair Advantage", new Date(System.currentTimeMillis()), System.currentTimeMillis() - DateUtil.parseDateDiff(args[1], false), true, new Date(), UUID.randomUUID(), SaltUtil.getRandomSaltedString(7), false));
                        } else {
                            sender.sendMessage(Color.translate("&cThat player does not exist."));
                        }
                    } catch (Exception ignored) {
                        sender.sendMessage(Color.translate("&cInvalid duration."));
                    }
                }
            }
            return false;
        }
        return false;
    }
}
