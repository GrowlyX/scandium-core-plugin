package com.solexgames.core;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.core.command.extend.CoreCommand;
import com.solexgames.core.command.extend.anticheat.AnticheatBanCommand;
import com.solexgames.core.command.extend.discord.SyncCommand;
import com.solexgames.core.command.extend.discord.UnsyncCommand;
import com.solexgames.core.command.extend.essential.*;
import com.solexgames.core.command.extend.grant.CGrantCommand;
import com.solexgames.core.command.extend.grant.GrantCommand;
import com.solexgames.core.command.extend.grant.GrantsCommand;
import com.solexgames.core.command.extend.moderation.FreezeCommand;
import com.solexgames.core.command.extend.moderation.StaffAnnounceCommand;
import com.solexgames.core.command.extend.moderation.StaffModeCommand;
import com.solexgames.core.command.extend.moderation.VanishCommand;
import com.solexgames.core.command.extend.network.ForceUpdateCommand;
import com.solexgames.core.command.extend.network.NetworkCommand;
import com.solexgames.core.command.extend.prefix.PrefixCommand;
import com.solexgames.core.command.extend.punish.*;
import com.solexgames.core.command.extend.rank.RankCommand;
import com.solexgames.core.command.extend.rank.RankImportCommand;
import com.solexgames.core.command.extend.server.SetSlotsCommand;
import com.solexgames.core.command.extend.shutdown.ShutdownCommand;
import com.solexgames.core.command.extend.test.TestCommand;
import com.solexgames.core.command.extend.toggle.*;
import com.solexgames.core.command.extend.warps.WarpCommand;
import com.solexgames.core.command.extend.web.WebAnnouncementCommand;
import com.solexgames.core.command.extend.web.WebAnnouncementDeleteCommand;
import com.solexgames.core.command.extend.whitelist.BetaWhitelistCommand;
import com.solexgames.core.command.extend.whitelist.WhitelistCommand;
import com.solexgames.core.database.Database;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.listener.ModSuiteListener;
import com.solexgames.core.listener.PlayerListener;
import com.solexgames.core.lunar.AbstractClientInjector;
import com.solexgames.core.lunar.extend.LunarCommand;
import com.solexgames.core.manager.*;
import com.solexgames.core.nms.AbstractNMSImplementation;
import com.solexgames.core.nms.extend.NMSImplementation_v1_16;
import com.solexgames.core.nms.extend.NMSImplementation_v1_7;
import com.solexgames.core.nms.extend.NMSImplementation_v1_8;
import com.solexgames.core.protocol.AbstractChatInterceptor;
import com.solexgames.core.protocol.extend.ProtocolChatInterceptor;
import com.solexgames.core.redis.RedisManager;
import com.solexgames.core.redis.RedisSettings;
import com.solexgames.core.redis.RedisSubscriptions;
import com.solexgames.core.task.*;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.external.ConfigExternal;
import com.solexgames.core.util.external.pagination.pagination.PaginationListener;
import com.solexgames.core.version.AbstractVersionImplementation;
import com.solexgames.core.version.extend.PingCommand_v1_16;
import com.solexgames.core.version.extend.PingCommand_v1_7;
import com.solexgames.core.version.extend.PingCommand_v1_8;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author GrowlyX
 * @since 3/1/2021
 * <p>
 * Holds instances to anything important.
 */

@Getter
@Setter
public final class CorePlugin extends JavaPlugin {

    public static SimpleDateFormat FORMAT;
    public static Random RANDOM;
    public static String CHAT_FORMAT;

    public static boolean CAN_JOIN = false;
    public static boolean COLOR_ENABLED = true;
    public static boolean NAME_MC_REWARDS = true;
    public static boolean ANTI_CHAT_SPAM = true;
    public static boolean ANTI_CMD_SPAM = true;
    public static boolean STAFF_ALERTS_COMMAND = false;

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

    @Getter
    private static CorePlugin instance;

    private ServerManager serverManager;
    private WarpManager warpManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private ShutdownManager shutdownManager;
    private DiscordManager discordManager;
    private CryptoManager cryptoManager;
    private FilterManager filterManager;
    private PrefixManager prefixManager;
    private PunishmentManager punishmentManager;

