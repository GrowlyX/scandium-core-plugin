package com.solexgames.xenon;

import co.aikar.commands.BungeeCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.xenon.command.*;
import com.solexgames.xenon.listener.ChannelListener;
import com.solexgames.xenon.listener.PlayerListener;
import com.solexgames.xenon.proxy.ProxyManager;
import com.solexgames.xenon.redis.JedisBuilder;
import com.solexgames.xenon.redis.JedisManager;
import com.solexgames.xenon.redis.JedisSettings;
import com.solexgames.xenon.redis.handler.impl.JedisListener;
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

    public static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

    private ArrayList<String> whitelistedPlayers = new ArrayList<>();
    private ArrayList<ServerInfo> hubServers = new ArrayList<>();

    private Configuration configuration;
    private Configuration redisConfig;
    private File configurationFile;
    private File redisConfigFile;

    private JedisManager jedisManager;

    private boolean maintenance;

    private String maintenanceMotd;
    private String normalMotd;
    private String maintenanceMessage;

    private int minProtocol;
    private String minVersion;

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
                .withSettings(new JedisSettings("", 6379, true, ""))
                .withHandler(new JedisListener())
                .build();

        this.maintenance = this.configuration.getBoolean("maintenance");

        this.minProtocol = this.configuration.getInt("minimum-protocol");
        this.minVersion = this.configuration.getString("minimum-version");

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

        final BungeeCommandManager manager = new BungeeCommandManager(this);

        manager.registerCommand(new HubCommand());
        manager.registerCommand(new ListCommand());
        manager.registerCommand(new MaintenanceCommand());
        manager.registerCommand(new XenonCommand());
        manager.registerCommand(new ProxyStatusCommand());

        manager.enableUnstableAPI("help");

        this.getProxy().registerChannel("core:permissions");
        this.getProxy().registerChannel("core:update");

        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
        this.getProxy().getPluginManager().registerListener(this, new ChannelListener());
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
        if (this.jedisManager != null) {
            this.jedisManager.disconnect();
        }

        this.configuration.set("whitelistedPlayers", whitelistedPlayers);
        this.configuration.set("maintenance", maintenance);

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.configurationFile);
    }
}
