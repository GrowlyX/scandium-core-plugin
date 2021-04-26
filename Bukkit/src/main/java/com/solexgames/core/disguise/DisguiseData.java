package com.solexgames.core.disguise;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DisguiseData {

    private final UUID uuid;
    private final String name;

    private final String skin;
    private final String signature;

}
