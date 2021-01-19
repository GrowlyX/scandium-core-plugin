package vip.potclub.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.command.extend.essential.*;
import vip.potclub.core.database.Database;
import vip.potclub.core.listener.PlayerListener;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.manager.PunishmentManager;
import vip.potclub.core.redis.RedisClient;
import vip.potclub.core.task.AutoMessageTask;
import vip.potclub.core.util.Color;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Setter
public final class CorePlugin extends JavaPlugin {

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

    @Getter
    public static CorePlugin instance;

    public PlayerManager playerManager;
    public PunishmentManager punishmentManager;

    public String serverName;

    public Database coreMongoDatabase;
    public RedisClient redisClient;

    public Executor redisThread;
    public Executor redisSubThread;
    public Executor mongoThread;

    @Override
    public void onEnable() {
        instance = this;

        GSONBUILDER = new GsonBuilder();
        GSON = GSONBUILDER.create();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);
        this.redisSubThread = Executors.newFixedThreadPool(1);

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        this.serverName = this.getConfig().getString("server-name");

        this.coreMongoDatabase = new Database();
        this.redisClient = new RedisClient();

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

        this.getCommand("toggletips").setExecutor(new ToggleTipsCommand());
        this.getCommand("togglestaffmessages").setExecutor(new ToggleStaffMessagesCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        new AutoMessageTask();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cThe server is currently rebooting.\n&cPlease reconnect in a few minutes.")));
        this.getServer().getScheduler().cancelAllTasks();
        if (redisClient.isClientActive()) {
            redisClient.destroyClient();
        }
    }
}
