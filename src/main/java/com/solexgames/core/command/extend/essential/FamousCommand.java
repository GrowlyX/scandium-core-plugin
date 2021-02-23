package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FamousCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        player.sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor() + "Content Creators: " + CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + "https://" + CorePlugin.getInstance().getServerManager().getNetwork().getWebsiteLink() + "/famous/"));
        return false;
    }
}
