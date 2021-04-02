package com.solexgames.core.enums;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author GrowlyX
 * @since 4/1/2021
 */

@Getter
public enum ReportType {

    COMBAT_HACKS("Combat Hacks", Arrays.asList("KillAura", "Reach", "Aim Assist"), XMaterial.DIAMOND_SWORD, 0),
    MOVEMENT_HACKS("Movement Hacks", Arrays.asList("Speed", "Bunny Hop", "Fly"), XMaterial.RABBIT_FOOT, 0),
    VELOCITY_HACKS("Velocity Hacks", Arrays.asList("Velocity", "Reduced KB", "Anti KB"), XMaterial.SLIME_BALL, 0),
    CHAT_ABUSE("Chat Abuse", Arrays.asList("Toxicity", "Spam", "Illegal Characters"), XMaterial.PAPER, 0),
    GAME_SABOTAGE("Game Sabotage", Arrays.asList("Camping", "Running", "Stalling"), XMaterial.RED_BED, 0),
    CROSS_TEAMING("Teaming", Arrays.asList("Trucing", "Player Collision", "Group Game Sabotage"), XMaterial.STICK, 0),
    SUSPICIOUS_ACTIVITY("Suspicious Activity", Arrays.asList("DDos Threats", "Dox Threats", "Self Harm"), XMaterial.RED_DYE, 1),
    OTHER("Other", Collections.singletonList("Anything that's not listed..."), XMaterial.PAINTING, 0);

    private final String name;
    private final List<String> examples;
    private final XMaterial xMaterial;
    private final Integer durability;

    @ConstructorProperties({"name", "examples", "xMaterial", "durability"})
    ReportType(String name, List<String> examples, XMaterial xMaterial, Integer durability) {
        this.name = name;
        this.examples = examples;
        this.xMaterial = xMaterial;
        this.durability = durability;
    }
}
