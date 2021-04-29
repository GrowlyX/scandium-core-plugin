package com.solexgames.core.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.Block;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class DisguiseManager {

    private final CorePlugin plugin = CorePlugin.getInstance();

    public DisguiseManager() {
        this.loadToCache();
    }

    public void loadToCache() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getDisguiseCollection().find().forEach((Block<? super Document>) document -> {
            CorePlugin.getInstance().getDisguiseCache().registerNewDataPair(new DisguiseData(UUID.fromString(document.getString("uuid")), document.getString("name"), document.getString("skin"), document.getString("signature")));
        }));
    }

    public void disguise(Player player, DisguiseData disguiseData, DisguiseData skinData, Rank rank) {
        final PotPlayer potPlayer = this.plugin.getPlayerManager().getPlayer(player);

        if (disguiseData.getUuid() != null && potPlayer != null) {
            potPlayer.setDisguiseRank(rank);
            potPlayer.setDisguised(true);
            potPlayer.setName(disguiseData.getName());

            this.setGameProfile(player, disguiseData, skinData);

            if (!player.hasMetadata("disguised")) {
                player.setMetadata("disguised", new FixedMetadataValue(this.plugin, true));
            }

            potPlayer.setupPlayerTag();
            potPlayer.setupPlayerList();

            player.sendMessage(Color.SECONDARY_COLOR + "You've disguised as " + Color.MAIN_COLOR + disguiseData.getName() + ChatColor.GRAY + " (with a random skin)" + Color.SECONDARY_COLOR + ".");
        }
    }

    public void disguiseOther(Player player, DisguiseData disguiseData, DisguiseData skinData, Rank rank) {
        final PotPlayer potPlayer = this.plugin.getPlayerManager().getPlayer(player);

        if (disguiseData.getUuid() != null && potPlayer != null) {
            potPlayer.setDisguiseRank(rank);
            potPlayer.setDisguised(true);
            potPlayer.setName(disguiseData.getName());

            this.setGameProfile(player, disguiseData, skinData);

            if (!player.hasMetadata("disguised")) {
                player.setMetadata("disguised", new FixedMetadataValue(this.plugin, true));
            }

            potPlayer.setupPlayerTag();
            potPlayer.setupPlayerList();
        }
    }

    /**
     * Fetch a player's skin then apply it to a target {@link GameProfile}
     * <p>
     *
     * @param name Name of the disguise profile.
     * @param uuid UUID of the target player's skin.
     */
    public DisguiseData getDisguiseData(String name, UUID uuid) {
        try {
            final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
            final JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            final String skin = json.get("value").getAsString();
            final String signature = json.get("signature").getAsString();

            return new DisguiseData(uuid, name, skin, signature);
        } catch (Exception exception) {
            return null;
        }
    }

    public void unDisguise(PotPlayer disguisePlayer, Player player) {
        try {
            final Object entityPlayer = player.getClass()
                    .getMethod("getHandle", ((Class<?>[]) null))
                    .invoke(player);
            final Class<?> entityHuman = entityPlayer.getClass().getSuperclass();

            Field declaredField;

            int maxVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[0]);
            int minVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[1]);

            if (maxVersion >= 1 && minVersion >= 9) {
                declaredField = entityHuman.getDeclaredField("bS");
            } else {
                declaredField = entityHuman.getDeclaredField("bH");
            }

            disguisePlayer.getGameProfile().getProperties().removeAll("textures");
            disguisePlayer.getGameProfile().getProperties().put("textures", new Property("textures", disguisePlayer.getSkin(), disguisePlayer.getSignature()));

            declaredField.setAccessible(true);
            declaredField.set(entityPlayer, disguisePlayer.getGameProfile());

            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.hidePlayer(player);
                    player1.showPlayer(player);
                }
            });

            disguisePlayer.setDisguiseRank(null);
            disguisePlayer.setDisguised(false);
            disguisePlayer.setName(disguisePlayer.getGameProfile().getName());

            this.updatePlayer(player);

            disguisePlayer.setupPlayerTag();
            disguisePlayer.setupPlayerList();

            if (player.hasMetadata("disguised")) {
                player.removeMetadata("disguised", this.plugin);
            }

            player.sendMessage(Color.SECONDARY_COLOR + "You've been undisguised and reset to your default skin.");
        } catch (Exception e) {
            plugin.getLogger().info("Something went wrong while trying to modify \"" + player.getName() + "\"'s GameProfile!");
            e.printStackTrace();
        }
    }


    /**
     * Modify a specified player's {@link GameProfile}.
     * <p>
     *
     * @param player       Target player to set the game-profile to.
     * @param disguiseData Data pair of the random disguise profile.
     */
    public void setGameProfile(Player player, DisguiseData disguiseData, DisguiseData skinData) {
        try {
            final Object entityPlayer = player.getClass()
                    .getMethod("getHandle", ((Class<?>[]) null))
                    .invoke(player);
            final Class<?> entityHuman = entityPlayer.getClass().getSuperclass();

            Field declaredField;

            int maxVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[0]);
            int minVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replaceAll("(v|R[0-9]+)", "").split("_")[1]);

            if (maxVersion >= 1 && minVersion >= 9) {
                declaredField = entityHuman.getDeclaredField("bS");
            } else {
                declaredField = entityHuman.getDeclaredField("bH");
            }

            final GameProfile gameProfile = new GameProfile(player.getUniqueId(), disguiseData.getName());

            declaredField.setAccessible(true);
            declaredField.set(entityPlayer, gameProfile);

            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.hidePlayer(player);
                    player1.showPlayer(player);
                }
            });

            gameProfile.getProperties().removeAll("textures");
            gameProfile.getProperties().put("textures", new Property("textures", skinData.getSkin(), skinData.getSignature()));

            this.updatePlayer(player);
        } catch (Exception e) {
            plugin.getLogger().info("Something went wrong while trying to modify \"" + player.getName() + "\"'s GameProfile!");
            e.printStackTrace();
        }
    }

    private void updatePlayer(Player player) {
        CorePlugin.getInstance().getNMS().updatePlayer(player);
        CorePlugin.getInstance().getNMS().updateCache(player);
    }

    public GameProfile getGameProfile(Player player) {
        try {
            final Class<?> strClass = Class.forName("org.bukkit.craftbukkit." + this.getServerVersion() + ".entity.CraftPlayer");
            return (GameProfile) strClass.cast(player).getClass().getMethod("getProfile").invoke(strClass.cast(player));
        } catch (Exception ignored) {
            return null;
        }
    }

    public String getServerVersion() {
        String version;

        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (Exception exception) {
            return null;
        }

        return version;
    }
}
