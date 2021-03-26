package com.solexgames.core.util;

import com.mongodb.client.MongoCursor;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.callback.XPFetchCallback;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(potPlayer.getExperience() + amount);
            player.sendMessage(serverType.getSecondaryColor() + "You have received " + serverType.getMainColor() + amount + serverType.getSecondaryColor() + " Experience!");
        }
    }

    /**
     * Returns a {@link List<String>} with the top 10 experience players.
     * <p>
     *
     * @return The string list.
     */
    public static List<String> getLeaderboardList(String sortingString) {
        List<String> stringArrayList = new ArrayList<>();
        Document sortingDocument = new Document();
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        AtomicInteger lineInt = new AtomicInteger();

        sortingDocument.put(sortingString, -1);

        ExperienceUtil.fetchDocument(sortingDocument, result -> {
            while (result.hasNext()) {
                Document document = result.next();
                int amountOfSort = 0;

                try {
                    amountOfSort = document.getInteger(sortingString);
                } catch (Exception ignored) { }

                stringArrayList.add(serverType.getMainColor() + ChatColor.BOLD.toString() + lineInt.getAndIncrement() + "." + ChatColor.GRAY + " - " + ChatColor.WHITE + document.getString("name") + ChatColor.GRAY + " - " + serverType.getSecondaryColor() + amountOfSort);
            }
        });

        return stringArrayList;
    }

    /**
     * Fetches a document then executes the callback Async
     * <p>
     *
     * @param sorting Sorting document
     * @param callback {@link XPFetchCallback} callback
     */
    public static void fetchDocument(Document sorting, XPFetchCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            final MongoCursor<Document> result = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find().sort(sorting).limit(10).iterator();

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> callback.onCompletion(result));
        });
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

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(amount);
            player.sendMessage(serverType.getSecondaryColor() + "Your experience has been set to " + serverType.getMainColor() + amount + serverType.getSecondaryColor() + "!");
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

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (potPlayer != null) {
            potPlayer.setExperience(potPlayer.getExperience() - amount);
            player.sendMessage(serverType.getMainColor() + String.valueOf(amount) + serverType.getSecondaryColor() + " experience has been removed from your profile.");
        }
    }
}
