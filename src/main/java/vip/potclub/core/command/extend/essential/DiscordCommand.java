package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.util.Color;

public class DiscordCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        sender.sendMessage(serverType.getSecondaryColor() + "Discord: " + serverType.getMainColor() + serverType.getDiscordLink());
        return false;
    }
}
