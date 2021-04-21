package com.solexgames.core.manager;

import com.solexgames.core.serializer.DataLibrary;
import com.solexgames.core.serializer.DataSerializer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DataManager {

    private final List<DataSerializer<?>> wrapperList = new ArrayList<>();

    public <T> DataSerializer<?> getWrapper(Class<T> type) {
        return DataLibrary.getInstance().getDataManager().getWrapperList().stream()
                .filter(abstractWrapper -> abstractWrapper.getClazz().equals(type))
                .findFirst().orElse(null);
    }

    public void registerSerializer(DataSerializer<?> serializer) {
        this.wrapperList.add(serializer);

        System.out.println("[Serializer] Register a data serializer with " + serializer.getClazz().getSimpleName() + ".class!");
    }
}