    private String serverName;

    private Logger chatLogger;
    private Logger commandLogger;

    private Database coreDatabase;
    private RedisManager redisManager;

    private ConfigExternal ranksConfig;
    private ConfigExternal whitelistConfig;
    private ConfigExternal databaseConfig;
    private ConfigExternal motdConfig;
    private ConfigExternal filterConfig;

    private RedisSubscriptions subscriptions;

    private AbstractChatInterceptor chatInterceptor;
    private AbstractClientInjector lunarCommand;
    private AbstractVersionImplementation versionImplementation;
    private AbstractNMSImplementation NMS;

    private Executor taskThread;
    private Executor redisThread;
    private Executor redisSubThread;
    private Executor mongoThread;

    private boolean debugging;
    private boolean disallow;

    private final TPSUpdateTask tpsRunnable = new TPSUpdateTask();

    @Override
    public void onEnable() {
        instance = this;

        if (!this.getName().equals("Scandium"))
            this.getServer().shutdown();

        FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mma");
        FORMAT.setTimeZone(TimeZone.getTimeZone("EST"));

        RANDOM = new Random();
        GSONBUILDER = new GsonBuilder().setPrettyPrinting();
        GSON = GSONBUILDER.create();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.taskThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);
        this.redisSubThread = Executors.newFixedThreadPool(1);

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        this.ranksConfig = new ConfigExternal("ranks");
        this.databaseConfig = new ConfigExternal("database");
        this.whitelistConfig = new ConfigExternal("whitelist");
        this.motdConfig = new ConfigExternal("motd");
        this.filterConfig = new ConfigExternal("filtered");

        CHAT_FORMAT = this.getConfig().getString("settings.chat-format");
        NAME_MC_REWARDS = this.getConfig().getBoolean("settings.namemc-rewards");

        ANTI_CHAT_SPAM = this.getConfig().getBoolean("settings.anti-chat-spam");
        ANTI_CMD_SPAM = this.getConfig().getBoolean("settings.anti-command-spam");
        STAFF_ALERTS_COMMAND = this.getConfig().getBoolean("settings.staff-command-alerts");

        if (this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) chatInterceptor = new ProtocolChatInterceptor(); else this.getLogger().info("[Protocol] Could not find ProtocolLib! Chat tab block will not work without it!");
        if (this.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) lunarCommand = new LunarCommand(); else this.getLogger().info("[Protocol] Could not find LunarClient-API! The /lunar command will not work without it!");

        if (this.getServer().getVersion().contains("1.7")) {
            this.versionImplementation = new PingCommand_v1_7();
            this.NMS = new NMSImplementation_v1_7();
        } else if (this.getServer().getVersion().contains("1.8")) {
            this.versionImplementation = new PingCommand_v1_8();
            this.NMS = new NMSImplementation_v1_8();
        } else if (this.getServer().getVersion().contains("1.16")) {
            this.versionImplementation = new PingCommand_v1_16();
            this.NMS = new NMSImplementation_v1_16();
        }

        this.getLogger().info("[Bukkit] Hooked into Bukkit version " + this.getServer().getVersion() + "!");

        this.serverName = this.getConfig().getString("server-id");
        this.debugging = false;
        this.disallow = false;

        this.subscriptions = new RedisSubscriptions();

        this.coreDatabase = new Database();
        this.redisManager = new RedisManager(new RedisSettings(
                this.databaseConfig.getString("redis.host"),
                this.databaseConfig.getInt("redis.port"),
                this.databaseConfig.getBoolean("redis.authentication.enabled"),
                this.databaseConfig.getString("redis.authentication.password")
        ));

        this.cryptoManager = new CryptoManager();
        this.serverManager = new ServerManager();
        this.rankManager = new RankManager();
        this.prefixManager = new PrefixManager();
        this.punishmentManager = new PunishmentManager();
        this.playerManager = new PlayerManager();
        this.discordManager = new DiscordManager();
        this.filterManager = new FilterManager();
        this.warpManager = new WarpManager();
        this.shutdownManager = new ShutdownManager();

