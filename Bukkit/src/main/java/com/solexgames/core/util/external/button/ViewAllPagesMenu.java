package com.solexgames.core.util.external.button;

import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import com.solexgames.core.util.external.pagination.PaginatedMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ViewAllPagesMenu extends Menu {

    public final PaginatedMenu menu;

    @Override
    public String getTitle(Player player) {
        return "Open a page...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final HashMap<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        for (int i = 1; i <= this.menu.getPages(player); i++) {
            buttons.put(index++, new JumpToPageButton(i, this.menu, this.menu.getPage() == i));
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
