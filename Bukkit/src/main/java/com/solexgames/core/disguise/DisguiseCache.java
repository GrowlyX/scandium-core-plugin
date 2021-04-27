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

    public DisguiseData getByUuid(UUID uuid) {
        return this.disguiseDataList.stream()
                .filter(disguiseData -> disguiseData.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }

    public DisguiseData getByName(String name) {
        return this.disguiseDataList.stream()
                .filter(disguiseData -> disguiseData.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public void registerNewDataPair(DisguiseData disguiseData) {
        this.disguiseDataList.add(disguiseData);
    }

    public void removeDataPair(DisguiseData disguiseData) {
        this.disguiseDataList.remove(disguiseData);
    }
}
