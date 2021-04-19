package com.solexgames.core.manager;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.impl.ModModeBoard;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.atomic.AtomicDocument;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@NoArgsConstructor
public class PlayerManager {

    public final Map<UUID, NetworkPlayer> allNetworkProfiles = new HashMap<>();
    public final Map<UUID, PotPlayer> allProfiles = new HashMap<>();

    public final Map<String, String> allSyncCodes = new HashMap<>();
    public final Map<String, String> all2FACodes = new HashMap<>();

    public final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("freeze-message");

    public PotPlayer getPlayer(Player player) {
        return this.allProfiles.getOrDefault(player.getUniqueId(), null);
    }

    public PotPlayer getPlayer(UUID uuid) {
        return this.allProfiles.getOrDefault(uuid, null);
    }

    public NetworkPlayer getNetworkPlayer(String player) {
        return this.allNetworkProfiles.values().stream()
                .filter(networkPlayer -> networkPlayer.getName().equalsIgnoreCase(player))
                .findFirst()
                .orElse(null);
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

    public void setupPlayer(AsyncPlayerPreLoginEvent event) {
        new PotPlayer(event.getUniqueId(), event.getName(), event.getAddress());
    }

    public boolean isOnline(String player) {
        return this.allNetworkProfiles.values()
                .stream()
                .filter(networkPlayer -> networkPlayer.getName().equalsIgnoreCase(player))
                .findFirst()
                .orElse(null) != null;
    }

    public PotPlayer getPlayer(String name) {
        final Player player = Bukkit.getPlayer(name);

        if (player == null) {
            return null;
        } else {
            return this.allProfiles.getOrDefault(player.getUniqueId(), null);
        }
    }

    public void vanishPlayer(Player player) {
        final PotPlayer vanishedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getActiveGrant().getRank().getWeight() < vanishedPotPlayer.getActiveGrant().getRank().getWeight())
                .forEach(potPlayer -> potPlayer.getPlayer().hidePlayer(player));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getNMS().removeExecute(player);
            CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);

