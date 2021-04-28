package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DisguiseManualCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.disguise")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length < 3) {
           player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/disguisemanual " + ChatColor.WHITE + "<rank> <name> <player>");
        }
        if (args.length == 3) {
            final Player target = Bukkit.getPlayerExact(args[2]);

            if (target != null) {
                final Rank rank = Rank.getByName(args[0]);

                if (rank == null) {
                    player.sendMessage(ChatColor.RED + "Error: That rank is not valid.");
                    return false;
                }

                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                if (potPlayer.isDisguised()) {
                    player.sendMessage(ChatColor.RED + "That player's already disguised with the name " + ChatColor.YELLOW + potPlayer.getName() + ChatColor.RED + "!");
                    return false;
                }

                final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getByName(args[1]);

                if (disguiseData != null) {
                    CorePlugin.getInstance().getDisguiseManager().disguise(player, disguiseData, disguiseData, rank);

                    player.sendMessage(Color.SECONDARY_COLOR + "You've disguised " + Color.MAIN_COLOR + player.getDisplayName() + Color.SECONDARY_COLOR + " as " + Color.translate(rank.getColor() + rank.getItalic()) + disguiseData.getName() + ChatColor.GRAY + " (with a random skin)" + Color.SECONDARY_COLOR + ".");
                } else {
                    CompletableFuture.runAsync(() -> {
                        final UUID uuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(args[1]);

                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Error: That disguise name is not valid.");
                            return;
                        }

                        final DisguiseData generatedData = CorePlugin.getInstance().getDisguiseManager().getDisguiseData(args[1], uuid);

                        CorePlugin.getInstance().getDisguiseManager().disguiseOther(player, generatedData, generatedData, rank);

                        player.sendMessage(Color.SECONDARY_COLOR + "You've disguised " + Color.MAIN_COLOR + player.getDisplayName() + Color.SECONDARY_COLOR + " as " + Color.translate(rank.getColor() + rank.getItalic()) + generatedData.getName() + ChatColor.GRAY + " (with a random skin)" + Color.SECONDARY_COLOR + ".");
                    });
                }
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("nick");
    }
}
