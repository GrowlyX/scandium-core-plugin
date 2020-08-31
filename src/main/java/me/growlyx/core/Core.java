package me.growlyx.core;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import me.growlyx.core.chat.commands.ClearChatCommand;
import me.growlyx.core.commands.CoreCommand;
import me.growlyx.core.commands.ServerInfoCommand;
import me.growlyx.core.essentials.commands.*;
import me.growlyx.core.essentials.commands.gamemodes.*;
import me.growlyx.core.essentials.commands.info.*;
import me.growlyx.core.listeners.JoinListener;
import me.growlyx.core.listeners.LeaveListener;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.Config;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name="Core", version="1.0-SNAPSHOT")
@Author(value = "GrowlyX")
@LogPrefix(value = "Core")
@Website(value = "growlyx.me")
public final class Core extends JavaPlugin {

    Config msg = new Config("plugins/Core", "messages.yml", this);
    Config tags = new Config("plugins/Core", "tags.yml", this);
    Config db = new Config("plugins/Core", "database.yml", this);
    Config license = new Config("plugins/Core", "license.yml", this);

    public static Core instance;
    public Config t;
    public Config m;
    public Config d;
    public Config l;

    @Override
    public void onEnable() {

        initializeConfigurationFiles();

        System.out.println(CC.translate("--------------------------------------"));
        System.out.println(CC.translate("Core - Starting Startup Process..."));
        System.out.println(CC.translate("Developed By devGrowly"));
        System.out.println(CC.translate("--------------------------------------"));
        System.out.println(CC.translate("Licensing Started..."));

        if(!new CoreLicense(license.getConfig().getString("HWID"), "https://growly.alwaysdata.net/verify.php/", this).setSecurityKey("010110111100000000001100101110000001").register()) return;

        System.out.println(CC.translate("Core Anti-Piracy System Verified..."));
        System.out.println(CC.translate("--------------------------------------"));

        instance = this;

        System.out.println(CC.translate("Framework Initialized..."));
        System.out.println(CC.translate("--------------------------------------"));

        initializeCommandManager();

        System.out.println(CC.translate("Commands Registered..."));
        System.out.println(CC.translate("--------------------------------------"));

        intializeListeners();

        System.out.println(CC.translate("Listeners Registered..."));
        System.out.println(CC.translate("--------------------------------------"));


        System.out.println(CC.translate("MongoDB Initialized..."));
        System.out.println(CC.translate("--------------------------------------"));

        System.out.println(CC.translate("Core Enabled Sucessfully!"));
        System.out.println(CC.translate("--------------------------------------"));

        l = license;
        m = msg;
        t = tags;
        d = db;

    }

    private void initializeMongo() {

        String s = db.getConfig().getString("MONGO.PASSWORD");
        char[] s1 = s.toCharArray();

        String user = db.getConfig().getString("MONGO.USERNAME");
        String database = db.getConfig().getString("MONGO.DATABASE");
        char[] password = s1;

        MongoCredential credential = MongoCredential.createCredential(user, database, password);
        MongoClient mongoClient = MongoClients.create(db.getConfig().getString("MONGO.CONNECTION-URL"));
        MongoDatabase mongo = mongoClient.getDatabase(database);

        mongo.createCollection("ranks", new CreateCollectionOptions());

        MongoCollection<Document> collection = mongoClient.getDatabase(db.getConfig().getString("MONGO.DATABASE")).getCollection("ranks");

    }

    public void initializeCommandManager() {

        cmd("gma").setExecutor(new GMA());
        cmd("gms").setExecutor(new GMS());
        cmd("gmc").setExecutor(new GMC());
        cmd("gmsp").setExecutor(new GMSP());
        cmd("sudo").setExecutor(new SudoCommand());
        cmd("core").setExecutor(new CoreCommand());
        cmd("feed").setExecutor(new FeedCommand());
        cmd("heal").setExecutor(new HealCommand());
        cmd("ping").setExecutor(new PingCommand());
        cmd("hide").setExecutor(new HideCommand());
        cmd("more").setExecutor(new MoreCommand());
        cmd("time").setExecutor(new TimeCommand());
        cmd("clear").setExecutor(new ClearCommand());
        cmd("store").setExecutor(new StoreCommand());
        cmd("unhide").setExecutor(new UnHideCommand());
        cmd("helpop").setExecutor(new HelpOPCommand());
        cmd("forums").setExecutor(new ForumsCommand());
        cmd("report").setExecutor(new ReportCommand());
        cmd("freeze").setExecutor(new FreezeCommand());
        cmd("website").setExecutor(new WebsiteCommand());
        cmd("sudoall").setExecutor(new SudoAllCommand());
        cmd("discord").setExecutor(new DiscordCommand());
        cmd("gamemode").setExecutor(new GamemodeCommand());
        cmd("broadcast").setExecutor(new BroadcastCommand());
        cmd("clearchat").setExecutor(new ClearChatCommand());
        cmd("gamemodes").setExecutor(new GamemodesCommand());
        cmd("teamspeak").setExecutor(new TeamSpeakCommand());
        cmd("serverinfo").setExecutor(new ServerInfoCommand());




    }


    public void intializeListeners() {

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents((Listener) new FreezeCommand(), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);

    }

    public void initializeConfigurationFiles() {

        license.create();
        msg.create();
        db.create();
        tags.create();

        if(!license.exists()) {
            license.setDefault("license.yml");
            license.getConfig().options().copyDefaults(true);
            license.saveConfig();
        }

        if(!msg.exists()) {
            msg.setDefault("messages.yml");
            msg.getConfig().options().copyDefaults(true);
            msg.saveConfig();
        }

        if(!tags.exists()) {
            tags.setDefault("tags.yml");
            tags.getConfig().options().copyDefaults(true);
            tags.saveConfig();
        }

        if(!db.exists()) {
            db.setDefault("database.yml");
            db.getConfig().options().copyDefaults(true);
            db.saveConfig();
        }

        this.saveDefaultConfig();
        getConfig().options().copyDefaults();

    }

    @Override
    public void onDisable() {

        System.out.println(CC.translate("--------------------------------------"));
        System.out.println(CC.translate("Core Disabled Sucessfully!"));
        System.out.println(CC.translate("--------------------------------------"));

    }

    public PluginCommand cmd(String text) {

        String output = text;
        return this.getCommand(output);

    }



}
