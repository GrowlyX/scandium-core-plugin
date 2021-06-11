package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author GrowlyX
 * @since 6/9/2021
 */

@Command(label = "removeprofile", consoleOnly = true)
public class RemoveProfileCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <uuid>");
        }

        if (args.length > 0) {
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(UUID.fromString(args[0]))
                    .orElse(null);

            if (document != null) {
                CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().deleteOne(document);

                sender.sendMessage(ChatColor.RED + "Deleted their profile.");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to find that player.");
            }
        }

        return false;
    }
}
