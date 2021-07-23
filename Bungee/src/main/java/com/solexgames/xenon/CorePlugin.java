package com.solexgames.xenon;

import co.aikar.commands.BungeeCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.solexgames.xenon.command.*;
import com.solexgames.xenon.listener.PlayerListener;
import com.solexgames.xenon.manager.NetworkPlayerManager;
import com.solexgames.xenon.manager.VpnManager;
import com.solexgames.xenon.redis.JedisBuilder;
import com.solexgames.xenon.redis.JedisManager;
import com.solexgames.xenon.redis.JedisSettings;
import com.solexgames.xenon.redis.handler.impl.JedisListener;
import com.solexgames.xenon.task.ActiveTimerFooterUpdateTask;
import com.solexgames.xenon.timer.XenonTopicTimer;
import com.solexgames.xenon.util.Color;
import com.solexgames.xenon.util.MOTDUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author GrowlyX
 * @since 3/5/2021
 * @version 1.0
 * <p>
 * Holds instances to everything Xenon related.
 */

@Getter
@Setter
public class CorePlugin extends Plugin {

    @Getter
    private static CorePlugin instance;

    public static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

    public static final String JEDIS_KEY_NETWORK_PLAYERS = "xenon_network_dataupdates";

    private ArrayList<String> whitelistedPlayers = new ArrayList<>();
    private ArrayList<String> hardWhitelistedPlayers = new ArrayList<>();
    private ArrayList<ServerInfo> hubServers = new ArrayList<>();

    private Map<String, Long> vpnUsers = new ConcurrentHashMap<>();

    private Configuration configuration;
    private Configuration redisConfig;
    private File configurationFile;
    private File redisConfigFile;

    private JedisManager jedisManager;
    private NetworkPlayerManager networkPlayerManager;

    private boolean maintenance;
    private boolean hardMaintenance;

    private String motdTimerHeader;
    private String motdTimerFooter;

    private String maintenanceMotd;
    private String normalMotd;
    private String maintenanceMessage;

    private String apiKey;
    private String alertPermission;
    private String alertFormat;

    private String timerFormatEnded;
    private String timerFormatCountdown;

    private int minProtocol;
    private String minVersion;

    private int maxProtocol;
    private String maxVersion;

    private boolean centerAuto;

    private VpnManager vpnManager;

    private XenonTopicTimer xenonTopicTimer = new XenonTopicTimer();

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        createConfig();

        this.configurationFile = new File("Xenon", "settings.yml");
        this.redisConfigFile = new File("Xenon", "data.yml");

        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        this.redisConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(redisConfigFile);

        this.jedisManager = new JedisBuilder()
                .withChannel("scandium:bukkit")
                .withSettings(new JedisSettings(this.redisConfig.getString("redis.host"), this.redisConfig.getInt("redis.port"), this.redisConfig.getBoolean("redis.authentication.enabled"), this.redisConfig.getString("redis.authentication.password")))
                .withHandler(new JedisListener())
                .build();

        this.maintenance = this.configuration.getBoolean("maintenance");
        this.hardMaintenance = this.configuration.getBoolean("hardMaintenance");

        this.minProtocol = this.configuration.getInt("minimum-protocol");
        this.minVersion = this.configuration.getString("minimum-version");

        this.maxProtocol = this.configuration.getInt("maximum-protocol");
        this.maxVersion = this.configuration.getString("maximum-version");

        this.centerAuto = this.configuration.getBoolean("motd.center-automatic");

        this.apiKey = this.configuration.getString("vpn-detector.api-key");
        this.alertFormat = Color.translate(this.configuration.getString("vpn-detector.alerts.format"));
        this.alertPermission = this.configuration.getString("vpn-detector.alerts.permission");

        this.networkPlayerManager = new NetworkPlayerManager();

        this.whitelistedPlayers.addAll(this.configuration.getStringList("whitelistedPlayers"));
        this.hardWhitelistedPlayers.addAll(this.configuration.getStringList("hardWhitelistedPlayers"));

        this.maintenanceMotd = Color.translate(MOTDUtil.getCenteredMotd(this.configuration.getString("motd.maintenance.line-1")) + "<nl>" + MOTDUtil.getCenteredMotd(this.configuration.getString("motd.maintenance.line-2")))
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n");
        this.maintenanceMessage = Color.translate(this.configuration.getString("maintenance-string")
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n"));
        this.normalMotd = Color.translate(MOTDUtil.getCenteredMotd(this.configuration.getString("motd.normal.line-1")) + "<nl>" + MOTDUtil.getCenteredMotd(this.configuration.getString("motd.normal.line-2")))
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n");
        this.timerFormatEnded = Color.translate(this.configuration.getString("timer-format.ended"));
        this.timerFormatCountdown = Color.translate(this.configuration.getString("timer-format.countdown"));
        this.motdTimerHeader = Color.translate(MOTDUtil.getCenteredMotd(this.configuration.getString("motd.normal.line-1")));

        this.getProxy().getServers().values().stream()
                .filter(serverInfo -> (serverInfo.getName().contains("hub") || serverInfo.getName().contains("Hub") || serverInfo.getName().contains("Lobby") || serverInfo.getName().contains("lobby")) && !(serverInfo.getName().contains("Restricted") || serverInfo.getName().contains("restricted")))
                .forEach(serverInfo -> this.hubServers.add(serverInfo));

        this.vpnManager = new VpnManager();

        final BungeeCommandManager manager = new BungeeCommandManager(this);

        manager.registerCommand(new HubCommand());
        manager.registerCommand(new VpnCommand());
        manager.registerCommand(new MaintenanceCommand());
        manager.registerCommand(new HardMaintenanceCommand());
        manager.registerCommand(new XenonCommand());
        manager.registerCommand(new ProxyStatusCommand());
        manager.registerCommand(new TimerCommand());

        manager.enableUnstableAPI("help");



        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());


        this.getProxy().getScheduler().schedule(this, new ActiveTimerFooterUpdateTask(), 1L, 1L, TimeUnit.SECONDS);
        this.getProxy().getScheduler().schedule(this, () -> {
            this.jedisManager.get((jedis, throwable) -> {
                jedis.hset(CorePlugin.JEDIS_KEY_NETWORK_PLAYERS, "global", String.valueOf(RedisBungee.getApi().getPlayerCount()));

                this.getProxy().getServers().forEach((s, serverInfo) -> {
                    jedis.hset(CorePlugin.JEDIS_KEY_NETWORK_PLAYERS, s, String.valueOf(serverInfo.getPlayers().size()));
                });
            });
        }, 1L, 5L, TimeUnit.SECONDS);
    }

    private void createConfig() {
        if (!new File("Xenon").exists()) {
            new File("Xenon").mkdir();
        }

        final File file = new File("Xenon", "settings.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        final File newFile = new File("Xenon", "data.yml");

        if (!newFile.exists()) {
            try (InputStream in = getResourceAsStream("data.yml")) {
                Files.copy(in, newFile.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public ServerInfo getBestHub() {
        return this.hubServers.stream().filter(Objects::nonNull)
                .min(Comparator.comparingInt(server -> (int) + (long) server.getPlayers().size()))
                .orElse(null);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        if (this.jedisManager != null) {
            this.jedisManager.disconnect();
        }

        this.configuration.set("whitelistedPlayers", whitelistedPlayers);
        this.configuration.set("maintenance", maintenance);

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.configurationFile);
    }
}
