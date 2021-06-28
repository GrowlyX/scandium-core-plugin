package com.solexgames.core.hooks.nms.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.EBaseCommand;
import com.solexgames.core.hooks.nms.INMS;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class NMSAccess_v1_16 implements INMS {

    @Override
    public void removeExecute(Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void addExecute(Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void updateTablist() {
        /*final List<EntityPlayer> playerList = new ArrayList<>(MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> +(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            Object list = MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(MinecraftServer.getServer().getPlayerList());
            Class<?> playerListClass = list.getClass().getSuperclass();
            Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, finalList);
        } catch (Exception ignored) {
        }*/
    }

    @Override
    public void updateCache(Player player) {
        final List<net.minecraft.server.v1_16_R3.EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_16_R3.MinecraftServer.getServer().getPlayerList().players);
        final net.minecraft.server.v1_16_R3.EntityPlayer entityPlayer = playerList.stream()
                .filter(entityPlayer1 -> entityPlayer1.getUniqueID().equals(player.getUniqueId()))
                .findFirst().orElse(null);

        playerList.remove(entityPlayer);
        playerList.add(((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle());

        try {
            final Object list = net.minecraft.server.v1_16_R3.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_16_R3.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, playerList);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setupTablist(Player player) {
//        if (CorePlugin.getInstance().getServerSettings().isTabEnabled()) {
//            IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabHeader() + "\"}");
//            IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabFooter() + "\"}");
//            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
//
//            try {
//                Field headerField = packet.getClass().getDeclaredField("a");
//                headerField.setAccessible(true);
//                headerField.set(packet, tabHeader);
//                headerField.setAccessible(false);
//
//                Field footerField = packet.getClass().getDeclaredField("b");
//                footerField.setAccessible(true);
//                footerField.set(packet, tabFooter);
//                footerField.setAccessible(false);
//            } catch (Exception ignored) {
//            }
//
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//        }
    }

    @Override
    public void updatePlayer(Player player) {
        final net.minecraft.server.v1_16_R3.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle();
        final Location previousLocation = player.getLocation().clone();

        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo(net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo(net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        /*entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutRespawn(
                entityPlayer.getWorld().getMinecraftWorld().getDimensionManager(),
                entityPlayer.getWorld().worldData.getDifficulty(),
                3L,
                entityPlayer.getWorldServer().getSeed(),
                entityPlayer.playerInteractManager.getGameMode(),
                entityPlayer.playerInteractManager.getGameMode(),
                false,
                false,
                true
        ));*/

        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();

        player.teleport(previousLocation);
    }

    @Override
    public void swapCommandMap() {
        try {
            final Field commandMapField = CorePlugin.getInstance().getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final Object oldCommandMap = commandMapField.get(CorePlugin.getInstance().getServer());
            final CraftCommandMap newCommandMap = new CraftCommandMap(CorePlugin.getInstance().getServer()) {

                private final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

                @Override
                public List<String> tabComplete(CommandSender sender, String cmdLine) {
                    Validate.notNull(sender, "Sender cannot be null");
                    Validate.notNull(cmdLine, "Command line cannot null");

                    int spaceIndex = cmdLine.indexOf(' ');

                    if (spaceIndex == -1) {
                        final ArrayList<String> completions = new ArrayList<>();
                        final Map<String, Command> knownCommands = this.knownCommands;

                        final String prefix = (sender instanceof Player ? "/" : "");

                        for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                            final Command command = commandEntry.getValue();

                            if (!command.testPermissionSilent(sender)) {
                                continue;
                            }

                            final String name = commandEntry.getKey();

                            if (command instanceof BaseCommand) {
                                final BaseCommand baseCommand = (BaseCommand) command;

                                if (!baseCommand.isHidden()) {
                                    completions.add(prefix + name);
                                } else if (baseCommand.isHidden() && sender.hasPermission("scandium.staff")) {
                                    completions.add(prefix + name);
                                }
                            } else if (command instanceof EBaseCommand) {
                                final EBaseCommand baseCommand = (EBaseCommand) command;

                                if (!baseCommand.isHidden()) {
                                    completions.add(prefix + name);
                                } else if (baseCommand.isHidden() && sender.hasPermission("scandium.staff")) {
                                    completions.add(prefix + name);
                                }
                            } else if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                                completions.add(prefix + name);
                            }
                        }

                        completions.sort(String.CASE_INSENSITIVE_ORDER);
                        return completions;
                    }

                    final String commandName = cmdLine.substring(0, spaceIndex);
                    final Command target = getCommand(commandName);

                    if (target == null) {
                        return null;
                    }

                    if (!target.testPermissionSilent(sender)) {
                        return null;
                    }

                    final String argLine = cmdLine.substring(spaceIndex + 1);
                    final String[] args = PATTERN_ON_SPACE.split(argLine, -1);

                    try {
                        return target.tabComplete(sender, commandName, args);
                    } catch (CommandException ex) {
                        throw ex;
                    } catch (Throwable ex) {
                        throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
                    }
                }
            };

            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & -17);

            knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
            commandMapField.set(CorePlugin.getInstance().getServer(), newCommandMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
