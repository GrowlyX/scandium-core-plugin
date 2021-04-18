package com.solexgames.core.listener.custom;

import com.solexgames.core.server.NetworkServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class ServerRetrieveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final NetworkServer server;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
