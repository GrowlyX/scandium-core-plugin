package com.solexgames.core.version.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.version.AbstractVersionImplementation;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand_1_8 extends AbstractVersionImplementation {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        Player player = (Player) sender;
        if (args.length == 0) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Your ping is: " + serverType.getMainColor() + entityPlayer.ping));
        }
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                EntityPlayer entityPlayer = ((CraftPlayer) target).getHandle();
                player.sendMessage(Color.translate(serverType.getSecondaryColor() + target.getName() + "'s ping is: " + serverType.getMainColor() + entityPlayer.ping));
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
