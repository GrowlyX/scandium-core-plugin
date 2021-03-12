package com.solexgames.core.command.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnMuteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.unmute")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
        }
        if (args.length > 1) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            final String message = StringUtil.buildMessage(args, 1);

            if (offlinePlayer != null) {
                CorePlugin.getInstance().getPunishmentManager().handleUnpunishment(offlinePlayer, message, (sender instanceof Player ? (Player) sender : null), PunishmentType.MUTE);
            } else {
                sender.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }

        return false;
    }
}
