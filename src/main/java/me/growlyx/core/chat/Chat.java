package me.growlyx.core.chat;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public interface Chat {

    Set<String> spammers = new HashSet();
    Set<Player> playersMuted = new HashSet();

}
