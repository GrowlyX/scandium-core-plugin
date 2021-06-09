package com.solexgames.core.manager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.awt.*;

@Getter
@Setter
public class DiscordManager {

    private WebhookClient client;

    public DiscordManager() {
        this.setupWebhook();
    }

    public void setupWebhook() {
        if (!CorePlugin.getInstance().getConfig().getBoolean("discord.enabled")) {
            return;
        }

        final WebhookClientBuilder builder = new WebhookClientBuilder(CorePlugin.getInstance().getConfig().getString("discord.webhook"));

        builder.setThreadFactory(job -> {
            final Thread thread = new Thread(job);

            thread.setName("Scandium Webhook Executor");
            thread.setDaemon(true);

            return thread;
        });
        builder.setWait(true);

        this.client = builder.build();
    }

    public void sendReport(Player player, Player target, String reason) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Reports", null));

        embedBuilder.setDescription(String.join("\n",
                "We've received a report from **" + player.getName() + "**",
                "with a target of **" + target.getName() + "** and with",
                "the reason being `" + reason + "`.",
                " ",
                "*This report was sent from " + CorePlugin.getInstance().getServerName() + ".*"
        ));

        embedBuilder.setColor(0xffd700);

        this.client.send(embedBuilder.build());
    }

    public void sendPunishment(Punishment punishment) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        final String target = CorePlugin.getInstance().getUuidCache().getUsernameFromUuid(punishment.getTarget());

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(punishment.getPunishmentType().getName(), null));

        embedBuilder.setDescription(String.join("\n",
                "There's been a new " + punishment.getPunishmentType().getName() + " registered on",
                CorePlugin.getInstance().getServerName() + ".",
                " ",
                "**__Details__**",
                " • Issuer: `" + punishment.getIssuerName() + "`",
                " • Target: `" + CorePlugin.getInstance().getUuidCache().getUsernameFromUuid(punishment.getTarget()) + "`",
                " • Reason: `" + punishment.getReason() + "`",
                " • Duration: `" + punishment.getDurationString() + "`",
                " • Expiration: `" + punishment.getExpirationString() + "`",
                "  ",
                "*View this punishment via /c " + target + ".*"
        ));

        embedBuilder.setColor(0xffd700);

        this.client.send(embedBuilder.build());
    }

    public void sendRequest(Player player, String reason) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Requests", null));

        embedBuilder.setDescription(String.join("\n",
                "We've received a request by **" + player.getName() + "**",
                "with the reason being `" + reason + "`.",
                " ",
                "*This report was sent from " + CorePlugin.getInstance().getServerName() + ".*"
        ));

        embedBuilder.setColor(0xffd700);

        this.client.send(embedBuilder.build());
    }
}
