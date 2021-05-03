package com.solexgames.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.core.adapter.DateTypeAdapter;
import com.solexgames.core.adapter.LocationTypeAdapter;
import com.solexgames.core.adapter.PotionEffectTypeAdapter;
import com.solexgames.core.command.impl.CoreCommand;
import com.solexgames.core.command.impl.other.WebPostCommand;
import com.solexgames.core.database.Database;
import com.solexgames.core.disguise.DisguiseCache;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.hooks.client.IClient;
import com.solexgames.core.hooks.client.impl.LunarClientImpl;
import com.solexgames.core.hooks.nms.INMS;
import com.solexgames.core.hooks.nms.impl.*;
import com.solexgames.core.hooks.protocol.AbstractPacketHandler;
import com.solexgames.core.hooks.protocol.impl.ProtocolPacketHandler;
import com.solexgames.core.manager.*;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.redis.RedisManager;
import com.solexgames.core.redis.RedisSettings;
import com.solexgames.core.redis.RedisSubscriptions;
import com.solexgames.core.serializer.DataLibrary;
import com.solexgames.core.serializer.impl.ItemStackSerializer;
import com.solexgames.core.serializer.impl.LocationSerializer;
import com.solexgames.core.settings.ServerSettings;
import com.solexgames.core.task.*;
import com.solexgames.core.util.BukkitUtil;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.config.FileConfig;
import com.solexgames.core.uuid.UUIDCache;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * @author GrowlyX
 * @since 3/1/2021
 */

@Getter
@Setter
public final class CorePlugin extends JavaPlugin {

    public static SimpleDateFormat FORMAT;
    public static Random RANDOM;
    public static Gson GSON;

    @Getter
    private static CorePlugin instance;

    private ServerManager serverManager;
    private ReportManager reportManager;
    private WarpManager warpManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private ShutdownManager shutdownManager;
    private DiscordManager discordManager;
    private CryptoManager cryptoManager;
    private FilterManager filterManager;
    private PrefixManager prefixManager;
    private PunishmentManager punishmentManager;
    private NameTagManager nameTagManager;
    private DisguiseManager disguiseManager;

    private UUIDCache uuidCache;
    private DisguiseCache disguiseCache;
    private ServerSettings serverSettings;

    private String serverName;
    private HttpClient httpClient;
    private Database coreDatabase;
    private RedisManager redisManager;
    private String pluginName;

    private FileConfig ranksConfig;
    private FileConfig databaseConfig;
    private FileConfig filterConfig;

    private RedisSubscriptions subscriptions;
    private DataLibrary library;

    private AbstractPacketHandler packetHandler;
    private IClient clientHook;
    private INMS NMS;

    private boolean debugging;
    private boolean disallow;

    private final ConversationFactory conversationFactory = new ConversationFactory(this);
    private final TPSUpdateTask tpsRunnable = new TPSUpdateTask();

