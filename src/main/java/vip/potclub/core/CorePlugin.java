package vip.potclub.core;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.command.extend.essential.AdminChatCommand;
import vip.potclub.core.command.extend.essential.HelpOpCommand;
import vip.potclub.core.command.extend.essential.StaffChatCommand;
import vip.potclub.core.database.Database;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.redis.RedisClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter @Setter
public final class CorePlugin extends JavaPlugin implements Listener {

    @Getter
    public static CorePlugin instance;

    public PlayerManager playerManager;

    public Database database;
    public RedisClient redisClient;

    public Executor redisThread;
    public Executor mongoThread;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        if (!CorePlugin.getInstance().getServer().getPluginManager().isPluginEnabled("Argon")) {
            this.getLogger().severe("Could not find Argon Data Manager.");
            this.getServer().getScheduler().cancelAllTasks();
            this.getServer().shutdown();
        }

        this.database = new Database();
        this.redisClient = new RedisClient();
        this.playerManager = new PlayerManager();

        this.setupCommands();

        this.mongoThread = Executors.newFixedThreadPool(1);
        this.redisThread = Executors.newFixedThreadPool(1);
    }

    public void setupCommands() {
        /*
        this.getCommand("sudo").setExecutor(new SudoCommand());
        this.getCommand("kill").setExecutor(new KillCommand());
        this.getCommand("feed").setExecutor(new FeedCommand());
        this.getCommand("heal").setExecutor(new HealCommand());
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
        this.getCommand("helpop").setExecutor(new HelpOpCommand());
    }

    @Override
    public void onDisable() {
    }
}
