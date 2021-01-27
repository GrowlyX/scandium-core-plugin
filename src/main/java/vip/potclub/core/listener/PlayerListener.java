package vip.potclub.core.listener;

import com.solexgames.perms.grant.Grant;
import com.solexgames.perms.grant.procedure.GrantProcedure;
import com.solexgames.perms.profile.Profile;
import org.apache.commons.lang.time.DurationFormatUtils;
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
import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
            event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&cThe server is currently in maintenance."));
        } else {
            int boundOfThree = CorePlugin.RANDOM.nextInt(3);
            if (boundOfThree == 1) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Join our discord via &bdiscord.gg/D5svAj23R4&7!"));
            } else if (boundOfThree == 2) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Follow our twitter for giveaways and more! &b@PotClubVIP&7"));
            } else {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7We have a new website! Check it out at &bwww.potclub.vip&7!"));
            }
        }
    }

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if (!event.getUniqueId().equals(UUID.fromString("2413bb4a-dd69-4810-8949-b614a82c8a38"))) {
            if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
                if (!CorePlugin.getInstance().getConfig().getStringList("whitelisted").contains(event.getName())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', CorePlugin.getInstance().getConfig().getString("whitelisted-msg").replace("%NL%", "\n")));
                } else {
                    Punishment.getAllPunishments().forEach(punishment -> {
                        if (punishment.getTarget().equals(event.getUniqueId())) {
                            if ((punishment.getPunishmentType().equals(PunishmentType.BAN)) || (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) || (punishment.getPunishmentType().equals(PunishmentType.IPBAN))) {
                                if (punishment.isActive() || !punishment.isRemoved()) {
                                    switch (punishment.getPunishmentType()) {
                                        case BLACKLIST:
                                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                                            break;
                                        case BAN:
                                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, (punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                                            break;
                                    }
                                } else {
                                    event.allow();
                                }
                            } else {
                                event.allow();
                            }
                        } else {
                            event.allow();
                        }
                    });
                }
            } else {
                Punishment.getAllPunishments().forEach(punishment -> {
                    if (punishment.getTarget().equals(event.getUniqueId())) {
                        if ((punishment.getPunishmentType().equals(PunishmentType.BAN)) || (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) || (punishment.getPunishmentType().equals(PunishmentType.IPBAN))) {
                            if (punishment.isActive() || !punishment.isRemoved()) {
                                switch (punishment.getPunishmentType()) {
                                    case BLACKLIST:
                                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                                        break;
                                    case BAN:
                                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, (punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                                        break;
                                }
                            } else {
                                event.allow();
                            }
                        } else {
                            event.allow();
                        }
                    } else {
                        event.allow();
                    }
                });
            }
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate("&cYou are not allowed on this server.\n&cConfused? You are currently on our &c&lBlocked List&c.\n&cPlease contact &c&lGrowlyX#1337&c if you think this is false."));
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

        if (event.getPlayer().hasPermission("scandium.staff")) {
            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer()))), 10L);
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
                    long slowChat = CorePlugin.getInstance().getServerManager().getChatSlow();
                    if ((System.currentTimeMillis() < PotPlayer.getPlayer(player).getChatCooldown())) {
                        if (player.hasPermission("scandium.chat.cooldown.bypass")) {
                            Bukkit.getOnlinePlayers().forEach(player1 -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                                if (potPlayer.isCanSeeGlobalChat()) {
                                    player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                                }
                            });
                            PotPlayer.getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
                        } else {
                            player.sendMessage(slowChat > 0L ? Color.translate(PunishmentStrings.SLOW_CHAT.replace("<amount>", DurationFormatUtils.formatDurationWords(slowChat, true, true))) : Color.translate(PunishmentStrings.COOLDOWN));
                            event.setCancelled(true);
                        }
                    } else {
                        Bukkit.getOnlinePlayers().forEach(player1 -> {
                            PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                            if (potPlayer.isCanSeeGlobalChat()) {
                                player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                            }
                        });
                        PotPlayer.getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
                    }
                }
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
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
                        long slowChat = CorePlugin.getInstance().getServerManager().getChatSlow();
                        if ((System.currentTimeMillis() < PotPlayer.getPlayer(player).getChatCooldown())) {
                            if (player.hasPermission("scandium.chat.cooldown.bypass")) {
                                Bukkit.getOnlinePlayers().forEach(player1 -> {
                                    PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                                    if (potPlayer.isCanSeeGlobalChat()) {
                                        player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                                    }
                                });
                                PotPlayer.getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
                            } else {
                                player.sendMessage(slowChat > 0L ? Color.translate(PunishmentStrings.SLOW_CHAT.replace("<amount>", DurationFormatUtils.formatDurationWords(slowChat, true, true))) : Color.translate(PunishmentStrings.COOLDOWN));
                                event.setCancelled(true);
                            }
                        } else {
                            Bukkit.getOnlinePlayers().forEach(player1 -> {
                                PotPlayer potPlayer = PotPlayer.getPlayer(player1);
                                if (potPlayer.isCanSeeGlobalChat()) {
                                    player1.sendMessage(Color.translate(profile.getActiveGrant().getRank().getData().getPrefix() + player.getName() + " &7" + '»' + " " + (profile.getActiveGrant().getRank().getData().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
                                }
                            });
                            PotPlayer.getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
                        }
                    }
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
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
