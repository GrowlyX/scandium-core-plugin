package com.solexgames.core.manager;

import com.solexgames.core.serializer.DataLibrary;
import com.solexgames.core.serializer.DataSerializer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DataManager {

    private final Map<Class<? extends DataSerializer>, DataSerializer> wrapperList = new HashMap<>();

    public <T> DataSerializer<?> getWrapper(Class<T> type) {
        return DataLibrary.getInstance().getDataManager().getWrapperList().values().stream()
                .filter(abstractWrapper -> abstractWrapper.getClazz().equals(type))
                .findFirst().orElse(null);
    }

    public void registerSerializer(DataSerializer<?> serializer) {
        this.wrapperList.put(serializer.getClass(), serializer);

        System.out.println("[Serializer] Register a data serializer with " + serializer.getClazz().getSimpleName() + ".class!");
    }
}
