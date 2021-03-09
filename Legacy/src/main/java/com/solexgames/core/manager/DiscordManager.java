package com.solexgames.core.manager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;

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

        WebhookClientBuilder builder = new WebhookClientBuilder(CorePlugin.getInstance().getConfig().getString("discord.webhook"));

        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);

            thread.setName(CorePlugin.getInstance().getServerManager().getNetwork().getServerName());
            thread.setDaemon(true);

            return thread;
        });
        builder.setWait(true);

        this.client = builder.build();
    }


}
