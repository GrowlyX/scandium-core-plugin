package vip.potclub.core.command.extend.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.manager.RankManager;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.rank.Rank;
import vip.potclub.core.util.Color;

public class RankCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission(ChatChannel.HOST.getPermission())) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUse /rank <player> <rank>"));
                player.sendMessage(Color.translate("&cThe ranking system is currently in BETA!"));
            }

            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate("&cUse /rank <player> <rank>"));
                    player.sendMessage(Color.translate("&cThe ranking system is currently in BETA!"));
                }
                if (args.length == 2) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    Rank rank = RankManager.getById(args[1]);
                    if (!sender.hasPermission("core.rank.setabove") && Bukkit.getPlayer(sender.getName()) != null) {
                        PotPlayer potPlayer = PotPlayer.getPlayer(target.getUniqueId());
                        if (rank != null) {
                            if (potPlayer.getRank().getWeight() <= rank.getWeight()) {
                                player.sendMessage(Color.translate("&cYou can not set a rank higher than your current rank."));
                            } else {
                                CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
                                    OfflinePlayer offlinePlayer = CorePlugin.getInstance().getServer().getOfflinePlayer(target.getName());

                                    CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().updateOne(offlinePlayer.hasPlayedBefore() ? Filters.eq("uuid", offlinePlayer.getUniqueId().toString()) : Filters.eq("name", offlinePlayer.getName()), Updates.set("rank", rank.getId()));
                                    CorePlugin.getInstance().getServer().getLogger().info(sender.getName() + " updated " + offlinePlayer.getName() + "'s permissive rank to " + rank.getName());

                                    sender.sendMessage(Color.translate("&dYou have granted &5" + offlinePlayer + "&d the &5" + rank.getColor() + rank.getName() + "&d rank."));

                                    if (offlinePlayer.isOnline()) {
                                        PotPlayer targetPotPlayer = PotPlayer.getPlayer(target.getUniqueId());

                                        if (targetPotPlayer != null) {
                                            targetPotPlayer.setRank(rank);
                                            targetPotPlayer.updateServerPlayer();
                                        }
                                    }
                                });
                            }
                        } else {
                            player.sendMessage(Color.translate("&cThat rank is null!"));
                        }
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