            vanishedPotPlayer.setupPlayerTag();
            vanishedPotPlayer.setupPlayerList();
        });

        vanishedPotPlayer.setVanished(true);

        player.sendMessage(Color.SECONDARY_COLOR + "You are now vanished to all online players with a priority less than " + Color.MAIN_COLOR + vanishedPotPlayer.getActiveGrant().getRank().getWeight() + Color.SECONDARY_COLOR + ".");
    }

    public void vanishPlayerRaw(Player player) {
        final PotPlayer vanishedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getActiveGrant().getRank().getWeight() < vanishedPotPlayer.getActiveGrant().getRank().getWeight())
                .forEach(potPlayer -> potPlayer.getPlayer().hidePlayer(player));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getNMS().removeExecute(player);
            CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);

            vanishedPotPlayer.setupPlayerTag();
            vanishedPotPlayer.setupPlayerList();
        });

        vanishedPotPlayer.setVanished(true);
    }

    public void modModePlayer(Player player) {
        final ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        potPlayer.setStaffMode(true);
        potPlayer.setupPlayerTag();
        potPlayer.setArmorHistory(player.getInventory().getArmorContents());
        potPlayer.setItemHistory(player.getInventory().getContents());
        potPlayer.setPreviousBoard(player.getScoreboard());

        final ModModeBoard modModeBoard = new ModModeBoard(player);
        potPlayer.setModModeBoard(modModeBoard);

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new ItemBuilder(XMaterial.SKELETON_SKULL.parseMaterial(), 1).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random Player").create());

        player.getInventory().setItem(6, new ItemBuilder(XMaterial.BOOK.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new ItemBuilder(XMaterial.PACKED_ICE.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

        player.updateInventory();

        final ChatColor secondColor = Color.SECONDARY_COLOR;

        final double pitch = (double) (player.getLocation().getPitch() + 90.0F) * 3.141592653589793D / 180.0D;
        final double yaw = (double) (player.getLocation().getYaw() + 90.0F) * 3.141592653589793D / 180.0D;

        final double xCoordinate = Math.sin(pitch) * Math.cos(yaw);
        final double yCoordinate = Math.sin(pitch) * Math.sin(yaw);
        final double zCoordinate = Math.cos(pitch) * 100.0D;

        final org.bukkit.util.Vector vector = new org.bukkit.util.Vector(xCoordinate, zCoordinate, yCoordinate);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3));

        if (player.isSprinting()) {
            player.setVelocity(vector.multiply(50));
        }

        player.sendMessage(secondColor + "You are now in moderation mode.");

        PlayerUtil.sendAlert(player, "modmoded");

        if (CorePlugin.getInstance().getLunar() != null) {
            CorePlugin.getInstance().getLunar().enableStaffModules(player);
        }
    }

    public void modModeRaw(Player player) {
        final ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        potPlayer.setStaffMode(true);
        potPlayer.setupPlayerTag();
        potPlayer.setArmorHistory(player.getInventory().getArmorContents());
        potPlayer.setItemHistory(player.getInventory().getContents());
        potPlayer.setPreviousBoard(player.getScoreboard());

        final ModModeBoard modModeBoard = new ModModeBoard(player);
        potPlayer.setModModeBoard(modModeBoard);

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new ItemBuilder(XMaterial.SKELETON_SKULL.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random Player").create());

        player.getInventory().setItem(6, new ItemBuilder(XMaterial.BOOK.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new ItemBuilder(XMaterial.PACKED_ICE.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());

        player.updateInventory();

        final double pitch = (double) (player.getLocation().getPitch() + 90.0F) * 3.141592653589793D / 180.0D;
        final double yaw = (double) (player.getLocation().getYaw() + 90.0F) * 3.141592653589793D / 180.0D;

        final double xCoordinate = Math.sin(pitch) * Math.cos(yaw);
        final double yCoordinate = Math.sin(pitch) * Math.sin(yaw);
        final double zCoordinate = Math.cos(pitch) * 100.0D;

        final org.bukkit.util.Vector vector = new org.bukkit.util.Vector(xCoordinate, zCoordinate, yCoordinate);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3));

        if (player.isSprinting()) {
            player.setVelocity(vector.multiply(50));
        }

        if (CorePlugin.getInstance().getLunar() != null) {
            CorePlugin.getInstance().getLunar().enableStaffModules(player);
        }
    }

    public void unModModePlayer(Player player) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        player.getInventory().clear();
        player.getInventory().setContents(potPlayer.getItemHistory());
        player.getInventory().setArmorContents(potPlayer.getArmorHistory());

        potPlayer.setStaffMode(false);
        potPlayer.setupPlayerTag();
        potPlayer.getModModeBoard().remove();
        potPlayer.setModModeBoard(null);

        player.setScoreboard(potPlayer.getPreviousBoard());
        player.sendMessage(Color.translate(ChatColor.RED + "You've exited moderation mode."));

        player.setAllowFlight(false);
        player.setFlying(false);

        potPlayer.setPreviousBoard(null);

        PlayerUtil.sendAlert(player, "unmodmoded");

        player.removePotionEffect(PotionEffectType.SPEED);

        if (CorePlugin.getInstance().getLunar() != null) {
            CorePlugin.getInstance().getLunar().disableStaffModules(player);
        }
    }

    public NetworkPlayer getPlayerFromSyncCode(String syncCode) {
        return this.allNetworkProfiles.values().stream()
                .filter(networkPlayer -> networkPlayer.getDiscordCode().equalsIgnoreCase(syncCode))
                .findFirst()
                .orElse(null);
    }

    public Optional<Document> getDocumentByName(String name) {
        return Optional.ofNullable(CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("name", name)).first());
    }

    public Optional<Document> getDocumentByUuid(UUID uuid) {
        return Optional.ofNullable(CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first());
    }

    public CompletableFuture<Document> findOrMake(String playerName, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first();

            if (document == null) {
                final PotPlayer potPlayer = new PotPlayer(uuid, playerName, null);
                final AtomicDocument atomicDocument = new AtomicDocument();

                CompletableFuture.supplyAsync(() -> {
                    potPlayer.savePlayerData();
                    return true;
                }).thenRunAsync(() -> CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first())
                        .thenAcceptAsync(atomicDocument::setDocument));

                return atomicDocument.getDocument();
            } else {
                return document;
            }
        });
    }

    public void unVanishPlayer(Player player) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1 != player)
                .forEach(p -> p.showPlayer(player));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getNMS().addExecute(player);

            potPlayer.setVanished(false);
            potPlayer.setupPlayerTag();

            CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(player);
        });

        player.sendMessage(ChatColor.GREEN + Color.translate("You are now visible to all online players."));
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b") + message;
    }

    public void sendDisconnectFreezeMessage(Player target) {
        this.sendToNetworkStaff("&3[S] &7[" + CorePlugin.getInstance().getServerName() + "] &b" + target.getDisplayName() + " &cdisconnected whilst being frozen!");
    }

    public void sendToNetworkStaff(String... strings) {
        for (String string : strings) {
            RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate(string), "scandium.staff"));
        }
    }

    public void sendFreezeMessage(Player player) {
        this.freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public NetworkPlayer getNetworkPlayer(Player player) {
        return this.getAllNetworkProfiles().getOrDefault(player.getUniqueId(), null);
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
