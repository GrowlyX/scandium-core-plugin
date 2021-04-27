package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.builder.PageListBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DisguiseListCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.disguiselist")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final PageListBuilder pageListBuilder = new PageListBuilder(10, "Disguised Players");
        final List<String> stringList = CorePlugin.getInstance().getPlayerManager().getAllProfiles().values().stream()
                .filter(uuidPotPlayerEntry -> uuidPotPlayerEntry != null && uuidPotPlayerEntry.isDisguised())
                .map(potPlayer -> potPlayer.getColorByRankColor() + potPlayer.getName() + ChatColor.GRAY + " (" + potPlayer.getOriginalName() + ")")
                .collect(Collectors.toList());

        if (args.length == 0) {
            pageListBuilder.display(sender, 1, stringList);
        }
        if (args.length == 1) {
            try {
                final int page = Integer.parseInt(args[0]);

                pageListBuilder.display(sender, page, stringList);
            } catch (Exception ignored) {
                player.sendMessage(ChatColor.RED + "That's not a valid page integer!");
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("nicked", "nicklist");
    }
}
