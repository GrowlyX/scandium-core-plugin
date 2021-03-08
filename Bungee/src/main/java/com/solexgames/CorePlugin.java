package com.solexgames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.command.*;
import com.solexgames.listener.PlayerListener;
import com.solexgames.proxy.ProxyManager;
import com.solexgames.redis.RedisManager;
import com.solexgames.redis.RedisSettings;
import com.solexgames.util.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
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
import java.util.stream.Collectors;

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

        this.configurationFile = new File(getDataFolder(), "config.yml");
        this.redisConfigFile = new File("Xenon", "config.yml");

        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        this.redisConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(redisConfigFile);

        this.proxyManager = new ProxyManager();
        this.redisManager = new RedisManager(new RedisSettings(
                this.redisConfig.getString("redis.host"),
                this.redisConfig.getInt("redis.port"),
                this.redisConfig.getBoolean("redis.authentication.enabled"),
                this.redisConfig.getString("redis.authentication.password")
        ));

        this.maintenance = this.configuration.getBoolean("maintenance");
        this.whitelistedPlayers.addAll(this.configuration.getStringList("whitelistedPlayers"));

        this.maintenanceMotd = Color.translate(this.configuration.getString("motd.maintenance")
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n"));
        this.normalMotd = Color.translate(this.configuration.getString("motd.normal")
                .replace("<bar>", Character.toString('⎜'))
                .replace("<nl>", "\n"));

        this.getProxy().getServers().values().stream()
                .filter(serverInfo -> serverInfo.getName().contains("hub") || serverInfo.getName().contains("lobby"))
                .forEach(serverInfo -> this.hubServers.add(serverInfo));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GlobalListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new StaffListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new XenonCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MaintenanceCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyStatusCommand());

        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    private void createConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
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
                .min(Comparator.comparingInt(server -> (int) +(long) server.getPlayers().size()))
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

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configurationFile);
    }
}
