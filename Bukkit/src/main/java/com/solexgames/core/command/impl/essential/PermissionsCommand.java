package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionsCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.permissions")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        List<String> permissions = player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .collect(Collectors.toList());
        int permissionsCount = permissions.size();

        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));
        StringUtil.sendCenteredMessage(player, Color.SECONDARY_COLOR + "You currently have " + Color.MAIN_COLOR + "" + permissionsCount + Color.SECONDARY_COLOR + " permissions!");
        player.sendMessage("  ");
        permissions.forEach(s -> player.sendMessage(ChatColor.GRAY + " * " + Color.MAIN_COLOR + s));
        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));

        return false;
    }
}
