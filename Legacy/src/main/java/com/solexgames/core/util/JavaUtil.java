package com.solexgames.core.util;

import com.google.common.collect.ImmutableSet;
import com.solexgames.core.CorePlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Some website
 * @since ???
 */

public final class JavaUtil {

    public static void registerCommandsIn(String packageName) {
        Collection<Class<?>> collection = JavaUtil.getClassesInPackage(CorePlugin.getInstance(), packageName);

        collection.forEach(aClass -> {
            try {
                aClass.newInstance();
            } catch (Exception ignored) {
            }
        });

        CorePlugin.getInstance().logConsole("&a[Command] &eLoaded &6" + collection.size() + "&e new commands!");
    }

    public static void registerListenersIn(String packageName) {
        Collection<Class<?>> collection = JavaUtil.getClassesInPackage(CorePlugin.getInstance(), packageName);

        collection.forEach(aClass -> {
            try {
                CorePlugin.getInstance().getServer().getPluginManager().registerEvents((Listener) aClass.newInstance(), CorePlugin.getInstance());
            } catch (Exception ignored) {
            }
        });

        CorePlugin.getInstance().logConsole("&a[Listener] &eLoaded &6" + collection.size() + "&e new listeners!");
    }

    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }
}
