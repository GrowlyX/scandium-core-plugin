package me.growlyx.core.utils.configurations;

import me.growlyx.core.Core;

import java.util.List;

public class Tags {

    public static String string(String text) {

        String output = text;
        return Core.instance.t.getConfig().getString(output);

    }

    public static List<String> list(String text) {

        String output = text;
        return Core.instance.t.getConfig().getStringList(output);

    }

    public static boolean aboolean(String text) {

        String output = text;
        return Core.instance.t.getConfig().getBoolean(output);

    }

    public static int integer(String text) {

        String output = text;
        return Core.instance.t.getConfig().getInt(output);

    }

    public static List<Integer> integerList(String text) {

        String output = text;
        return Core.instance.t.getConfig().getIntegerList(output);

    }

    public static List<Boolean> booleanList(String text) {

        String output = text;
        return Core.instance.t.getConfig().getBooleanList(output);

    }

}
