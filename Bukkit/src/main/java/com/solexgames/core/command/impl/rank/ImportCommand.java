package com.solexgames.core.command.impl.rank;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.external.ConfigExternal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ImportCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(new String[]{
                    "",
                    ChatColor.GREEN + ChatColor.BOLD.toString() + "Would you like to import the ranks from the ranks.yml?",
                    ChatColor.GRAY + "If you proceed, make sure to understand all the current",
                    ChatColor.GRAY + "ranks will be deleted and replaced with the new ones.",
                    ""
            });

            player.spigot().sendMessage(
                    new Clickable(ChatColor.GREEN + ChatColor.BOLD.toString() + "[CONFIRM]", ChatColor.GREEN + "Click to import all ranks from the ranks.yml.", "/import confirm").asComponents()
            );

            player.sendMessage("  ");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("import")) {
                player.sendMessage(ChatColor.GRAY + "Importing ranks from the configuration...");

                CompletableFuture.supplyAsync(this::handleImport).thenAccept(aBoolean -> player.sendMessage(ChatColor.GREEN + "Successfully imported all ranks!"));
            }
        }

        return false;
    }

    private boolean handleImport() {
        final ConfigExternal config = CorePlugin.getInstance().getRanksConfig();

        Rank.getRanks().clear();
        CorePlugin.getInstance().getCoreDatabase().getRankCollection().drop();

        config.getConfiguration().getKeys(false).forEach(key -> {
            final String prefix = config.getString(key + ".prefix", "&7", false);
            final String suffix = config.getString(key + ".suffix", "&7", false);
            final String color = config.getString(key + ".color", "&7", false);

            final int weight = config.getInt(key + ".weight");
            final boolean defaultRank = config.getBoolean(key + ".defaultRank");

            final List<String> permissions = config.getStringListOrDefault(key + ".permissions", new ArrayList<>());

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, key, prefix, color, suffix, defaultRank, weight));
        });

        config.getConfiguration().getKeys(false).stream()
                .map(key -> Rank.getByName(config.getString(key)))
                .filter(Objects::nonNull)
                .forEach(rank -> {
                    final List<String> stringList = config.getStringListOrDefault(rank.getName() + ".inheritance", new ArrayList<>());

                    stringList.stream()
                            .map(s -> Rank.getByName(config.getString(s)))
                            .filter(Objects::nonNull)
                            .map(Rank::getUuid)
                            .forEach(rank.getInheritance()::add);
                });

        CorePlugin.getInstance().getRankManager().saveRanks();

        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("importrankdata");
    }
}
