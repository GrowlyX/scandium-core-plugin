package com.solexgames.core.serializer;

/**
 * @author GrowlyX
 * @since 4/20/2021
 */

public abstract class DataSerializer<T> {

    /**
     * Get the class
     */
    public abstract Class<T> getClazz();

    /**
     * Fetches {@param T} from a class from json
     *
     * @param json Json serialized string
     * @return Returns a {@param T} type
     */
    public T fromJson(String json) {
        return DataLibrary.getInstance().getGson().fromJson(json, this.getClazz());
    }

    /**
     * Gets a serialized json version of an object that is {@param T} type
     *
     * @param object the object to serialize
     * @throws RuntimeException for the clazz not matching the {@param T} type
     *
     * @return a String serialized json
     */
    public String getJson(Object object) {
        if (!object.getClass().equals(this.getClazz())) {
            throw new RuntimeException("Class not matching type parameter T");
        }

        return DataLibrary.getInstance().getGson().toJson(object);
    }
}
