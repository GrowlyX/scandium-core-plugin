package com.solexgames.core.uuid;

import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UUIDCache extends ConcurrentHashMap<String, UUID> {

    @Override
    public void clear() {
        super.clear();
    }
}
