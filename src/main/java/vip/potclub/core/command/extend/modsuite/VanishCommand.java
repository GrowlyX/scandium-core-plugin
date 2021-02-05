package vip.potclub.core.command.extend.modsuite;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.manager.ServerManager;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class VanishCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PlayerManager vanishManager = CorePlugin.getInstance().getPlayerManager();
        ServerManager manager = CorePlugin.getInstance().getServerManager();
        if (player.hasPermission("scandium.command.vanish")) {
            if (args.length == 0) {
                PotPlayer potPlayer = PotPlayer.getPlayer(player);
                if (manager.getVanishedPlayers().contains(player)) {
                    potPlayer.setVanished(false);
                    vanishManager.unVanishPlayer(player);
                } else {
                    potPlayer.setVanished(true);
                    vanishManager.vanishPlayer(player);
                }
            }
            if (args.length > 0) {
                if (player.hasPermission("scandium.command.vanish.other")) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        PotPlayer potPlayer = PotPlayer.getPlayer(target);
                        if (manager.getVanishedPlayers().contains(target)) {
                            vanishManager.unVanishPlayer(target);
                            potPlayer.setVanished(false);
                            player.sendMessage(Color.translate("&aUnvanished " + target.getName() + "."));
                        } else {
                            vanishManager.vanishPlayer(target);
                            potPlayer.setVanished(true);
                            player.sendMessage(Color.translate("&aVanished " + target.getName() + "."));
                        }
                    } else {
                        player.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cNo permission."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
