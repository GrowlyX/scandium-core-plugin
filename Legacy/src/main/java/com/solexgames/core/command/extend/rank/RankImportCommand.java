package com.solexgames.core.command.extend.rank;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.external.ConfigExternal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RankImportCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (player.isOp()) {
            player.sendMessage(Color.translate("&cThis command is restricted."));
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
                this.handleImport();
                player.sendMessage(ChatColor.GREEN + "Successfully imported all ranks!");
            }
        }

        return false;
    }

    private void handleImport() {
        Rank.getRanks().clear();

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().drop());

        ConfigExternal config = CorePlugin.getInstance().getRanksConfig();
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {

            config.getConfiguration().getKeys(false).forEach(key -> {
                String name = config.getString(key + ".NAME");
                String prefix = config.getString(key + ".PREFIX", "&7", false);
                String suffix = config.getString(key + ".SUFFIX", "&7", false);
                String color = config.getString(key + ".COLOR", "&7", false);

                int weight = config.getInt(key + ".WEIGHT");
                boolean defaultRank = config.getBoolean(key + ".DEFAULT");

                List<String> permissions = config.getStringListOrDefault(key + ".PERMISSIONS", new ArrayList<>());

                new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, name, prefix, color, suffix, defaultRank, weight);
            });

            config.getConfiguration().getKeys(false).stream()
                    .map(key -> Rank.getByName(config.getString(key + ".NAME")))
                    .filter(Objects::nonNull)
                    .forEach(rank -> {
                        for (String name2 : config.getStringListOrDefault(rank.getName().toUpperCase() + ".INHERITANCE", new ArrayList<>())) {
                            Rank other = Rank.getByName(config.getString(name2 + ".NAME"));
                            if (other != null) {
                                rank.getInheritance().add(other.getUuid());
                            }
                        }
                    });
        });

        CorePlugin.getInstance().getRankManager().saveRanks();
    }
}
