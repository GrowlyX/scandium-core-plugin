package me.growlyx.core.utils.configurations;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;

import java.util.List;
import java.util.stream.Collectors;

public class Messages {

    public static String string(String text) {

        String output = text;
        return Core.instance.m.getConfig().getString(output);

    }

    public static boolean aboolean(String text) {

        String output = text;
        return Core.instance.m.getConfig().getBoolean(output);

    }

    public static int integer(String text) {

        String output = text;
        return Core.instance.m.getConfig().getInt(output);

    }

    public static List<Integer> integerList(String text) {

        String output = text;
        return Core.instance.m.getConfig().getIntegerList(output);

    }

    public static List<Boolean> booleanList(String text) {

        String output = text;
        return Core.instance.m.getConfig().getBooleanList(output);

    }

}
