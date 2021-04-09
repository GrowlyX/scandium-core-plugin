package com.solexgames.xenon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.xenon.command.*;
import com.solexgames.xenon.listener.PlayerListener;
import com.solexgames.xenon.proxy.ProxyManager;
import com.solexgames.xenon.redis.RedisManager;
import com.solexgames.xenon.redis.RedisSettings;
import com.solexgames.xenon.util.Color;
import com.solexgames.xenon.util.MOTDUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
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
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    public static Gson GSON;
    public static GsonBuilder GSONBUILDER;

    private ArrayList<String> whitelistedPlayers = new ArrayList<>();
    private ArrayList<ServerInfo> hubServers = new ArrayList<>();

    private Configuration configuration;
    private Configuration redisConfig;
    private File configurationFile;
    private File redisConfigFile;

    private ProxyManager proxyManager;
    private RedisManager redisManager;

    private boolean maintenance;

    private String maintenanceMotd;
    private String normalMotd;
    private String maintenanceMessage;

    private Executor redisExecutor;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        GSONBUILDER = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting();
        GSON = GSONBUILDER.create();

        createConfig();

        this.redisExecutor = Executors.newFixedThreadPool(1);

        this.configurationFile = new File("Xenon", "settings.yml");
        this.redisConfigFile = new File("Xenon", "data.yml");

        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        this.redisConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(redisConfigFile);

/*        this.proxyManager = new ProxyManager();
        this.redisManager = new RedisManager(new RedisSettings(
                this.redisConfig.getString("redis.host"),
                this.redisConfig.getInt("redis.port"),
                this.redisConfig.getBoolean("redis.authentication.enabled"),
                this.redisConfig.getString("redis.authentication.password")
        ));*/

        this.maintenance = this.configuration.getBoolean("maintenance");
        this.whitelistedPlayers.addAll(this.configuration.getStringList("whitelistedPlayers"));

        this.maintenanceMotd = Color.translate(MOTDUtil.getCenteredMotd(this.configuration.getString("motd.maintenance.line-1")) + "<nl>" + MOTDUtil.getCenteredMotd(this.configuration.getString("motd.maintenance.line-2")))
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n");
        this.maintenanceMessage = Color.translate(this.configuration.getString("maintenance-string")
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n"));
        this.normalMotd = Color.translate(MOTDUtil.getCenteredMotd(this.configuration.getString("motd.normal.line-1")) + "<nl>" + MOTDUtil.getCenteredMotd(this.configuration.getString("motd.normal.line-2")))
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n");

        this.getProxy().getServers().values().stream()
                .filter(serverInfo -> (serverInfo.getName().contains("hub") || serverInfo.getName().contains("Hub") || serverInfo.getName().contains("Lobby") || serverInfo.getName().contains("lobby")) && !(serverInfo.getName().contains("Restricted") || serverInfo.getName().contains("restricted")))
                .forEach(serverInfo -> this.hubServers.add(serverInfo));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GlobalListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new XenonCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MaintenanceCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyStatusCommand());

        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().registerChannel("");
    }

    private void createConfig() {
        if (!new File("Xenon").exists())
            new File("Xenon").mkdir();
        File file = new File("Xenon", "settings.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!new File("Xenon").exists())
            new File("Xenon").mkdir();
        File newFile = new File("Xenon", "data.yml");
        if (!newFile.exists()) {
            try (InputStream in = getResourceAsStream("data.yml")) {
                Files.copy(in, newFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerInfo getBestHub() {
        return this.hubServers.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(server -> (int) + (long) server.getPlayers().size()))
                .orElse(null);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        if (this.redisManager != null) {
            if (this.redisManager.isActive()) {
                this.redisManager.unsubscribe();
            }
        }

        this.configuration.set("whitelistedPlayers", whitelistedPlayers);
        this.configuration.set("maintenance", maintenance);

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.configurationFile);
    }
}
