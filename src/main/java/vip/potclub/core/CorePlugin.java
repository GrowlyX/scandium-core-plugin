package vip.potclub.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.command.extend.CoreCommand;
import vip.potclub.core.command.extend.anticheat.AnticheatBanCommand;
import vip.potclub.core.command.extend.discord.SyncCommand;
import vip.potclub.core.command.extend.discord.UnsyncCommand;
import vip.potclub.core.command.extend.essential.*;
import vip.potclub.core.command.extend.grant.CGrantCommand;
import vip.potclub.core.command.extend.grant.GrantCommand;
import vip.potclub.core.command.extend.grant.GrantsCommand;
import vip.potclub.core.command.extend.modsuite.FreezeCommand;
import vip.potclub.core.command.extend.modsuite.VanishCommand;
import vip.potclub.core.command.extend.network.ForceUpdateCommand;
import vip.potclub.core.command.extend.network.NetworkCommand;
import vip.potclub.core.command.extend.prefix.PrefixCommand;
import vip.potclub.core.command.extend.punish.*;
import vip.potclub.core.command.extend.rank.RankCommand;
import vip.potclub.core.command.extend.rank.RankImportCommand;
import vip.potclub.core.command.extend.server.SetSlotsCommand;
import vip.potclub.core.command.extend.shutdown.ShutdownCommand;
import vip.potclub.core.command.extend.warps.WarpCommand;
import vip.potclub.core.command.extend.web.WebAnnouncementCommand;
import vip.potclub.core.command.extend.web.WebAnnouncementDeleteCommand;
import vip.potclub.core.command.extend.whitelist.WhitelistCommand;
import vip.potclub.core.database.Database;
import vip.potclub.core.listener.PlayerListener;
import vip.potclub.core.lunar.AbstractClientInjector;
import vip.potclub.core.lunar.extend.LunarCommand;
import vip.potclub.core.manager.*;
import vip.potclub.core.nms.AbstractNMSImplementation;
import vip.potclub.core.nms.extend.NMSImplementation_v1_7;
import vip.potclub.core.nms.extend.NMSImplementation_v1_8;
import vip.potclub.core.protocol.AbstractChatInterceptor;
import vip.potclub.core.protocol.extend.ProtocolChatInterceptor;
import vip.potclub.core.redis.RedisClient;
import vip.potclub.core.task.*;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.external.ConfigExternal;
import vip.potclub.core.version.AbstractVersionImplementation;
import vip.potclub.core.version.extend.PingCommand_1_7;
import vip.potclub.core.version.extend.PingCommand_1_8;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

    @Getter
    private static CorePlugin instance;

    private ServerManager serverManager;
    private WarpManager warpManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private ShutdownManager shutdownManager;
    private PrefixManager prefixManager;
    private PunishmentManager punishmentManager;

    private String serverName;

    private Logger chatLogger;
    private Logger commandLogger;

    private Database coreDatabase;
    private RedisClient redisClient;

    private ConfigExternal ranksConfig;
    private ConfigExternal whitelistConfig;
    private ConfigExternal databaseConfig;

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

        CHAT_FORMAT = this.getConfig().getString("settings.chat-format");
        NAME_MC_REWARDS = this.getConfig().getBoolean("settings.namemc-rewards");

        ANTI_CHAT_SPAM = this.getConfig().getBoolean("settings.anti-chat-spam");
        ANTI_CMD_SPAM = this.getConfig().getBoolean("settings.anti-command-spam");

        if (this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) chatInterceptor = new ProtocolChatInterceptor(); else this.getLogger().info("[Protocol] Could not find ProtocolLib! Chat tab block will not work without it!");
        if (this.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) lunarCommand = new LunarCommand(); else this.getLogger().info("[Protocol] Could not find LunarClient-API! The /lunar command will not work without it!");

        if (this.getServer().getVersion().contains("1.7")) {
            this.versionImplementation = new PingCommand_1_7();
            this.NMS = new NMSImplementation_v1_7();

            this.getLogger().info("[Bukkit] Hooked into Bukkit version " + this.getServer().getVersion() + "!");
        } else if (this.getServer().getVersion().contains("1.8")) {
            this.versionImplementation = new PingCommand_1_8();
            this.NMS = new NMSImplementation_v1_8();

            this.getLogger().info("[Bukkit] Hooked into Bukkit version " + this.getServer().getVersion() + "!");
        } else {
            this.getLogger().info("[Bukkit] I don't support this version yet :( Contact growly to add support!");
        }

        this.serverName = this.getConfig().getString("server-id");
        this.debugging = false;
        this.disallow = false;

        this.coreDatabase = new Database();
        this.redisClient = new RedisClient();

        this.serverManager = new ServerManager();
        this.rankManager = new RankManager();
        this.prefixManager = new PrefixManager();
        this.punishmentManager = new PunishmentManager();
        this.playerManager = new PlayerManager();
        this.warpManager = new WarpManager();
        this.shutdownManager = new ShutdownManager();

