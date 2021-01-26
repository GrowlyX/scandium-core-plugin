package vip.potclub.core.listener;

import com.solexgames.perms.grant.Grant;
import com.solexgames.perms.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.menu.IMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
            event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&cThe server is currently in maintenance."));
        } else {
            int boundOfThree = CorePlugin.RANDOM.nextInt(3);
            if (boundOfThree == 1) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Join our discord via https://discord.gg/D5svAj23R4/!"));
            } else if (boundOfThree == 2) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Follow our twitter for giveaways and more! @PotClubVIP"));
            } else {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7We have a new website! Check it out at https://potclub.vip/!"));
            }
        }
    }

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
            if (!CorePlugin.getInstance().getConfig().getStringList("whitelisted").contains(event.getName())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', CorePlugin.getInstance().getConfig().getString("whitelisted-msg").replace("%NL%", "\n")));
            } else {
                event.allow();
            }
        } else {
            event.allow();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Profile profile = new Profile(event.getPlayer().getUniqueId(), new ArrayList<>(), new ArrayList<>());
        if (!profile.isLoaded()) profile.asyncLoad();
        profile.setupAttachment();

        new PotPlayer(event.getPlayer().getUniqueId());

        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.getTarget().equals(event.getPlayer().getUniqueId())) {
                if ((punishment.getPunishmentType().equals(PunishmentType.BAN)) || (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) || (punishment.getPunishmentType().equals(PunishmentType.IPBAN))) {
                    if (punishment.isActive() || !punishment.isRemoved()) {
                        switch (punishment.getPunishmentType()) {
                            case BLACKLIST:
                                event.getPlayer().kickPlayer(Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                                break;
                            case BAN:
                                event.getPlayer().kickPlayer((punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                                break;
                        }
                    }
                }
            }
        });

        if (event.getPlayer().hasPermission("scandium.staff")) {
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer())));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!PotPlayer.getPlayer(player).isCurrentlyMuted()) {
                event.setCancelled(true);
                if (event.getMessage().startsWith("!") && event.getPlayer().hasPermission(ChatChannelType.STAFF.getPermission())) {
                    event.setCancelled(true);
                    CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("!", ""), event.getPlayer())));
                } else if (event.getMessage().startsWith("#") && event.getPlayer().hasPermission(ChatChannelType.ADMIN.getPermission())) {
                    event.setCancelled(true);
                    CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("#", ""), event.getPlayer())));
                } else if (event.getMessage().startsWith("$") && event.getPlayer().hasPermission(ChatChannelType.DEV.getPermission())) {
                    event.setCancelled(true);
                    CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$", ""), event.getPlayer())));
                } else if (event.getMessage().startsWith("@") && event.getPlayer().hasPermission(ChatChannelType.HOST.getPermission())) {
                    event.setCancelled(true);
                    CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@", ""), event.getPlayer())));
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player1 -> {
                        PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                        if (potPlayer.isCanSeeGlobalChat()) {
                            player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                        }
                    }));
                }
            } else {
                if (player.hasPermission("scandium.chat.bypass")) {
                    event.setCancelled(true);
                    if (event.getMessage().startsWith("!") && event.getPlayer().hasPermission(ChatChannelType.STAFF.getPermission())) {
                        event.setCancelled(true);
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("!", ""), event.getPlayer())));
                    } else if (event.getMessage().startsWith("#") && event.getPlayer().hasPermission(ChatChannelType.ADMIN.getPermission())) {
                        event.setCancelled(true);
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("#", ""), event.getPlayer())));
                    } else if (event.getMessage().startsWith("$") && event.getPlayer().hasPermission(ChatChannelType.DEV.getPermission())) {
                        event.setCancelled(true);
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$", ""), event.getPlayer())));
                    } else if (event.getMessage().startsWith("@") && event.getPlayer().hasPermission(ChatChannelType.HOST.getPermission())) {
                        event.setCancelled(true);
                        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@", ""), event.getPlayer())));
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player1 -> {
                            PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                            if (potPlayer.isCanSeeGlobalChat()) {
                                player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                            }
                        }));
                    }
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
                }
            }
        } else {
            event.setCancelled(true);
            switch (PotPlayer.getPlayer(player).getLanguage()) {
                case ENGLISH:
                    player.sendMessage(Color.translate("&cThe chat is currently muted. Please try chatting again later."));
                    break;
                case FRENCH:
                    player.sendMessage(Color.translate("&cLe chat est actuellement désactivé. Veuillez réessayer plus tard."));
                    break;
                case ITALIAN:
                    player.sendMessage(Color.translate("&cLa chat è attualmente disattivata. Prova a chattare di nuovo più tardi."));
                    break;
                case GERMAN:
                    player.sendMessage(Color.translate("&cDer Chat ist derzeit stummgeschaltet. Bitte versuchen Sie es später noch einmal."));
                    break;
                case SPANISH:
                    player.sendMessage(Color.translate("&cEl chat está silenciado actualmente. Intenta chatear de nuevo más tarde."));
                    break;
            }
        }

        player.setDisplayName(player.getName());
        if (profile != null) {
            Grant grant = profile.getActiveGrant();
            if (!player.getDisplayName().equals(Color.translate(grant.getRank().getData().getColorPrefix() + player.getName()))) {
                player.setDisplayName(Color.translate(grant.getRank().getData().getColorPrefix() + player.getName()));
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("scandium.staff")) {
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));
        }

        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Profile.getProfiles().remove(profile);
        profile.save();
        PotPlayer.getPlayer(event.getPlayer().getUniqueId()).savePlayerData();
    }
}
