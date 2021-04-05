package com.solexgames.core.enums;

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

    private String typeName;

}
