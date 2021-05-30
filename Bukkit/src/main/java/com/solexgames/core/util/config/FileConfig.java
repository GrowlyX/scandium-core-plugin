package com.solexgames.core.util.config;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class FileConfig {

    private final File file;
    private final YamlConfiguration configuration;

    public FileConfig(String name) {
        this.file = new File(CorePlugin.getInstance().getDataFolder(), name + ".yml");

        if (!this.file.exists()) {
            this.file.getParentFile().mkdir();

            if (CorePlugin.getInstance().getResource(name + ".yml") == null) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    CorePlugin.getInstance().getLogger().severe("Failed to create new file " + name + ".yml");
                }
            } else {
                CorePlugin.getInstance().saveResource(name + ".yml", false);
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() throws IOException {
        this.configuration.save(this.file);
    }

    public int getInt(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? Color.translate(this.configuration.getString(path)) : null;
    }

    public String getString(String path, String fallback, boolean colorize) {
        if (!this.configuration.contains(path)) {
            return fallback;
        } else {
            return colorize ? Color.translate(this.configuration.getString(path)) : this.configuration.getString(path);
        }
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            final ArrayList<String> strings = new ArrayList<>();

            for (String string : this.configuration.getStringList(path)) {
                strings.add(Color.translate(string));
            }

            return strings;
        }

        return null;
    }

    public List<String> getStringList(String path, List<String> fallback) {
        if (this.configuration.contains(path)) {
            final ArrayList<String> strings = new ArrayList<>();

            for (String string : this.configuration.getStringList(path)) {
                strings.add(Color.translate(string));
            }

            return strings;
        }

        return fallback;
    }
}
