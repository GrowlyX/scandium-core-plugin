package com.solexgames.core.util.builder;

import com.solexgames.core.CorePlugin;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 4/12/2021
 */

@Data
@RequiredArgsConstructor
public class PostBuilder {

    private final UUID uuid;
    private final CommandSender sender;

    private String title;
    private String deck;
    private String formatTime;

    private Date creation;
    private long milli;

    public Document getDocument() {
        Document document = new Document("_id", this.uuid);

        document.put("uuid", this.uuid.toString());

        if (this.sender instanceof Player) {
            Player player = (Player) this.sender;

            document.put("playerName", player.getName());
            document.put("playerUuid", player.getUniqueId().toString());
        } else {
            document.put("playerName", "Console");
            document.put("playerUuid", "Console");
        }

        document.put("announcementName", this.title);
        document.put("announcementContent", this.deck);

        document.put("time", this.formatTime);
        document.put("rawDate", this.milli);

        return document;
    }
}
