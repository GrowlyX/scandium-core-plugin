package me.growlyx.core.essentials.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.annotation.command.Commands;

import java.util.ArrayList;
import java.util.Iterator;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "freeze", permission = "core.freeze"))
public class FreezeCommand implements CommandExecutor {

    ArrayList<String> frozen = new ArrayList();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

            sender.sendMessage(CC.translate("&cUsage: /freeze <player>"));
            return true;

        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {

            sender.sendMessage(CC.translate("&cError: Player not found."));
            return true;

        }

        if (this.frozen.contains(target.getName())) {

            this.frozen.remove(target.getName());
            sender.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.UNFREEZE")));
            return true;

        }

        this.frozen.add(target.getName());
        sender.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.FREEZE")));
        return true;

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (this.frozen.contains(p.getName())) {

            e.setTo(e.getFrom());

            for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.FROZEN-MSG")) {
                p.sendMessage(string);
            }
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            Player p = (Player)event.getEntity();

            if (this.frozen.contains(p.getName())) {

                event.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (this.frozen.contains(p.getName())) {
            event.setCancelled(true);

            for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.FROZEN-MSG")) {
                event.getPlayer().sendMessage(string);
            }
        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (this.frozen.contains(p.getName())) {
            event.setCancelled(true);

            for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.FROZEN-MSG")) {
                event.getPlayer().sendMessage(string);
            }

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player p = event.getPlayer();

        if (this.frozen.contains(p.getName())) {

            Iterator iterator = Bukkit.getOnlinePlayers().iterator();

            while(iterator.hasNext()) {

                Player players = (Player) iterator.next();

                if (players.hasPermission("core.staff")) {

                    players.sendMessage(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.FREEZE-LEAVE")
                            .replace("<frozen>", p.getName()));


                }

            }
        }

    }

}