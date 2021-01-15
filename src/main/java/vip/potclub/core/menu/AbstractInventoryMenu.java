package vip.potclub.core.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractInventoryMenu<T extends JavaPlugin> implements IMenu {

    protected JavaPlugin plugin;
    protected Inventory inventory;

    public AbstractInventoryMenu(String title, int size) {
        this.plugin = JavaPlugin.getPlugin((Class)(((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
        this.inventory = this.plugin.getServer().createInventory(this, size, title.length() > 32 ? title.substring(0, 32) : title);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
