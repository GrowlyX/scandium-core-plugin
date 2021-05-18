package com.solexgames.core.command.impl.test;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.Button;
import com.solexgames.core.util.external.Menu;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.Map;

@Command(label = "test", aliases = {"test2"}, hidden = false /* if it shows in tab completion for non staff */)
public class TestCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        final Menu menu = new Menu() {
            @Override
            public String getTitle(Player player) {
                return "testing Menu";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> integerButtonMap = new HashMap<>();

                for (int i = 10; i >= 10 && i <= 16; i++) {
                    integerButtonMap.put(i, new ItemBuilder(XMaterial.COMPARATOR).setDisplayName(Color.MAIN_COLOR.toString() + i + " Slot").toButton());
                }

                return integerButtonMap;
            }
        };

        menu.setFillBorders(true);
        menu.openMenu((Player) sender);

        return false;
    }
}
