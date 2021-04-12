package com.solexgames.core.command.impl.xlib;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.website.PostBuilder;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WebPostCommand extends BukkitCommand {

    public WebPostCommand() {
        super("webpost");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.hasPermission("xlib.command.webbc")) {
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + s + ChatColor.WHITE + " <title|split with _> <content>.");
        }
        if (args.length > 1) {
            UUID uuid = UUID.randomUUID();
            PostBuilder postBuilder = new PostBuilder(uuid, sender);
            Date creation = new Date();

            String title = args[0].replace("_", " ");
            String message = StringUtil.buildMessage(args, 1).replace("<nl>", "\n");

            postBuilder.setCreation(creation);
            postBuilder.setDeck(message);
            postBuilder.setMilli(creation.getTime());
            postBuilder.setTitle(title);
            postBuilder.setFormatTime(CorePlugin.FORMAT.format(creation));

            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().insertOne(postBuilder.getDocument()));

            sender.sendMessage(Color.SECONDARY_COLOR + "You've created a website post with the title: " + Color.MAIN_COLOR + title + Color.SECONDARY_COLOR + "!");
            sender.sendMessage(Color.SECONDARY_COLOR + "View it via: " + Color.MAIN_COLOR + " https://blare.rip/");
        }
        return false;
    }
}