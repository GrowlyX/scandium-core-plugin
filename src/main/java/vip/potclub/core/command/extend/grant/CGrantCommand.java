package vip.potclub.core.command.extend.grant;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.DateUtil;
import vip.potclub.core.util.StringUtil;

public class CGrantCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                    sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
                }
                if (args.length == 2) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                    sender.sendMessage(Color.translate("&cNOTE: This is not the final granting system."));
                }
                if (args.length > 2) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        Rank rank = Rank.getByName(args[1]);
                        if (rank != null) {
                            if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                                String reason = StringUtil.buildMessage(args, 3);
                                PotPlayer targetPotPlayer = PotPlayer.getPlayer(target);
                                Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), 2147483647L, reason, true, true);

                                targetPotPlayer.getAllGrants().add(newGrant);
                                targetPotPlayer.setupAttachment();
                                targetPotPlayer.saveWithoutRemove();

                                target.sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                sender.sendMessage(ChatColor.GREEN + Color.translate("Set " + target.getDisplayName() + ChatColor.GREEN + "'s rank to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                            } else {
                                try {
                                    String reason = StringUtil.buildMessage(args, 3);
                                    PotPlayer targetPotPlayer = PotPlayer.getPlayer(target);
                                    Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false), reason, true, false);

                                    targetPotPlayer.getAllGrants().add(newGrant);
                                    targetPotPlayer.setupAttachment();
                                    targetPotPlayer.saveWithoutRemove();

                                    target.sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                    sender.sendMessage(ChatColor.GREEN + Color.translate("Set " + target.getDisplayName() + ChatColor.GREEN + "'s rank to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                }
                                catch (Exception exception) {
                                    sender.sendMessage(ChatColor.RED + "Invalid duration.");
                                }
                            }
                        } else {
                            sender.sendMessage(Color.translate("&cThat rank does not exist."));
                        }
                    } else {
                        sender.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
            }
            return false;
        }
        return false;
    }
}