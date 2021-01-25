package vip.potclub.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.Practice;
import vip.potclub.core.command.extend.CoreCommand;
import vip.potclub.core.command.extend.essential.*;
import vip.potclub.core.database.Database;
import vip.potclub.core.listener.PlayerListener;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.manager.PunishmentManager;
import vip.potclub.core.manager.ServerManager;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.redis.RedisClient;
import vip.potclub.core.task.AutoMessageTask;
import vip.potclub.core.task.PunishExpireTask;
import vip.potclub.core.util.Color;
import vip.potclub.profile.Profile;

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
    public static CorePlugin instance;

    public ServerManager serverManager;
    public PlayerManager playerManager;
    public PunishmentManager punishmentManager;

    public String serverName;

    public Database coreDatabase;
    public RedisClient redisClient;

    public Executor redisThread;
    public Executor redisSubThread;
    public Executor mongoThread;

    public boolean debugging;
    public boolean disallow;

    @Override
    public void onEnable() {
        instance = this;

        FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mma");
        FORMAT.setTimeZone(TimeZone.getTimeZone("EST"));

        RANDOM  = new Random();
        GSONBUILDER = new GsonBuilder();
        GSON = GSONBUILDER.create();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);
        this.redisSubThread = Executors.newFixedThreadPool(1);

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        this.serverName = this.getConfig().getString("server-name");
        this.debugging = false;
        this.disallow = false;

        this.coreDatabase = new Database();
        this.redisClient = new RedisClient();

        this.serverManager = new ServerManager();
        this.playerManager = new PlayerManager();
        this.punishmentManager = new PunishmentManager();

        this.setupExtra();
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
        this.getCommand("sudoall").setExecutor(new SudoAllCommand());
        this.getCommand("tphere").setExecutor(new TpHereCommand());
        this.getCommand("gmc").setExecutor(new GmcCommand());
        this.getCommand("gms").setExecutor(new GmsCommand());
        this.getCommand("tp").setExecutor(new TpCommand());
        this.getCommand("report").setExecutor(new ReportCommand());
        this.getCommand("punish").setExecutor(new PunishCommand());
        this.getCommand("scandium").setExecutor(new CoreCommand());
        this.getCommand("discord").setExecutor(new DiscordCommand());
        this.getCommand("options").setExecutor(new OptionsCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());
        this.getCommand("twitter").setExecutor(new TwitterCommand());
        this.getCommand("website").setExecutor(new WebsiteCommand());
        this.getCommand("unmute").setExecutor(new UnMuteCommand());
        this.getCommand("unban").setExecutor(new UnBanCommand());
        this.getCommand("store").setExecutor(new StoreCommand());
        this.getCommand("message").setExecutor(new MessageCommand());
        this.getCommand("reply").setExecutor(new ReplyCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("mutechat").setExecutor(new MuteChatCommand());
        this.getCommand("language").setExecutor(new LanguageCommand());

        this.getCommand("toggletips").setExecutor(new ToggleTipsCommand());
        this.getCommand("togglestaffmessages").setExecutor(new ToggleStaffMessagesCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        new AutoMessageTask();
        new PunishExpireTask();
        new BukkitRunnable() {
            @Override
            public void run() {
                PotPlayer.profilePlayers.forEach((uuid, potPlayer) -> potPlayer.saveWithoutRemove());
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 36000L, 36000L);
    }

    @Override
    public void onDisable() {
        this.punishmentManager.savePunishments();
        this.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting.\n&cPlease reconnect in a few minutes, or check discord for more information.")));
        this.getServer().getScheduler().cancelAllTasks();
        if (this.redisClient.isClientActive()) {
            this.redisClient.destroyClient();
        }
    }
}
