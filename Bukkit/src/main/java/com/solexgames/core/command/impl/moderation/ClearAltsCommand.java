package com.solexgames.core.command.impl.moderation;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GrowlyX
 * @since 5/22/2021
 */

@Command(label = "clearalts", aliases = "removealts", permission = "scandium.command.clearalts", async = true)
public class ClearAltsCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
        }

        if (args.length == 1) {
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null);

            if (document != null) {
                final String ipAddress = document.getString("previousIpAddress");
                final Iterator<Document> cursor = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("previousIpAddress", ipAddress)).iterator();
                final AtomicInteger amount = new AtomicInteger();

                cursor.forEachRemaining(alt -> {
                    if (!alt.getString("name").equals(document.getString("name"))) {
                        CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().deleteOne(alt);
                        amount.getAndIncrement();
                    }
                });

                player.sendMessage(Color.SECONDARY_COLOR + "You've just deleted " + Color.MAIN_COLOR + amount.get() + Color.SECONDARY_COLOR + " alternate accounts from " + Color.MAIN_COLOR + document.getString("name") + "'s" + Color.SECONDARY_COLOR + " history.");
            } else {
                player.sendMessage(ChatColor.RED + "Couldn't find any alts related to " + ChatColor.YELLOW + args[0] + ChatColor.RED + ".");
            }
        }

        return false;
    }
}
