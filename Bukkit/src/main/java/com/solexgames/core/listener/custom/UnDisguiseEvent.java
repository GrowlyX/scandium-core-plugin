package com.solexgames.core.listener.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author puugz
 * @since 19/6/2021 16:22
 */
@Getter
@RequiredArgsConstructor
public class UnDisguiseEvent extends Event {

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
