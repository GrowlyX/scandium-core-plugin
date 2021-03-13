package com.solexgames.core.command.extend.server;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SetSlotsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.setslots")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <int>."));
        }
        if (args.length == 1) {
            try {
                int slots = Integer.parseInt(args[0]);
                setSlots(slots);
                player.sendMessage(Color.translate("&aUpdated the max player value to " + slots + " Players."));
            } catch (NumberFormatException e) { player.sendMessage(Color.translate("&cThat's not a valid integer.")); }
        }
        return false;
    }

    private void setSlots(int slots) {
        slots = Math.abs(slots);

        try {
            Object invoke = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer").getDeclaredMethod("getHandle", new Class[0]).invoke(Bukkit.getServer());
            Field declaredField = invoke.getClass().getSuperclass().getDeclaredField("maxPlayers");

            declaredField.setAccessible(true);
            declaredField.set(invoke, slots);

            changeProperties(slots);
        } catch (ReflectiveOperationException exception) {
            System.out.println("[Error] While setting slots of server. " + exception.getMessage());
        }
    }

    private void changeProperties(int slots) {
        Path resolve = Paths.get(CorePlugin.getInstance().getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties");

        try {
            List<String> allLines = Files.readAllLines(resolve);

            for (int i = 0; i < allLines.size(); ++i) {
                if (((String)allLines.get(i)).startsWith("max-players")) {
                    allLines.remove(i);
                }
            }

            allLines.add("max-players=" + slots);
            Files.write(resolve, allLines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            System.out.println("[Error] While setting slots of server. " + exception.getMessage());
        }
    }
}
