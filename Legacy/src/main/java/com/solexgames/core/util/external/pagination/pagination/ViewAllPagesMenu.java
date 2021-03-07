package com.solexgames.core.util.external.pagination.pagination;

import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.Menu;
import com.solexgames.core.util.external.pagination.button.BackButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ViewAllPagesMenu extends Menu {

    @NonNull
    @Getter
    @Setter
    public PaginatedMenu menu;

    @Override
    public String getTitle(Player player) {
        return "Open a page...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        for (int i = 1; i <= menu.getPages(player); i++) {
            buttons.put(index++, new JumpToPageButton(i, menu, menu.getPage() == i));
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
