package vip.potclub.core.util.external;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import vip.potclub.core.CorePlugin;

import java.io.File;
import java.util.*;

@Getter
@Setter
public class ConfigExternal {

    private File file;
    private YamlConfiguration configuration;

    public ConfigExternal(String name) {
        this.file = new File(CorePlugin.getInstance().getDataFolder(), name + ".yml");
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }

        CorePlugin.getInstance().saveResource(name + ".yml", false);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public double getDouble(String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0D;
    }

    public int getInt(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : "ERROR: STRING NOT FOUND";
    }

    public String getString(String path, String callback, boolean colorize) {
        if (!this.configuration.contains(path)) {
            return callback;
        } else {
            return colorize ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : this.configuration.getString(path);
        }
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = this.getStringList(path);
        if (list == null) {
            return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
        } else {
            int size = list.size();
            List<String> toReturn = new ArrayList();

            for(int i = size - 1; i >= 0; --i) {
                toReturn.add(list.get(i));
            }

            return toReturn;
        }
    }

    public List<String> getStringList(String path) {
        if (!this.configuration.contains(path)) {
            return Collections.singletonList("ERROR: STRING LIST NOT FOUND!");
        } else {
            ArrayList<String> strings = new ArrayList<>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }

            return strings;
        }
    }

    public List<String> getStringListOrDefault(String path, List<String> toReturn) {
        if (!this.configuration.contains(path)) {
            return toReturn;
        } else {
            ArrayList<String> strings = new ArrayList<>();

            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }

            return strings;
        }
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }
}
