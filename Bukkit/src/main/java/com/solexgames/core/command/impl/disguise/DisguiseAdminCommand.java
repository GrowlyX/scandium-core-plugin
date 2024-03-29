package com.solexgames.core.command.impl.disguise;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.VotingUtil;
import com.solexgames.core.util.builder.PageListBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Command(label = "disguiseadmin", permission = "scandium.command.disguiseadmin", aliases = {"nickadmin"}, async = true)
public class DisguiseAdminCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            this.getHelpMessage(1, sender,
                    "/disguiseadmin add <username>",
                    "/disguiseadmin massadd <url>",
                    "/disguiseadmin remove <username>",
                    "/disguiseadmin list"
            );
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length < 2) {
                        this.getHelpMessage(1, sender,
                                "/disguiseadmin add <username>",
                                "/disguiseadmin massadd <url>",
                                "/disguiseadmin remove <username>",
                                "/disguiseadmin list"
                        );
                    }
                    if (args.length == 2) {
                        final UUID uuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(args[1]);

                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Error: That player is not valid.");
                            return false;
                        }

                        if (CorePlugin.getInstance().getDisguiseCache().getByUuid(uuid) != null || CorePlugin.getInstance().getDisguiseCache().getByName(args[1]) != null) {
                            player.sendMessage(ChatColor.RED + "Error: There's already a disguise profile with that name!");
                            return false;
                        }

                        final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseManager().getDisguiseData(args[1], uuid);

                        if (disguiseData == null) {
                            player.sendMessage(ChatColor.RED + "Error: We couldn't create a new disguise profile for that username.");
                            return false;
                        }

                        CorePlugin.getInstance().getDisguiseCache().registerNewDataPair(disguiseData);
                        RedisUtil.publishAsync(RedisUtil.onDisguiseProfileCreate(disguiseData));

                        disguiseData.saveData();

                        player.sendMessage(Color.SECONDARY_COLOR + "You've registered a new disguise profile with the name " + Color.MAIN_COLOR + disguiseData.getName() + Color.SECONDARY_COLOR + "!");
                    }
                    break;
                case "remove":
                    if (args.length < 2) {
                        this.getHelpMessage(1, sender,
                                "/disguiseadmin add <username>",
                                "/disguiseadmin massadd <url>",
                                "/disguiseadmin remove <username>",
                                "/disguiseadmin list"
                        );
                    }
                    if (args.length == 2) {
                        final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getDisguiseDataList().stream()
                                .filter(disguiseData1 -> disguiseData1.getName().equalsIgnoreCase(args[1]))
                                .findFirst().orElse(null);

                        if (disguiseData == null) {
                            player.sendMessage(ChatColor.RED + "Error: We couldn't find a disguise profile with that name.");
                            return false;
                        }

                        CorePlugin.getInstance().getDisguiseCache().removeDataPair(disguiseData);

                        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getDisguiseCollection().deleteOne(Filters.eq("_id", disguiseData.getUuid())));

                        RedisUtil.publishAsync(RedisUtil.onDisguiseProfileRemove(disguiseData));

                        player.sendMessage(Color.SECONDARY_COLOR + "You've removed the disguise profile with the name " + Color.MAIN_COLOR + disguiseData.getName() + Color.SECONDARY_COLOR + "!");
                    }
                    break;
                case "massadd":
                    if (args.length < 2) {
                        this.getHelpMessage(1, sender,
                                "/disguiseadmin add <username>",
                                "/disguiseadmin massadd <url>",
                                "/disguiseadmin remove <username>",
                                "/disguiseadmin list"
                        );
                    }
                    if (args.length == 2) {
                        try (Scanner scanner = new Scanner(new URL(args[1]).openStream()).useDelimiter("\\A")) {
                            player.sendMessage(Color.SECONDARY_COLOR + "Starting the process of mass adding disguise profiles...");

                            String inputLine;
                            while ((inputLine = scanner.nextLine()) != null) {
                                final UUID uuid = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(inputLine);

                                if (uuid != null) {
                                    if (CorePlugin.getInstance().getDisguiseCache().getByUuid(uuid) != null || CorePlugin.getInstance().getDisguiseCache().getByName(args[1]) != null) {
                                        continue;
                                    }

                                    final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseManager().getDisguiseData(inputLine, uuid);

                                    if (disguiseData != null) {
                                        CorePlugin.getInstance().getDisguiseCache().registerNewDataPair(disguiseData);
                                        RedisUtil.publishAsync(RedisUtil.onDisguiseProfileCreate(disguiseData));

                                        disguiseData.saveData();

                                        player.sendMessage(Color.SECONDARY_COLOR + "You've registered a new disguise profile with the name " + Color.MAIN_COLOR + disguiseData.getName() + Color.SECONDARY_COLOR + "!");
                                    }
                                }
                            }

                            return Boolean.parseBoolean(scanner.next());
                        } catch (Exception ignored) {
                            player.sendMessage();
                        }
                    }
                    break;
                case "list":
                    final PageListBuilder pageListBuilder = new PageListBuilder(10, "Disguises");
                    final List<String> stringList = CorePlugin.getInstance().getDisguiseCache().getDisguiseDataList().stream()
                            .map(DisguiseData::getName)
                            .collect(Collectors.toList());

                    if (args.length == 1) {
                        pageListBuilder.display(sender, 1, stringList);
                    }
                    if (args.length == 2) {
                        try {
                            final int page = Integer.parseInt(args[0]);

                            pageListBuilder.display(sender, page, stringList);
                        } catch (Exception ignored) {
                            player.sendMessage(ChatColor.RED + "That's not a valid page integer!");
                        }
                    }
                    break;
                default:
                    this.getHelpMessage(1, sender,
                            "/disguiseadmin add <username>",
                            "/disguiseadmin massadd <url>",
                            "/disguiseadmin remove <username>",
                            "/disguiseadmin list"
                    );
            }
        }

        return false;
    }
}
