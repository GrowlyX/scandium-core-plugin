package vip.potclub.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.command.extend.essential.*;
import vip.potclub.core.command.extend.rank.RankCommand;
import vip.potclub.core.database.Database;
import vip.potclub.core.listener.PlayerListener;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.manager.RankManager;
import vip.potclub.core.redis.RedisClient;

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
    public RankManager rankManager;

    public String serverName;

    public Database coreMongoDatabase;
    public RedisClient redisClient;

    public Executor redisThread;
    public Executor mongoThread;

    @Override
    public void onEnable() {
        instance = this;

        GSONBUILDER = (new GsonBuilder());
        GSON = GSONBUILDER.create();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        this.serverName = this.getConfig().getString("server-name");
        this.coreMongoDatabase = new Database();
        this.redisClient = new RedisClient();
        this.playerManager = new PlayerManager();
        this.rankManager = new RankManager();

        this.setupCommands();
    }

    public void setupCommands() {
        /*
        this.getCommand("sudo").setExecutor(new SudoCommand());

        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("ping").setExecutor(new PingCommand());
        this.getCommand("more").setExecutor(new MoreCommand());
        this.getCommand("time").setExecutor(new TimeCommand());
        this.getCommand("clear").setExecutor(new ClearCommand());
        this.getCommand("helpop").setExecutor(new HelpOPCommand());
        this.getCommand("report").setExecutor(new ReportCommand());
        this.getCommand("sudoall").setExecutor(new SudoAllCommand());
        this.getCommand("killall").setExecutor(new KillAllCommand());
        this.getCommand("onlinestaff").setExecutor(new OnlineStaffCommand());
         */
        this.getCommand("staffchat").setExecutor(new StaffChatCommand());
        this.getCommand("adminchat").setExecutor(new AdminChatCommand());
        this.getCommand("devchat").setExecutor(new DevChatCommand());
        this.getCommand("hostchat").setExecutor(new HostChatCommand());
        this.getCommand("helpop").setExecutor(new HelpOpCommand());
        this.getCommand("broadcast").setExecutor(new BroadcastCommand());
        this.getCommand("kill").setExecutor(new KillCommand());
        this.getCommand("feed").setExecutor(new FeedCommand());
        this.getCommand("heal").setExecutor(new HealCommand());
        this.getCommand("rank").setExecutor(new RankCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        if (redisClient.isClientActive()) {
            redisClient.destroyClient();
        }
    }
}
