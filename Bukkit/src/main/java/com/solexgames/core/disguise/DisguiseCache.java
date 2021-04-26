package com.solexgames.core.disguise;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor
public class DisguiseCache {

    @Getter
    private final List<DisguiseData> disguiseDataList = new ArrayList<>();

    public DisguiseData getRandomData() {
        return this.disguiseDataList.get(ThreadLocalRandom.current().nextInt(this.disguiseDataList.size()));
    }

    public void registerNewDataPair(UUID uuid, String name, String skin, String signature) {
        final DisguiseData disguiseData = new DisguiseData(uuid, name, skin, signature);

        this.disguiseDataList.add(disguiseData);
    }

    public void registerNewDataPair(DisguiseData disguiseData) {
        this.disguiseDataList.add(disguiseData);
    }

    public void removeDataPair(DisguiseData disguiseData) {
        this.disguiseDataList.remove(disguiseData);
    }
}
