package vip.potclub.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.command.extend.CoreCommand;
import vip.potclub.core.command.extend.discord.SyncCommand;
import vip.potclub.core.command.extend.discord.UnsyncCommand;
import vip.potclub.core.command.extend.essential.*;
import vip.potclub.core.command.extend.grant.CGrantCommand;
import vip.potclub.core.command.extend.grant.GrantCommand;
import vip.potclub.core.command.extend.grant.GrantsCommand;
import vip.potclub.core.command.extend.network.ForceUpdateCommand;
import vip.potclub.core.command.extend.network.NetworkCommand;
import vip.potclub.core.command.extend.prefix.PrefixCommand;
import vip.potclub.core.command.extend.punish.UnBlacklistCommand;
import vip.potclub.core.command.extend.punish.UnMuteCommand;
import vip.potclub.core.command.extend.punish.PunishCommand;
import vip.potclub.core.command.extend.punish.UnBanCommand;
import vip.potclub.core.command.extend.rank.RankImportCommand;
import vip.potclub.core.command.extend.server.SetSlotsCommand;
import vip.potclub.core.command.extend.web.WebAnnouncementCommand;
import vip.potclub.core.command.extend.web.WebAnnouncementDeleteCommand;
import vip.potclub.core.database.Database;
import vip.potclub.core.listener.PlayerListener;
import vip.potclub.core.manager.*;
import vip.potclub.core.redis.RedisClient;
import vip.potclub.core.task.*;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.external.ConfigExternal;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Setter
public final class CorePlugin extends JavaPlugin {

    public static SimpleDateFormat FORMAT;
    public static Random RANDOM;

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

    @Getter
    private static CorePlugin instance;

    private ServerManager serverManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private PrefixManager prefixManager;
    private PunishmentManager punishmentManager;

    private String serverName;

    private Database coreDatabase;
    private RedisClient redisClient;
    private ConfigExternal ranksConfig;

    private Executor taskThread;
    private Executor redisThread;
    private Executor redisSubThread;
    private Executor mongoThread;

    private boolean debugging;
    private boolean disallow;

    @Override
    public void onEnable() {
        instance = this;

        FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mma");
        FORMAT.setTimeZone(TimeZone.getTimeZone("EST"));

        RANDOM  = new Random();
        GSONBUILDER = new GsonBuilder().setPrettyPrinting();
        GSON = GSONBUILDER.create();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.taskThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);
        this.redisSubThread = Executors.newFixedThreadPool(1);

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();
        this.ranksConfig = new ConfigExternal("ranks");

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

        this.setupExtra();

        this.getRedisThread().execute(() -> this.getRedisClient().write(RedisUtil.onServerOnline()));
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
        this.getCommand("ping").setExecutor(new PingCommand());
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
        this.getCommand("famous").setExecutor(new FamousCommand());
        this.getCommand("profile").setExecutor(new ProfileCommand());
        this.getCommand("media").setExecutor(new MediaCommand());
        this.getCommand("discord").setExecutor(new DiscordCommand());
        this.getCommand("import").setExecutor(new RankImportCommand());
        this.getCommand("options").setExecutor(new OptionsCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());
        this.getCommand("twitter").setExecutor(new TwitterCommand());
        this.getCommand("website").setExecutor(new WebsiteCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
        this.getCommand("unban").setExecutor(new UnBanCommand());
        this.getCommand("forceupdate").setExecutor(new ForceUpdateCommand());
        this.getCommand("network").setExecutor(new NetworkCommand());
        this.getCommand("color").setExecutor(new ColorCommand());
        this.getCommand("unblacklist").setExecutor(new UnBlacklistCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
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
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("slowchat").setExecutor(new SlowChatCommand());
        this.getCommand("mutechat").setExecutor(new MuteChatCommand());
        this.getCommand("language").setExecutor(new LanguageCommand());
        this.getCommand("whitelist").setExecutor(new WhitelistCommand());

        this.getCommand("webannouncementdelete").setExecutor(new WebAnnouncementDeleteCommand());
        this.getCommand("webannouncement").setExecutor(new WebAnnouncementCommand());

        this.getCommand("toggletips").setExecutor(new ToggleTipsCommand());
        this.getCommand("togglestaffmessages").setExecutor(new ToggleStaffMessagesCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        new AutoMessageTask();
        new PunishExpireTask();
        new GrantExpireTask();
        new PlayerSaveTask();
        new ServerUpdateTask();
    }

    @Override
    public void onDisable() {
        this.getRedisThread().execute(() -> this.getRedisClient().write(RedisUtil.onServerOffline()));

        this.punishmentManager.savePunishments();
        this.rankManager.saveRanks();
        this.prefixManager.savePrefixes();

        this.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting.\n&cPlease reconnect in a few minutes, or check discord for more information.")));
        this.getServer().getScheduler().cancelAllTasks();

        if (this.redisClient.isClientActive()) {
            this.redisClient.destroyClient();
        }
    }
}
