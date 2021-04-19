package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public final class ExperienceUtil {

    /**
     * Add a certain amount of experience to a player.
     * <p>
     *
     * @param player Target player to add the experience to.
     * @param amount Amount of experience to add.
     */
    public static void addExperience(Player player, int amount) {
        if (amount == 0 || amount <= Integer.MIN_VALUE) {
            return;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(potPlayer.getExperience() + amount);
            player.sendMessage(Color.SECONDARY_COLOR + "You've received " + Color.MAIN_COLOR + amount + Color.SECONDARY_COLOR + " Experience!");
        }
    }

    /**
     * Returns a {@link List<String>} with the top 10 experience players.
     * <p>
     *
     * @return The string list.
     */
    public static List<String> getLeaderboardList(String sortingString) {
        final List<String> stringArrayList = new ArrayList<>();
        final Document sortingDocument = new Document();
        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        final AtomicInteger lineInt = new AtomicInteger();

        sortingDocument.put(sortingString, -1);

        CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find().sort(sortingDocument).limit(10).iterator()).thenAccept(documentMongoCursor -> {
            while (documentMongoCursor.hasNext()) {
                Document document = documentMongoCursor.next();
                int amountOfSort;

                try {
                    amountOfSort = document.getInteger(sortingString);
                } catch (Exception ignored) {
                    continue;
                }

                stringArrayList.add(Color.MAIN_COLOR + ChatColor.BOLD.toString() + lineInt.getAndIncrement() + "." + ChatColor.GRAY + " - " + ChatColor.WHITE + document.getString("name") + ChatColor.GRAY + " - " + Color.SECONDARY_COLOR + amountOfSort);
            }
        });

        return stringArrayList;
    }

    /**
     * Set a certain amount of experience to a player's profile.
     * <p>
     *
     * @param player Target player to add the experience to.
     * @param amount Amount of experience to set it to.
     */
    public static void setExperience(Player player, int amount) {
        if (amount == 0 || amount <= Integer.MIN_VALUE) {
            return;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(amount);
            player.sendMessage(Color.SECONDARY_COLOR + "Your experience has been set to " + Color.MAIN_COLOR + amount + Color.SECONDARY_COLOR + "!");
        }
    }

    /**
     * Remove a certain amount of experience from a player.
     * <p>
     *
     * @param player Target player to add the experience to.
     * @param amount Amount of experience to remove.
     */
    public static void removeExperience(Player player, int amount) {
        if (amount == 0 || amount <= Integer.MIN_VALUE) {
            return;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(potPlayer.getExperience() - amount);
            player.sendMessage(Color.MAIN_COLOR + String.valueOf(amount) + Color.SECONDARY_COLOR + " experience has been removed from your profile.");
        }
    }
}
