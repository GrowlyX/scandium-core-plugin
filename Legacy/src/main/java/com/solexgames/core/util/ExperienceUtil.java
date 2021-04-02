package com.solexgames.core.util;

import com.mongodb.client.MongoCursor;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
            player.sendMessage(serverType.getSecondaryColor() + "You've received " + serverType.getMainColor() + amount + serverType.getSecondaryColor() + " Experience!");
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

        CompletableFuture<MongoCursor<Document>> cursorCompletableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            MongoCursor<Document> cursor = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find().sort(sortingDocument).limit(10).iterator();
            cursorCompletableFuture.complete(cursor);
        });

        cursorCompletableFuture.thenAccept(documentMongoCursor -> {
            while (documentMongoCursor.hasNext()) {
                Document document = documentMongoCursor.next();
                int amountOfSort;

                try {
                    amountOfSort = document.getInteger(sortingString);
                } catch (Exception ignored) {
                    continue;
                }

                stringArrayList.add(serverType.getMainColor() + ChatColor.BOLD.toString() + lineInt.getAndIncrement() + "." + ChatColor.GRAY + " - " + ChatColor.WHITE + document.getString("name") + ChatColor.GRAY + " - " + serverType.getSecondaryColor() + amountOfSort);
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
