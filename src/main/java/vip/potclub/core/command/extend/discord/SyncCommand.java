package vip.potclub.core.command.extend.discord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class SyncCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        ChatColor MC = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor();
        String MCB = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + ChatColor.BOLD.toString();
        ChatColor SC = CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor();

        player.sendMessage(Color.translate("  "));
        player.sendMessage(Color.translate(MCB + "DISCORD SYNC:"));
        player.sendMessage(Color.translate("  "));
        player.sendMessage(Color.translate(SC + "To sync your account with your discord account and receive"));
        player.sendMessage(Color.translate(SC + "the " + ChatColor.GREEN + "Verified" + SC + " role, copy this code '" + MC + PotPlayer.getPlayer(player).getSyncCode() + SC + "' and"));
        player.sendMessage(Color.translate(SC + "paste it into the " + MC + "#sync" + SC + " channel with " + MC + "'-sync'" + SC + " on our discord"));
        player.sendMessage(Color.translate(SC + "server!"));
        player.sendMessage(Color.translate("  "));
        return false;
    }
}
