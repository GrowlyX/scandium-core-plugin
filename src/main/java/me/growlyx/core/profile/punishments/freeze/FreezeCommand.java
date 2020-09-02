package me.growlyx.core.profile.punishments.freeze;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.potion.PotionEffect;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "freeze", permission = "core.commands.freeze"))
public class FreezeCommand implements CommandExecutor {

    private Core plugin;

    public FreezeCommand(final Core plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {

        Player player = (Player) sender;

        if (args.length != 1) {
            return false;
        }

        final Player target = Core.instance.getServer().getPlayer(args[0]);
        if (target == null) {

            sender.sendMessage(CC.translate("&cError: Player not found."));
            return true;

        }
        if (Core.instance.getManagerHandler().getFrozenManager().isFrozen(target.getUniqueId())) {

            sender.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&aYou have unfrozen &f" + target.getDisplayName()));
            this.unfreezePlayer(target);

            return true;

        }
        sender.sendMessage(ChatColor.GREEN + "You have been frozen " + target.getName());
        this.freezePlayer(target);
        return true;
    }

    private void freezePlayer(final Player player) {

        Core.instance.getManagerHandler().getPlayerSnapshotManager().takeSnapshot(player);
        Core.instance.getManagerHandler().getFrozenManager().freezeUUID(player.getUniqueId());

        for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.FROZEN-MSG")) {
            player.sendMessage(CC.translate(string));
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.setWalkSpeed(0.0f);
        this.clearPotionEffects(player);
        player.updateInventory();
        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getFrozenInv());
    }

    private void unfreezePlayer(final Player player) {
        Core.instance.getManagerHandler().getFrozenManager().unfreezeUUID(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have been unfrozen by staff.");
        player.closeInventory();
        Core.instance.getManagerHandler().getPlayerSnapshotManager().restorePlayer(player);
    }

    private void clearPotionEffects(final Player player) {
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

}