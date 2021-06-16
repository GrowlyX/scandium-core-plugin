package com.solexgames.core.command.impl.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.StringUtil;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command(label = "grantmanual", permission = "scandium.command.grantmanual")
public class GrantManualCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <rank> <duration> <reason>");
        }
        if (args.length > 2) {
            final UUID uuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(args[0]);

            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "Error: That uuid is not valid.");
                return false;
            }

            CorePlugin.getInstance().getPlayerManager().findOrMake(args[0], uuid)
                    .thenAcceptAsync(document -> {
                        if (document == null) {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
                            return;
                        }

                        final Rank rank = Rank.getByName(args[1]);

                        if (rank == null) {
                            sender.sendMessage(ChatColor.RED + "Error: That rank does not exist.");
                            return;
                        }

                        final String reason = StringUtil.buildMessage(args, 3);
                        final Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), -1L, reason, true, true, "global");

                        switch (args[2].toLowerCase()) {
                            case "perm": case "permanent": case "forever":
                                newGrant.setPermanent(true);
                                break;
                            default:
                                final long date = DateUtil.parseDateDiff(args[2], false);

                                if (date == -1L) {
                                    newGrant.setPermanent(true);
                                    break;
                                }

                                final long duration = System.currentTimeMillis() - date;

                                newGrant.setDuration(duration);
                                break;
                        }

                        CorePlugin.getInstance().getPlayerManager().handleGrant(newGrant, document, sender instanceof Player ? (Player) sender : null, CorePlugin.getInstance().getServerName(), false);
                    });
        }

        return false;
    }
}
