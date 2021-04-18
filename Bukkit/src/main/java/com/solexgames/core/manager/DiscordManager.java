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
            Thread thread = new Thread(job);

            thread.setName(CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + " Webhook");
            thread.setDaemon(true);

            return thread;
        });
        builder.setWait(true);

        this.client = builder.build();
    }

    public void sendReport(Player player, Player target, String reason) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("New Report", null));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Player", player.getName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Target", target.getName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Reason", reason));

        embedBuilder.setThumbnailUrl("https://visage.surgeplay.com/head/512/" + player.getUniqueId().toString());
        embedBuilder.setColor(0xFF0000);

        this.client.send(embedBuilder.build());
    }

    public void sendPunishment(Punishment punishment) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("New " + punishment.getPunishmentType().getName(), null));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Issuer", punishment.getIssuerName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Target", punishment.getTarget().toString()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Reason", punishment.getReason()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Duration", punishment.getDurationString()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Expiration", punishment.getExpirationString()));

        embedBuilder.setThumbnailUrl("https://visage.surgeplay.com/head/512/" + punishment.getTarget().toString());
        embedBuilder.setColor(0xFF0000);

        this.client.send(embedBuilder.build());
    }

    public void sendRequest(Player player, String reason) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("New Request", null));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Player", player.getName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Request", reason));

        embedBuilder.setThumbnailUrl("https://visage.surgeplay.com/head/512/" + player.getUniqueId().toString());
        embedBuilder.setColor(0xFF0000);

        this.client.send(embedBuilder.build());
    }
}
