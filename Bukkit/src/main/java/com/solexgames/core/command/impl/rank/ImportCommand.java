package com.solexgames.core.command.impl.rank;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.config.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Command(label = "import", permission = "scandium.command.import", consoleOnly = true)
public class ImportCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new String[]{
                    "",
                    ChatColor.GREEN + ChatColor.BOLD.toString() + "Would you like to import the ranks from the ranks.yml?",
                    ChatColor.GRAY + "If you proceed, make sure to understand all the current",
                    ChatColor.GRAY + "ranks will be deleted and replaced with the new ones.",
                    "",
                    ChatColor.YELLOW + "[Use /import confirm to start the rank importation]",
            });
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("import")) {
                sender.sendMessage(ChatColor.GRAY + "Importing ranks from the configuration...");

                CompletableFuture.supplyAsync(this::handleImport)
                        .thenAccept(aBoolean -> sender.sendMessage(ChatColor.GREEN + "Successfully imported all ranks!"));
            }
        }

        return false;
    }

    private boolean handleImport() {
        final FileConfig config = CorePlugin.getInstance().getRanksConfig();

        Rank.getRanks().clear();
        CorePlugin.getInstance().getCoreDatabase().getRankCollection().drop();

        config.getConfiguration().getKeys(false).forEach(key -> {
            final String prefix = config.getString(key + ".prefix", "&7", false);
            final String suffix = config.getString(key + ".suffix", "&7", false);
            final String color = config.getString(key + ".color", "&7", false);

            final int weight = config.getInt(key + ".weight");
            final boolean defaultRank = config.getBoolean(key + ".defaultRank");

            final List<String> permissions = config.getStringList(key + ".permissions", new ArrayList<>());

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, key, prefix, color, suffix, defaultRank, weight));
        });

        config.getConfiguration().getKeys(false).stream()
                .map(key -> Rank.getByName(config.getString(key)))
                .filter(Objects::nonNull)
                .forEach(rank -> {
                    final List<String> stringList = config.getStringList(rank.getName() + ".inheritance", new ArrayList<>());

                    stringList.stream()
                            .map(s -> Rank.getByName(config.getString(s)))
                            .filter(Objects::nonNull)
                            .map(Rank::getUuid)
                            .forEach(rank.getInheritance()::add);
                });

        CorePlugin.getInstance().getRankManager().saveRanks();

        return true;
    }
}
