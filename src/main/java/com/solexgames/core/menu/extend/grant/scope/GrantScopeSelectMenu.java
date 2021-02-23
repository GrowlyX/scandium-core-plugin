package com.solexgames.core.menu.extend.grant.scope;

import com.solexgames.core.menu.AbstractInventoryMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class GrantScopeSelectMenu extends AbstractInventoryMenu {

    public GrantScopeSelectMenu() {
        super("Select grant scope for: ", 9*3);
    }

    @Override
    public void update() {

    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException {

    }
}
