package vip.potclub.core.player.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Media {

    private String twitter;
    private String youtubeLink;
    private String instagram;
    private String discord;

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
