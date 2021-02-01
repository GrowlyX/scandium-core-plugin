package vip.potclub.core.player.prefixes;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Prefix {

    private UUID id;
    private String name;
    private String displayName;
    private String prefix;



}