    @Override
    public void onEnable() {
        instance = this;

        final long milli = System.currentTimeMillis();

        RANDOM = new Random();
        GSON = new GsonBuilder()
                .registerTypeAdapter(PotionEffect.class, new PotionEffectTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        this.httpClient = new DefaultHttpClient();

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults();

        this.pluginName = this.getConfig().getString("core-settings.command-name");

        FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mma");
        FORMAT.setTimeZone(TimeZone.getTimeZone(this.getConfig().getString("settings.time-zone")));

        this.ranksConfig = new FileConfig("ranks");
        this.databaseConfig = new FileConfig("database");
        this.filterConfig = new FileConfig("filtered");

        this.serverSettings = new ServerSettings();

        this.library = new DataLibrary();
        this.library.getDataManager().registerSerializer(new LocationSerializer());
        this.library.getDataManager().registerSerializer(new ItemStackSerializer());

        this.setupSettings();
        this.setupHooks();

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

        this.uuidCache = new UUIDCache();
        this.disguiseCache = new DisguiseCache();

        this.punishmentManager = new PunishmentManager();
        this.cryptoManager = new CryptoManager();
        this.reportManager = new ReportManager();
        this.serverManager = new ServerManager();
        this.rankManager = new RankManager();
        this.prefixManager = new PrefixManager();
        this.playerManager = new PlayerManager();
        this.discordManager = new DiscordManager();
        this.filterManager = new FilterManager();
        this.warpManager = new WarpManager();
        this.shutdownManager = new ShutdownManager();
        this.nameTagManager = new NameTagManager();
        this.disguiseManager = new DisguiseManager();

        this.setupExtra();
        this.logInformation(milli);

        new ServerLoadingTask();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "core:permissions");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "core:update");
    }

    private void setupSettings() {
        this.serverSettings.setTabEnabled(this.getConfig().getBoolean("tablist.enabled"));
        this.serverSettings.setTabHeader(Color.translate(this.getConfig().getString("tablist.header").replace("<nl>", "\n").replace("<server-name>", this.getConfig().getString("server-id"))));
        this.serverSettings.setTabFooter(Color.translate(this.getConfig().getString("tablist.footer").replace("<nl>", "\n").replace("<server-name>", this.getConfig().getString("server-id"))));
        this.serverSettings.setChatFormat(this.getConfig().getString("settings.chat-format"));
        this.serverSettings.setChatFormatEnabled(this.getConfig().getBoolean("settings.chat-format-enabled"));
        this.serverSettings.setNameMcEnabled(this.getConfig().getBoolean("settings.namemc-rewards"));
        this.serverSettings.setAntiSpamEnabled(this.getConfig().getBoolean("settings.anti-chat-spam"));
        this.serverSettings.setAntiCommandSpamEnabled(this.getConfig().getBoolean("settings.anti-command-spam"));
        this.serverSettings.setStaffAlertsEnabled(this.getConfig().getBoolean("settings.staff-command-alerts"));
        this.serverSettings.setAlertFormat(Color.translate(this.getConfig().getString("settings.staff-command-alerts-format")));
    }

    private void setupHooks() {
        if (this.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.packetHandler = new ProtocolPacketHandler();
        } else {
            this.getLogger().info("[Protocol] Could not find ProtocolLib! Chat tab block will not work without it!");
        }

        if (this.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) {
            this.clientHook = new LunarClientImpl();
        } else {
            this.getLogger().info("[Protocol] Could not find LunarClient-API! The /lunar command will not work without it!");
        }

        if (this.getServer().getVersion().contains("1.7")) {
            this.NMS = new NMSAccess_v1_7();
        } else if (this.getServer().getVersion().contains("1.8")) {
            this.NMS = new NMSAccess_v1_8();
        } else if (this.getServer().getVersion().contains("1.9")) {
            this.NMS = new NMSAccess_v1_9();
        } else if (this.getServer().getVersion().contains("1.12")) {
            this.NMS = new NMSAccess_v1_12();
        } else if (this.getServer().getVersion().contains("1.16")) {
            this.NMS = new NMSAccess_v1_16();
        }

        this.getNMS().swapCommandMap();
        this.getLogger().info("[Bukkit] Hooked into Bukkit version " + this.getServer().getVersion() + "!");
    }

    public void setupExtra() {
        if (this.packetHandler != null) {
            this.packetHandler.initializePacketHandlers();
        }

        new PunishmentStrings().setupMessages();
        new Color().setupMessages();

        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.logConsole("&a[PAPI] &eSetup the &6ScandiumPAPI &ePlaceholderAPI Hook!");
        }

        BukkitUtil.registerListenersIn("com.solexgames.core.listener");

        this.registerDefaultTasks();
        this.registerSpigotCommands();
        this.registerBukkitCommands();

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.tpsRunnable, 0L, 1L);
    }

    private void registerSpigotCommands() {
        BukkitUtil.registerCommandModules(
                "Auth", "Discord", "Essential",
                "Experience", "Grant", "Library",
                "Moderation", "Network", "Other",
                "Prefix", "Punish", "Rank",
                "Shutdown", "Server", "Test",
                "Toggle", "Warps", "Disguise"
        );
    }

    private void registerDefaultTasks() {
        if (this.getConfig().getBoolean("tips.enabled")) {
            new AutoMessageTask();
        }

        new PunishExpireTask();
        new GrantExpireTask();
        new ServerUpdateTask();
        new PunishSaveTask();
        new FrozenMessageTask();
        new BoardUpdateTask();
        new ServerTimeoutTask();
        new PlayerDataUpdateTask();
    }

    private void logInformation(long milli) {
        this.getLogger().info("Initialized CorePlugin in " + (System.currentTimeMillis() - milli) + "ms (" + DurationFormatUtils.formatDurationWords((System.currentTimeMillis() - milli), true, true) + ").");
    }

    private void registerBukkitCommands() {
        if (this.getServer().getPluginManager() instanceof SimplePluginManager) {
            CommandMap commandMap = null;

            try {
                final Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");

                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(this.getServer().getPluginManager());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (commandMap != null) {
                commandMap.register(this.pluginName, new CoreCommand());

                if (this.getServerManager().getNetwork().equals(ServerType.BLARE)) {
                    commandMap.register("lib", new WebPostCommand());
                }
            } else {
                this.getServer().getPluginManager().disablePlugin(this);
                this.getLogger().warning("Your server software's PluginManager does not contain a commandMap so I cannot register a command. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
            }
        } else {
            this.getLogger().warning("Your server software is running a PluginManager that is unrecognized. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
        }
    }

    public void registerCommand(Command command) {
        if (this.getServer().getPluginManager() instanceof SimplePluginManager) {
            CommandMap commandMap = null;

            try {
                final Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");

                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(this.getServer().getPluginManager());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (commandMap != null) {
                commandMap.register(this.pluginName, command);
            } else {
                this.getServer().getPluginManager().disablePlugin(this);
                this.getLogger().warning("Your server software's PluginManager does not contain a commandMap so I cannot register a command. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
            }
        } else {
            this.getLogger().warning("Your server software is running a PluginManager that is unrecognized. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
        }
    }

    public void logConsole(String message) {
        this.getServer().getConsoleSender().sendMessage(Color.translate(message));
    }

    @Override
    public void onDisable() {
        this.serverSettings.setCanJoin(false);

        RedisUtil.publishSync(RedisUtil.onServerOffline());

        this.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + "The server is currently rebooting...\n" + ChatColor.GRAY + "Please rejoin in a few minutes."));

        this.prefixManager.savePrefixes();

        if (this.redisManager != null && this.redisManager.isActive()) {
            this.redisManager.unsubscribe();
        }
    }
}
