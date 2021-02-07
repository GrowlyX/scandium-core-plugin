package vip.potclub.core.version.extend;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.util.Color;
import vip.potclub.core.version.AbstractBukkitVersionImplementation;

public class PingCommand_1_7 extends AbstractBukkitVersionImplementation {

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
