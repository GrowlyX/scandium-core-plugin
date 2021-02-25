package com.solexgames.core.potion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PotionMessageType {

    NORMAL("NORMAL"),
    NERDY("NERDY"),
    KITCHEN("KITCHEN"),
    GAMER("GAMER"),
    TOXIC("TOXIC");

    public String typeName;

}
