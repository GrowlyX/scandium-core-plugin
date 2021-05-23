package com.solexgames.core.util.command;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.EBaseCommand;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BukkitCommandMap extends SimpleCommandMap {

    private final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    public BukkitCommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        final int spaceIndex = cmdLine.indexOf(' ');

        if (spaceIndex == -1) {
            final ArrayList<String> completions = new ArrayList<>();
            final Map<String, Command> knownCommands = this.knownCommands;

            final String prefix = (sender instanceof Player ? "/" : "");

            for (final Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                final Command command = commandEntry.getValue();

                if (!command.testPermissionSilent(sender)) {
                    continue;
                }

                final String name = commandEntry.getKey();

                if (command instanceof BaseCommand) {
                    final BaseCommand baseCommand = (BaseCommand) command;
                    final String permissionNode = (baseCommand.getPermissionNode() == null || baseCommand.getPermissionNode().equals("") ? "scandium.staff" : baseCommand.getPermissionNode());

                    if (baseCommand.isConsoleOnly() && sender instanceof ConsoleCommandSender) {
                        completions.add(prefix + name);
                    } else if (!baseCommand.isHidden() && baseCommand.getPermissionNode().equals(""))  {
                        completions.add(prefix + name);
                    } else if (baseCommand.isHidden() && sender.hasPermission(permissionNode)) {
                        completions.add(prefix + name);
                    }
                } else if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    completions.add(prefix + name);
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
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

        final String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
        final String[] args = PATTERN_ON_SPACE.split(argLine, -1);

        try {
            return target.tabComplete(sender, commandName, args);
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
        }
    }
}
