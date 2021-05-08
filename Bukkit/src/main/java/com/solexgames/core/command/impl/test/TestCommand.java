package com.solexgames.core.command.impl.test;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;

@Command(label = "test", aliases = {"test2"}, hidden = false /* if it shows in tab completion for non staff */, async = true)
public class TestCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        new ItemBuilder(Material.MELON)
                .setDisplayName("Melon")
                .setDurability(1)
                .setOwner("Melon")
                .addItemFlags(ItemFlag.HIDE_DESTROYS)
                .toButton((player, clickType) -> player.sendMessage("Melon"));

        return false;
    }
}
