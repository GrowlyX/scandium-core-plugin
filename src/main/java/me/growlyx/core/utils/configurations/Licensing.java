package me.growlyx.core.utils.configurations;

import me.growlyx.core.Core;

import java.util.List;

public class Licensing {

    public static String string(String text) {

        String output = text;
        return Core.instance.l.getConfig().getString(output);

    }

    public static List<String> list(String text) {

        String output = text;
        return Core.instance.l.getConfig().getStringList(output);

    }

    public static boolean aboolean(String text) {

        String output = text;
        return Core.instance.l.getConfig().getBoolean(output);

    }

    public static int integer(String text) {

        String output = text;
        return Core.instance.l.getConfig().getInt(output);

    }

    public static List<Integer> integerList(String text) {

        String output = text;
        return Core.instance.l.getConfig().getIntegerList(output);

    }

    public static List<Boolean> booleanList(String text) {

        String output = text;
        return Core.instance.l.getConfig().getBooleanList(output);

    }

}
