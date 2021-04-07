package com.solexgames.core.util.external.pagination.pagination;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class PaginatedMenu extends Menu {

    private final int maxPerPage;

    @Getter
    private int page = 1; {
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player) + " &7(" + CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + page + "/" + this.getPages(player) + "&7)";
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod    delta to modify the page number by
     */
    public final void modPage(Player player, int mod) {
        page += mod;

        this.getButtons().clear();
        this.openMenu(player);
    }

    /**
     * Changes the page number to 1
     */
    public final void reset(Player player) {
        page = 1;

        this.getButtons().clear();
        this.openMenu(player);
    }


    /**
     * @param player player viewing the inventory
     */
    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) maxPerPage);
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int) ((double) (page - 1) * maxPerPage);
        int maxIndex = (int) ((double) (page) * maxPerPage);

        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        for (Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (this.maxPerPage) * (page - 1)) - 9;
                buttons.put(ind, entry.getValue());
            }
        }

        Map<Integer, Button> global = this.getGlobalButtons(player);

        if (global != null) {
            for (Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }

        return buttons;
    }

    /**
     * @param player player viewing the inventory
     * @return a Map of button that returns items which will be present on all pages
     */
    public abstract Map<Integer, Button> getGlobalButtons(Player player);

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    public abstract String getPrePaginatedTitle(Player player);

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player player);

}
