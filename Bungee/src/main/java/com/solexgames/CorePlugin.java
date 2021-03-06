package com.solexgames;

import com.solexgames.command.*;
import com.solexgames.listener.PlayerListener;
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
    public static CorePlugin instance;

    public ArrayList<String> whitelistedPlayers = new ArrayList<>();
    public ArrayList<ServerInfo> hubServers = new ArrayList<>();

    public Configuration configuration;
    public File configurationFile;

    public boolean maintenance;

    public String maintenanceMotd;
    public String normalMotd;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        createConfig();

        this.configurationFile = new File(getDataFolder(), "config.yml");
        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);

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
    }

    public void registerCommands(Command... commands) {
        for (Command command : commands) {
            ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
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
        this.configuration.set("whitelistedPlayers", whitelistedPlayers);
        this.configuration.set("maintenance", maintenance);

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configurationFile);
    }
}
