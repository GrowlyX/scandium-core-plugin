package vip.potclub.core.command.extend.rank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.clickable.Clickable;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.external.ConfigExternal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RankImportCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.getUniqueId().equals(CorePlugin.getInstance().getServerManager().getNetwork().getMainOwner()) || player.getUniqueId().equals(CorePlugin.getInstance().getServerManager().getNetwork().getMainDeveloper())) {
            if (args.length == 0) {
                Clickable clickable = new Clickable(Color.translate("&4&l[CONFIRM]"), Color.translate("&cClick to import all ranks."), "/import confirm");
                player.sendMessage(Color.translate("  "));
                player.sendMessage(Color.translate("&aWould you like to import the ranks from the ranks.yml?"));
                player.sendMessage(Color.translate("&aIf you proceed, make sure to understand all the current"));
                player.sendMessage(Color.translate("&aranks will be deleted and replaced with the new ones."));
                player.sendMessage(Color.translate("&aThis also includes player profiles & grants."));
                player.sendMessage(Color.translate("  "));
                player.spigot().sendMessage(clickable.asComponents());
                player.sendMessage(Color.translate("  "));
            }
            if (args.length == 1) {
                player.sendMessage(Color.translate("&aImporting ranks..."));
                this.handleImport();
                player.sendMessage(Color.translate("&aSuccessfully imported all ranks!"));
            }
        } else {
            player.sendMessage(Color.translate("&cThis command is restricted."));
        }
        return false;
    }

    private void handleImport() {
        Rank.getRanks().clear();

        CorePlugin.getInstance().getMongoThread().execute(() -> {
            CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().drop();
            CorePlugin.getInstance().getCoreDatabase().getRankCollection().drop();
        });

        ConfigExternal config = CorePlugin.getInstance().getRanksConfig();

        for (String key : config.getConfiguration().getKeys(false)) {
            String name = config.getString(key + ".NAME");
            String prefix = config.getString(key + ".PREFIX", "&7", false);
            String suffix = config.getString(key + ".SUFFIX", "&7", false);
            String color = config.getString(key + ".COLOR", "&7", false);

            int weight = config.getInt(key + ".WEIGHT");
            boolean defaultRank = config.getBoolean(key + ".DEFAULT");

            List<String> permissions = config.getStringListOrDefault(key + ".PERMISSIONS", new ArrayList<>());

            new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, name, prefix, color, suffix, defaultRank, weight);
        }

        for (String key : config.getConfiguration().getKeys(false)) {
            Rank rank = Rank.getByName(config.getString(key + ".NAME"));
            if (rank != null) {
                for (String name2 : config.getStringListOrDefault(key + ".INHERITANCE", new ArrayList<>())) {
                    Rank other = Rank.getByName(config.getString(name2 + ".NAME"));
                    if (other != null) {
                        rank.getInheritance().add(other.getUuid());
                    }
                }
            }
        }

        CorePlugin.getInstance().getRankManager().saveRanks();
    }
}
