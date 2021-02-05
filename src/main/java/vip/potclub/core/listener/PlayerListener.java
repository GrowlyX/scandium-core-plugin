package vip.potclub.core.listener;

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
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.media.MediaConstants;
import vip.potclub.core.menu.IMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.util.regex.Matcher;

public class PlayerListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
            event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&cThe server is currently in maintenance."));
        } else {
            int boundOfThree = CorePlugin.RANDOM.nextInt(3);
            if (boundOfThree == 1) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Join our discord via &bhttps://dsc.gg/pot&7!"));
            } else if (boundOfThree == 2) {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7Follow our twitter for giveaways and more! &b@PotClubVIP&7"));
            } else {
                event.setMotd(Color.translate("&d&lPotClub &7&l\u239c &fEU\n&7We have a new website! Check it out at &bwww.potclub.vip&7!"));
            }
        }
    }

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("whitelist")) {
            if (!CorePlugin.getInstance().getConfig().getStringList("whitelisted").contains(event.getName())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', CorePlugin.getInstance().getConfig().getString("whitelisted-msg").replace("%NL%", "\n")));
            } else {
                checkDisallow(event);
            }
        } else {
            checkDisallow(event);
        }
    }

    private void checkDisallow(AsyncPlayerPreLoginEvent event) {
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.getTarget().equals(event.getUniqueId())) {
                if ((punishment.getPunishmentType().equals(PunishmentType.BAN)) || (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) || (punishment.getPunishmentType().equals(PunishmentType.IPBAN))) {
                    if (punishment.isActive()) {
                        if (!punishment.isRemoved()) {
                            switch (punishment.getPunishmentType()) {
                                case BLACKLIST:
                                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                                    break;
                                case IPBAN:
                                case BAN:
                                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, (punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                                    break;
                            }
                        }
                    }
                }
            }
        });
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
        new PotPlayer(event.getPlayer().getUniqueId());
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().forEach(player -> {
            if (!event.getPlayer().hasPermission("scandium.vanished.see")) {
                event.getPlayer().hidePlayer(player);
            }
        });
        if (event.getPlayer().hasPermission("scandium.staff")) Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer()))), 10L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        Matcher discordMatcher = MediaConstants.DISCORD_USERNAME_REGEX.matcher(event.getMessage());
        Matcher twitterMatcher = MediaConstants.TWITTER_USERNAME_REGEX.matcher(event.getMessage());
        Matcher instaMatcher = MediaConstants.INSTAGRAM_USERNAME_REGEX.matcher(event.getMessage());
        Matcher youtubeMatcher = MediaConstants.YOUTUBE_PROFILELINK_REGEX.matcher(event.getMessage());

        if (potPlayer.getMedia().getMediaData().isModifyingDiscordData()) {
            if (discordMatcher.matches()) {
                potPlayer.getMedia().setDiscord(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your discord to &e" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingDiscordData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid discord username!"));
                player.sendMessage(Color.translate("&cExample: Wumpus#1234"));
            }
            event.setCancelled(true);
            return;
        }

        if (PotPlayer.getPlayer(player).getMedia().getMediaData().isModifyingInstaData()) {
            if (instaMatcher.matches()) {
                potPlayer.getMedia().setInstagram(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your instagram to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingInstaData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid instagram username!"));
                player.sendMessage(Color.translate("&cExample: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (PotPlayer.getPlayer(player).getMedia().getMediaData().isModifyingYoutubeData()) {
            if (youtubeMatcher.matches()) {
                potPlayer.getMedia().setYoutubeLink(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your youtube to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingYoutubeData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid youtube link!"));
                player.sendMessage(Color.translate("&cExample: https://youtube.com/c/SolexGames/"));
            }
            event.setCancelled(true);
            return;
        }

        if (PotPlayer.getPlayer(player).getMedia().getMediaData().isModifyingTwitterData()) {
            if (twitterMatcher.matches()) {
                potPlayer.getMedia().setTwitter(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your twitter to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingTwitterData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid twitter link!"));
                player.sendMessage(Color.translate("&cExample: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!PotPlayer.getPlayer(player).isMuted()) {
                checkChannel(event, player, potPlayer);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
                if (!PotPlayer.getPlayer(player).isCurrentlyMuted()) {
                    checkChannel(event, player, potPlayer);
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
    }

    private void checkChannel(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer) {
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
                    checkThenSend(event, player, potPlayer, slowChat);
                } else {
                    player.sendMessage(slowChat > 0L ? Color.translate(PunishmentStrings.SLOW_CHAT.replace("<amount>", DurationFormatUtils.formatDurationWords(slowChat, true, true))) : Color.translate(PunishmentStrings.COOLDOWN));
                    event.setCancelled(true);
                }
            } else {
                checkThenSend(event, player, potPlayer, slowChat);
            }
        }
    }

    @EventHandler
    public void onBlockedCommand(PlayerCommandPreprocessEvent event) {
        if (CorePlugin.getInstance().getConfig().getBoolean("block-commands.enabled")) {
            if (event.getPlayer().hasPermission("scandium.protocol.bypass")) return;
            for (String blockedCommand : CorePlugin.getInstance().getConfig().getStringList("block-commands.list")) {
                if (event.getMessage().contains("/" + blockedCommand)) {
                    event.getPlayer().sendMessage(Color.translate("&cThat command is blocked."));
                    event.setCancelled(true);
                }
            }
        }
    }

    private void checkThenSend(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer, long slowChat) {
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            PotPlayer potPlayer1 = PotPlayer.getPlayer(player1);
            if (potPlayer1.isCanSeeGlobalChat()) {
                player1.sendMessage(Color.translate((potPlayer.getAppliedPrefix() != null ? potPlayer.getAppliedPrefix().getPrefix() + " " : "") + potPlayer.getActiveGrant().getRank().getPrefix() + potPlayer.getActiveGrant().getRank().getColor() + (potPlayer.getCustomColor() != null ? potPlayer.getCustomColor() : "") + player.getName() + " &7" + '»' + " " + (potPlayer.getActiveGrant().getRank().getName().contains("Default") ? "&7" : "&f") + event.getMessage()));
            }
        });
        PotPlayer.getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("scandium.staff")) CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));
        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(event.getPlayer());
        PotPlayer.getPlayer(event.getPlayer().getUniqueId()).savePlayerData();
    }
}
