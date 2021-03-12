package com.solexgames.core.player.media;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Media {

    private String twitter = "N/A";
    private String youtubeLink = "N/A";
    private String instagram = "N/A";
    private String discord = "N/A";

    private MediaData mediaData;

    public Media() {
        this.mediaData = new MediaData();
    }
}
