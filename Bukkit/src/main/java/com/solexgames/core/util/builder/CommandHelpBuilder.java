package com.solexgames.core.util.builder;

import com.google.common.base.Preconditions;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author FrozenOrb
 * @revised GrowlyX 4/26/2021
 */

public class CommandHelpBuilder {

    private final int resultsPerPage;
    private final String resultsFor;

    public CommandHelpBuilder(int resultsPerPage, String resultsFor) {
        Preconditions.checkArgument(resultsPerPage > 0);

        this.resultsFor = resultsFor;
        this.resultsPerPage = resultsPerPage;
    }

    public void display(CommandSender sender, int page, List<String> results) {
        if (results.size() == 0) {
            sender.sendMessage(ChatColor.RED + "There aren't any results for " + ChatColor.YELLOW + this.resultsFor + ChatColor.RED + "!");
        } else {
            final int maxPages = results.size() / this.resultsPerPage + 1;

            if (page > 0 && page <= maxPages) {
                sender.sendMessage(Color.MAIN_COLOR + "=== " + Color.SECONDARY_COLOR + "Showing help for " + Color.MAIN_COLOR + "/" + this.resultsFor + Color.SECONDARY_COLOR + ". " + Color.MAIN_COLOR + "===");

                for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
                    sender.sendMessage(ChatColor.GRAY + "- " + Color.SECONDARY_COLOR + results.get(i).replace("<",  Color.MAIN_COLOR + "<"));
                }

                sender.sendMessage(Color.SECONDARY_COLOR + "Showing page " + Color.MAIN_COLOR + page + Color.SECONDARY_COLOR + " of " + Color.MAIN_COLOR + maxPages + Color.SECONDARY_COLOR + " (" + Color.MAIN_COLOR + results.size() + Color.SECONDARY_COLOR + " results).");
            } else {
                sender.sendMessage(ChatColor.RED + "Page " + ChatColor.YELLOW + page + ChatColor.RED + " is out of bounds. (" + ChatColor.YELLOW + "1 - " + maxPages + ChatColor.RED + ")");
            }
        }
    }
}
