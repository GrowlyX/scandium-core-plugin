package com.solexgames.core.util.command;

import com.solexgames.core.command.BaseCommand;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomCommandMap extends SimpleCommandMap {

    private final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    public CustomCommandMap(Server server) {
        super(server);
    }

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
}
