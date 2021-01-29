package vip.potclub.core.player.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Media {

    private String twitter = "N/A";
    private String youtubeLink = "N/A";
    private String instagram = "N/A";
    private String discord = "N/A";

    private MediaData mediaData;

    public boolean hasTwitter() {
        return (this.twitter != null);
    }

    public boolean hasYoutube() {
        return (this.youtubeLink != null);
    }

    public boolean hasDiscord() {
        return (this.discord != null);
    }

    public boolean hasInstagram() {
        return (this.instagram != null);
    }

    public String getTwitterLink() {
        return "https://twitter.com/" + twitter;
    }

    public String getInstagramLink() {
        return "https://instagram.com/" + twitter;
    }
}
