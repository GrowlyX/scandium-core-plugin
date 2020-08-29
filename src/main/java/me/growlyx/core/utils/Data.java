package me.growlyx.core.utils;

import me.growlyx.core.Core;

import java.util.List;

public class Data {

    public static String string(String text) {

        String output = text;
        return Core.instance.getConfig().getString(output);

    }

    public static List<String> list(String text) {

        String output = text;
        return Core.instance.getConfig().getStringList(output);

    }

    public static boolean aboolean(String text) {

        String output = text;
        return Core.instance.getConfig().getBoolean(output);

    }

    public static int integer(String text) {

        String output = text;
        return Core.instance.getConfig().getInt(output);

    }

    public static List<Integer> integerList(String text) {

        String output = text;
        return Core.instance.getConfig().getIntegerList(output);

    }

    public static List<Boolean> booleanList(String text) {

        String output = text;
        return Core.instance.getConfig().getBooleanList(output);

    }

}