        this.setupExtra();

        this.getRedisThread().execute(() -> this.getRedisManager().write(RedisUtil.onServerOnline()));
        Bukkit.getScheduler().runTaskLater(this, () -> CAN_JOIN = true, 5 * 20L);
    }

    public void setupExtra() {
        this.getCommand("staffchat").setExecutor(new StaffChatCommand());
        this.getCommand("adminchat").setExecutor(new AdminChatCommand());
        this.getCommand("devchat").setExecutor(new DevChatCommand());
        this.getCommand("hostchat").setExecutor(new HostChatCommand());
        this.getCommand("helpop").setExecutor(new HelpOpCommand());
        this.getCommand("broadcast").setExecutor(new BroadcastCommand());
        this.getCommand("kill").setExecutor(new KillCommand());
        this.getCommand("feed").setExecutor(new FeedCommand());
        this.getCommand("heal").setExecutor(new HealCommand());
        this.getCommand("tppos").setExecutor(new TpPosCommand());
        this.getCommand("sudo").setExecutor(new SudoCommand());
        this.getCommand("sudoall").setExecutor(new SudoAllCommand());
        this.getCommand("tphere").setExecutor(new TpHereCommand());
        this.getCommand("gmc").setExecutor(new GmcCommand());
        this.getCommand("gms").setExecutor(new GmsCommand());
        this.getCommand("gmsp").setExecutor(new GmspCommand());
        this.getCommand("staffannounce").setExecutor(new StaffAnnounceCommand());
        this.getCommand("tp").setExecutor(new TpCommand());
        this.getCommand("report").setExecutor(new ReportCommand());
        this.getCommand("punish").setExecutor(new PunishCommand());
        this.getCommand("shutdown").setExecutor(new ShutdownCommand());
        this.getCommand("freeze").setExecutor(new FreezeCommand());
        this.getCommand("ignore").setExecutor(new IgnoreCommand());
        this.getCommand("rank").setExecutor(new RankCommand());
        this.getCommand("media").setExecutor(new MediaCommand());
        this.getCommand("discord").setExecutor(new DiscordCommand());
        this.getCommand("import").setExecutor(new RankImportCommand());
        this.getCommand("options").setExecutor(new OptionsCommand());
        this.getCommand("warp").setExecutor(new WarpCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());
        this.getCommand("twitter").setExecutor(new TwitterCommand());
        this.getCommand("website").setExecutor(new WebsiteCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
        this.getCommand("unban").setExecutor(new UnBanCommand());
        this.getCommand("forceupdate").setExecutor(new ForceUpdateCommand());
        this.getCommand("network").setExecutor(new NetworkCommand());
        this.getCommand("unblacklist").setExecutor(new UnBlacklistCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
        this.getCommand("kickall").setExecutor(new KickAllCommand());
        this.getCommand("acban").setExecutor(new AnticheatBanCommand());
        this.getCommand("store").setExecutor(new StoreCommand());
        this.getCommand("sync").setExecutor(new SyncCommand());
        this.getCommand("unsync").setExecutor(new UnsyncCommand());
        this.getCommand("reply").setExecutor(new ReplyCommand());
        this.getCommand("grant").setExecutor(new GrantCommand());
        this.getCommand("cgrant").setExecutor(new CGrantCommand());
        this.getCommand("prefix").setExecutor(new PrefixCommand());
        this.getCommand("grants").setExecutor(new GrantsCommand());
        this.getCommand("setslots").setExecutor(new SetSlotsCommand());
        this.getCommand("clear").setExecutor(new ClearCommand());
        this.getCommand("grants").setExecutor(new GrantsCommand());
        this.getCommand("vanish").setExecutor(new VanishCommand());
        this.getCommand("modmode").setExecutor(new StaffModeCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("slowchat").setExecutor(new SlowChatCommand());
        this.getCommand("managerchat").setExecutor(new ManagerChatCommand());
        this.getCommand("mutechat").setExecutor(new MuteChatCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());
        this.getCommand("user").setExecutor(new UserCommand());
        this.getCommand("language").setExecutor(new LanguageCommand());
        this.getCommand("whitelist").setExecutor(new WhitelistCommand());
        this.getCommand("betawl").setExecutor(new BetaWhitelistCommand());

        if (this.getServerManager().getNetwork().equals(ServerType.POTCLUBVIP)) {
            this.getCommand("webannouncementdelete").setExecutor(new WebAnnouncementDeleteCommand());
            this.getCommand("webannouncement").setExecutor(new WebAnnouncementCommand());

            this.getCommand("famous").setExecutor(new FamousCommand());
            this.getCommand("profile").setExecutor(new ProfileCommand());
        }

        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("toggletips").setExecutor(new ToggleTipsCommand());
        this.getCommand("togglestaffmessages").setExecutor(new ToggleStaffMessagesCommand());
        this.getCommand("toggleautomodmode").setExecutor(new ToggleAutoModModeCommand());
        this.getCommand("toggleautovanish").setExecutor(new ToggleAutoVanishCommand());
        this.getCommand("togglefilteredmessages").setExecutor(new ToggleFilteredMessagesCommand());

        MessageCommand messageCommand = new MessageCommand();
        this.getCommand("message").setExecutor(messageCommand);
        this.getCommand("message").setTabCompleter(messageCommand);

        ListCommand listCommand = new ListCommand();
        this.getCommand("list").setExecutor(listCommand);
        this.getCommand("list").setTabCompleter(listCommand);

        if (this.lunarCommand != null) this.getCommand("lunar").setExecutor(lunarCommand);
        if (this.chatInterceptor != null) this.chatInterceptor.initializePacketInterceptor();
        if (this.versionImplementation != null) this.getCommand("ping").setExecutor(versionImplementation);

        this.registerListeners(
                new PlayerListener(),
                new PaginationListener(),
                new ModSuiteListener()
        );

        if (this.getConfig().getBoolean("settings.color-gui")) this.getCommand("color").setExecutor(new ColorCommand());
        if (this.getConfig().getBoolean("tips.enabled")) new AutoMessageTask();

        new PunishExpireTask();
        new GrantExpireTask();
        new PlayerSaveTask();
        new ServerUpdateTask();
        new PunishSaveTask();
        new FrozenMessageTask();
        new BoardUpdateTask();

        this.registerBukkitCommand();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.tpsRunnable, 0L, 1L);
    }

    private void registerBukkitCommand() {
        // Thanks to ItsSteve for the general concept of using the commandMap to register commands without using the plugin.yml
        // Source: https://www.spigotmc.org/threads/small-easy-register-command-without-plugin-yml.38036/
        if (this.getServer().getPluginManager() instanceof SimplePluginManager) {
            CommandMap commandMap = null;

            try {
                Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(this.getServer().getPluginManager());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (commandMap != null) {
                commandMap.register(getConfig().getString("core-settings.command-name"), new CoreCommand(getConfig().getString("core-settings.command-name")));
            } else {
                this.getServer().getPluginManager().disablePlugin(this);
                this.getLogger().warning("Your server software's PluginManager does not contain a commandMap so I cannot register a command. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
            }
        } else {
            this.getLogger().warning("Your server software is running a PluginManager that is unrecognized. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
        }
    }

    @Override
    public void onDisable() {
        CAN_JOIN = false;

        this.getWhitelistConfig().getConfiguration().set("whitelisted", this.getServerManager().getWhitelistedPlayers());
        this.getWhitelistConfig().getConfiguration().set("beta-whitelisted", this.getServerManager().getBetaWhitelistedPlayers());

        this.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting.\n&cPlease reconnect in a few minutes, or check discord for more information.")));

        this.punishmentManager.savePunishments();
        this.rankManager.saveRanks();

        RedisUtil.write(RedisUtil.updateRanks());

        this.warpManager.saveWarps();
        this.prefixManager.savePrefixes();

        RedisUtil.write(RedisUtil.onServerOffline());

        if (this.redisManager.isActive()) this.redisManager.unsubscribe();
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }
}
