package com.solexgames.core.listener.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author GrowlyX
 * @since 7/15/2021
 */

@Getter
@RequiredArgsConstructor
public class PlayerFreezeEvent extends Event {

    private static final HandlerList HANDLERS  = new HandlerList();

    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