//        this.setupLogs();
        this.setupExtra();

        this.getRedisThread().execute(() -> this.getRedisClient().write(RedisUtil.onServerOnline()));
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
        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("sudoall").setExecutor(new SudoAllCommand());
        this.getCommand("tphere").setExecutor(new TpHereCommand());
        this.getCommand("gmc").setExecutor(new GmcCommand());
        this.getCommand("gms").setExecutor(new GmsCommand());
        this.getCommand("tp").setExecutor(new TpCommand());
        this.getCommand("report").setExecutor(new ReportCommand());
        this.getCommand("punish").setExecutor(new PunishCommand());
        this.getCommand("scandium").setExecutor(new CoreCommand());
        this.getCommand("shutdown").setExecutor(new ShutdownCommand());
        this.getCommand("freeze").setExecutor(new FreezeCommand());
        this.getCommand("famous").setExecutor(new FamousCommand());
        this.getCommand("profile").setExecutor(new ProfileCommand());
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
        this.getCommand("message").setExecutor(new MessageCommand());
        this.getCommand("reply").setExecutor(new ReplyCommand());
        this.getCommand("grant").setExecutor(new GrantCommand());
        this.getCommand("cgrant").setExecutor(new CGrantCommand());
        this.getCommand("prefix").setExecutor(new PrefixCommand());
        this.getCommand("grants").setExecutor(new GrantsCommand());
        this.getCommand("setslots").setExecutor(new SetSlotsCommand());
        this.getCommand("clear").setExecutor(new ClearCommand());
        this.getCommand("grants").setExecutor(new GrantsCommand());
        this.getCommand("vanish").setExecutor(new VanishCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("slowchat").setExecutor(new SlowChatCommand());
        this.getCommand("mutechat").setExecutor(new MuteChatCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());
        this.getCommand("language").setExecutor(new LanguageCommand());
        this.getCommand("whitelist").setExecutor(new WhitelistCommand());

        this.getCommand("webannouncementdelete").setExecutor(new WebAnnouncementDeleteCommand());
        this.getCommand("webannouncement").setExecutor(new WebAnnouncementCommand());

        this.getCommand("toggletips").setExecutor(new ToggleTipsCommand());
        this.getCommand("togglestaffmessages").setExecutor(new ToggleStaffMessagesCommand());

        if (this.lunarCommand != null) this.getCommand("lunar").setExecutor(lunarCommand);
        if (this.chatInterceptor != null) this.chatInterceptor.initializePacketInterceptor();
        if (this.versionImplementation != null) this.getCommand("ping").setExecutor(versionImplementation);

        this.registerListeners(new PlayerListener());

        if (this.getConfig().getBoolean("settings.color-gui")) this.getCommand("color").setExecutor(new ColorCommand());
        if (this.getConfig().getBoolean("tips.enabled")) new AutoMessageTask();

        new PunishExpireTask();
        new GrantExpireTask();
        new PlayerSaveTask();
        new ServerUpdateTask();
        new PunishSaveTask();
        new FrozenMessageTask();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.tpsRunnable, 0L, 1L);
    }

    public void setupLogs() {
        this.chatLogger = Logger.getLogger("Chat");
        String date = FORMAT.format(new Date()).replace(" ", "_").replace("/", "_");

        FileHandler fileHandler;
        File file;

        file = new File("SolexGames\\Scandium\\Chat_Logs\\chat_log_" + date + ".log");
        if (!file.getParentFile().exists()) file.getParentFile().mkdir();

        try {
            fileHandler = new FileHandler("SolexGames\\Scandium\\Chat_Logs\\chat_log_" + date + ".log");
            this.chatLogger.addHandler(fileHandler);

            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.commandLogger = Logger.getLogger("Commands");

        FileHandler commandFileHandler;
        File otherFile;

        otherFile = new File("SolexGames\\Scandium\\Command_Logs\\command_log_" + date + ".log");
        if (!otherFile.getParentFile().exists()) otherFile.getParentFile().mkdir();

        try {
            commandFileHandler = new FileHandler("SolexGames\\Scandium\\Command_Logs\\command_log_" + date + ".log");
            this.commandLogger.addHandler(commandFileHandler);

            SimpleFormatter formatter = new SimpleFormatter();
            commandFileHandler.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        CAN_JOIN = false;

        this.getWhitelistConfig().getConfiguration().set("whitelisted", this.getServerManager().getWhitelistedPlayers());
        this.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting.\n&cPlease reconnect in a few minutes, or check discord for more information.")));

        this.punishmentManager.savePunishments();
        this.rankManager.saveRanks();

        RedisUtil.write(RedisUtil.updateRanks());

        this.warpManager.saveWarps();
        this.prefixManager.savePrefixes();

        RedisUtil.write(RedisUtil.onServerOffline());

        if (this.redisClient.isClientActive()) this.redisClient.destroyClient();
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }
}
