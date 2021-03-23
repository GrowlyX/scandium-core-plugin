package com.solexgames.core.internal.tablist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class TablistElement {

    private final String display;
    private final int slot;

    public TablistElement(String display, int slot) {
        this.display = ChatColor.translateAlternateColorCodes('&', display);
        this.slot = slot;
    }

    protected static TablistElement getByPosition(Player player, int slot) {
        List<TablistElement> elements = null;
        int size = 0;
        TablistElement returnElement = null;

        if (OutlastTab.getInstance().getTablistVersion().getSlots(player) == 80) {
            elements = OutlastTab.getInstance().getTablist().getElements(player);
            size = elements.size();
            for (int i = 0; i < size; i++)
                if (elements.get(i).getSlot() == slot) {
                    returnElement = elements.get(i);
                    break;
                }
        } else {
            // Found the most efficient way. ~Zanctarian
            // Note: Iterators are typically slow, avoid them if possible by using counting for loops.
            int algorithm = (((slot % 3 == 0 ? 3 : slot % 3) - 1) * 20) + (int) (Math.ceil(slot / (float) 3));
            elements = OutlastTab.getInstance().getTablist().getElements(player);
            size = elements.size();
            for (int i = 0; i < size; i++)
                if (elements.get(i).getSlot() == algorithm) {
                    returnElement = elements.get(i);
                    break;
                }
        }

        return returnElement;
    }

    public String getDisplay() {
        return display;
    }

    public int getSlot() {
        return slot;
    }
}
