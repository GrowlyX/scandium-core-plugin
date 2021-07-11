package com.solexgames.core.manager;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.impl.ModModeBoard;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.*;
import com.solexgames.core.util.atomic.AtomicDocument;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PlayerManager {

    private final Map<UUID, PotPlayer> allProfiles = new HashMap<>();

    private final Map<String, String> allSyncCodes = new HashMap<>();
    private final Map<String, String> all2FACodes = new HashMap<>();

    private final List<String> freezeMessage = CorePlugin.getInstance().getConfig().getStringList("language.freeze-message");
    private final List<NetworkPlayer> allNetworkProfiles = new ArrayList<>();

    public void setupPlayer(AsyncPlayerPreLoginEvent event) {
        new PotPlayer(event.getUniqueId(), event.getName(), event.getAddress());
    }

    public boolean isOnline(String player) {
        final List<NetworkPlayer> networkPlayers = new ArrayList<>(this.allNetworkProfiles);

        return networkPlayers.stream()
                .filter(networkPlayer -> networkPlayer.getName().equalsIgnoreCase(player))
                .findFirst().orElse(null) != null;
    }

    public PotPlayer getPlayer(String name) {
        final Player player = Bukkit.getPlayer(name);

        if (player == null) {
            return null;
        } else {
            return this.allProfiles.getOrDefault(player.getUniqueId(), null);
        }
    }

    public void vanishPlayer(Player player, Integer... ints) {
        final int power = ints[0] != null ? ints[0] : 0;

        this.vanishPlayerRaw(player, power);
        player.sendMessage(Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "You're now vanished with a power of " + Color.MAIN_COLOR + power + Color.SECONDARY_COLOR + ".");
    }

    public void modModePlayer(Player player) {
        if (CorePlugin.getInstance().getServerName().contains("hub")) {
            player.sendMessage(ChatColor.RED + "You cannot mod mode in hubs.");
            return;
        }

        this.modModeRaw(player);

        player.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "You're now in moderation mode.");
    }

    public void vanishPlayerRaw(Player player, int power) {
        final PotPlayer vanishedPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers().stream()
                .filter(player1 -> player1 != player)
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer != null && potPlayer.getActiveGrant().getRank().getWeight() < power)
                .forEach(potPlayer -> potPlayer.getPlayer().hidePlayer(player));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getNMS().removeExecute(player);
            CorePlugin.getInstance().getServerManager().getVanishedPlayers().add(player);

            vanishedPotPlayer.setupPlayerTag();
            vanishedPotPlayer.setupPlayerList();
        });

        vanishedPotPlayer.setVanished(true);
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

        this.applyItems(player);

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

        if (CorePlugin.getInstance().getClientHook() != null) {
            CorePlugin.getInstance().getClientHook().enableStaffModules(player);
        }
    }

    public void applyItems(Player player) {
        final ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final boolean practice = CorePlugin.getInstance().getServerName().contains("practice");

        player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Push Forward").create());
        player.getInventory().setItem(1, new ItemBuilder(XMaterial.SKELETON_SKULL.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Online Staff").create());
        player.getInventory().setItem(2, new ItemBuilder(XMaterial.NETHER_STAR.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Random " + (practice ? "Spectate" : "Player")).create());

        player.getInventory().setItem(6, new ItemBuilder(XMaterial.BOOK.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Inspect Player").create());
        player.getInventory().setItem(7, new ItemBuilder(XMaterial.PACKED_ICE.parseMaterial()).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + "Freeze Player").create());
        player.getInventory().setItem(8, new ItemBuilder(XMaterial.INK_SAC.parseMaterial(), (potPlayer.isVanished() ? 10 : 8)).setDisplayName(network.getMainColor() + ChatColor.BOLD.toString() + (potPlayer.isVanished() ? "Disable Vanish" : "Enable Vanish")).create());
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

        player.sendMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You're no longer in vanish.");
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
        player.setAllowFlight(false);
        player.setFlying(false);

        potPlayer.setPreviousBoard(null);

        player.removePotionEffect(PotionEffectType.SPEED);

        if (CorePlugin.getInstance().getClientHook() != null) {
            CorePlugin.getInstance().getClientHook().disableStaffModules(player);
        }

        player.sendMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You're no longer in moderation mode.");
    }

    public NetworkPlayer getPlayerFromSyncCode(String syncCode) {
        return this.allNetworkProfiles.stream()
                .filter(networkPlayer -> networkPlayer.getDiscordCode().equalsIgnoreCase(syncCode))
                .findFirst().orElse(null);
    }

    public Optional<Document> getDocumentByName(String name) {
        return Optional.ofNullable(CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("name", name)).first());
    }

    public Optional<Document> getDocumentByUuid(UUID uuid) {
        return Optional.ofNullable(CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("uuid", uuid.toString())).first());
    }

    public CompletableFuture<Document> findOrMake(String playerName, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first();

            if (document == null) {
                final PotPlayer potPlayer = new PotPlayer(uuid, playerName, null);
                potPlayer.savePlayerData();

                return potPlayer.getDocument(true);
            } else {
                return document;
            }
        });
    }

    public void handleGrant(Grant grant, Document document, Player issuer, String issuedServer, boolean raw) {
        final PotPlayer targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.getString("name"));
        final String rankName = Color.translate(grant.getRank().getColor() + grant.getRank().getItalic() + grant.getRank().getName());

        grant.setIssuedServer(issuedServer);

        if (targetPotPlayer != null) {
            targetPotPlayer.getAllGrants().add(grant);
            targetPotPlayer.setupPlayer();
            targetPotPlayer.saveWithoutRemove();

            targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your rank has been set to " + rankName + ChatColor.GREEN + ".");
        } else {
            final List<Grant> allGrants = new ArrayList<>();

            if (document.getList("allGrants", String.class) == null) {
                document.getList("allGrants", String.class)
                        .forEach(string -> allGrants.add(CorePlugin.GSON.fromJson(string, Grant.class)));
            }

            allGrants.add(grant);

            final List<String> grantStrings = new ArrayList<>();
            allGrants.forEach(json -> grantStrings.add(json.toJson()));

            final List<Grant> grants = document.getList("allGrants", String.class).stream()
                    .map(string -> CorePlugin.GSON.fromJson(string, Grant.class))
                    .filter(Objects::nonNull).collect(Collectors.toList());

            document.replace("allGrants", grantStrings);
            document.replace("rankName", GrantUtil.getProminentGrant(grants).getRank().getName());

            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", document.getString("uuid")), document, new ReplaceOptions().upsert(true)));
        }

        if (issuer != null && !raw) {
            issuer.sendMessage(Color.SECONDARY_COLOR + "You've granted " + (targetPotPlayer != null ? targetPotPlayer.getPlayer().getDisplayName() : Color.MAIN_COLOR + document.getString("name")) + Color.SECONDARY_COLOR + " the rank " + rankName + Color.SECONDARY_COLOR + " for " + Color.MAIN_COLOR + grant.getReason() + Color.SECONDARY_COLOR + ".");
            issuer.sendMessage(Color.SECONDARY_COLOR + "Granted for scopes: " + Color.MAIN_COLOR + grant.getScope() + Color.SECONDARY_COLOR + ".");
            issuer.sendMessage(Color.SECONDARY_COLOR + "The grant will expire in " + Color.MAIN_COLOR + (grant.isPermanent() ? ChatColor.DARK_RED + "Never" : DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true) + " (" + CorePlugin.FORMAT.format(new Date(System.currentTimeMillis() + grant.getDuration())) + ")"));
        } else if (issuer == null && !raw) {
            Bukkit.getConsoleSender().sendMessage(Color.SECONDARY_COLOR + "You've granted " + (targetPotPlayer != null ? targetPotPlayer.getPlayer().getDisplayName() : Color.MAIN_COLOR + document.getString("name")) + Color.SECONDARY_COLOR + " the rank " + grant.getRank().getColor() + grant.getRank().getItalic() + grant.getRank().getName() + Color.SECONDARY_COLOR + " for " + Color.MAIN_COLOR + grant.getReason() + Color.SECONDARY_COLOR + ".");
            Bukkit.getConsoleSender().sendMessage(Color.SECONDARY_COLOR + "Granted for scopes: " + Color.MAIN_COLOR + grant.getScope() + Color.SECONDARY_COLOR + ".");
            Bukkit.getConsoleSender().sendMessage(Color.SECONDARY_COLOR + "The grant will expire in " + Color.MAIN_COLOR + (grant.isPermanent() ? ChatColor.DARK_RED + "Never" : DurationFormatUtils.formatDurationWords(grant.getDuration(), true, true) + " (" + CorePlugin.FORMAT.format(new Date(System.currentTimeMillis() + grant.getDuration())) + ")"));
        }
    }

    public String formatChatChannel(ChatChannelType chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&3[" + fromServer + "] " + player + "&f: &b") + message;
    }

    public void sendDisconnectFreezeMessage(Player target) {
        this.sendToNetworkStaff("&b[S] &4[✘] &3[" + CorePlugin.getInstance().getServerName() + "] &b" + target.getDisplayName() + " &chas logged out while frozen.");
    }

    public void sendToNetworkStaff(String... strings) {
        for (String string : strings) {
            RedisUtil.publishAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate(string), "scandium.staff"));
        }
    }

    public void sendToNetworkStaffFormatted(String s) {
        RedisUtil.publishAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&b[S] &3[" + CorePlugin.getInstance().getServerName() + "] " + s), "scandium.staff"));
    }

    public void sendFreezeMessage(Player player) {
        this.freezeMessage.forEach(s -> player.sendMessage(Color.translate(s)));
    }

    public NetworkPlayer getNetworkPlayer(Player player) {
        return this.allNetworkProfiles.stream()
                .filter(networkPlayer -> networkPlayer.getName().equalsIgnoreCase(player.getName()))
                .findFirst().orElse(null);
    }

    public PotPlayer getPlayer(Player player) {
        return this.allProfiles.getOrDefault(player.getUniqueId(), null);
    }

    public PotPlayer getPlayer(UUID uuid) {
        return this.allProfiles.getOrDefault(uuid, null);
    }

    public NetworkPlayer getNetworkPlayer(String player) {
        return this.allNetworkProfiles.stream()
                .filter(networkPlayer -> networkPlayer.getName().equalsIgnoreCase(player))
                .findFirst().orElse(null);
    }

    public NetworkPlayer getNetworkPlayer(UUID player) {
        return this.allNetworkProfiles.stream()
                .filter(networkPlayer -> networkPlayer.getUuid().equals(player))
                .findFirst().orElse(null);
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

    public String formatBroadcast(String message) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Alert" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + message;
    }
}
