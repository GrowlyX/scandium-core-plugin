package me.growlyx.core;

import me.growlyx.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class CoreLicense {

    private String licenseKey;
    private Plugin plugin;
    private String validationServer;
    private LogType logType = LogType.NORMAL;
    private String securityKey = "010110111100000000001100101110000001";
    private boolean debug = false;

    public CoreLicense(String licenseKey, String validationServer, Plugin plugin) {
        this.licenseKey = licenseKey;
        this.plugin = plugin;
        this.validationServer = validationServer;
    }

    public CoreLicense setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
        return this;
    }

    public CoreLicense setConsoleLog(LogType logType) {
        this.logType = logType;
        return this;
    }

    public CoreLicense debug() {
        debug = true;
        return this;
    }

    public boolean register() {

        log(0, "--------------------------------------");
        log(0, "Core - Anti-Piracy System");
        log(0, "--------------------------------------");
        log(0, "Connecting to US-EAST Licence Servers...");
        log(0, "");
        ValidationType vt = isValid();

        if (vt == ValidationType.VALID) {

            log(1, "Status: Licensed! Continuing Startup..");
            log(1, "");
            log(1, "Thank you for purchasing Core!");
            log(1, "If you have any questions, please");
            log(1, "create a new ticket on https://discord.gg/cNKdrya");
            log(1, "--------------------------------------");
            return true;

        } else {
            log(1, "Invalid License!");
            log(1, "");
            log(1, "Status: Licensing Failed! Disabling...");
            log(1, "Reason: " + vt.toString());
            log(1, "--------------------------------------");

            Bukkit.getScheduler().cancelTasks(plugin);
            Bukkit.getPluginManager().disablePlugin(plugin);

            return false;
        }
    }

    public boolean isValidSimple() {
        return (isValid() == ValidationType.VALID);
    }

    private String requestServer(String v1, String v2) throws IOException {
        URL url = new URL(validationServer + "?v1=" + v1 + "&v2=" + v2 + "&pl=" + plugin.getName());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        if (debug) {
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    public ValidationType isValid() {
        String rand = toBinary(UUID.randomUUID().toString());
        String sKey = toBinary(securityKey);
        String key = toBinary(licenseKey);

        try {
            String response = requestServer(xor(rand, sKey), xor(rand, key));

            if (response.startsWith("<")) {

                log(1, CC.translate("&7&m--------------------------------------"));
                log(1, CC.translate("&6The License-Server returned an invalid response!"));
                log(1, CC.translate("&6Please contact the developer for more information."));
                log(1, CC.translate("&7&m--------------------------------------"));

                return ValidationType.PAGE_ERROR;
            }

            try {
                return ValidationType.valueOf(response);
            } catch (IllegalArgumentException exc) {
                String respRand = xor(xor(response, key), sKey);
                if (rand.substring(0, respRand.length()).equals(respRand))
                    return ValidationType.VALID;
                else
                    return ValidationType.WRONG_RESPONSE;
            }
        } catch (IOException e) {
            if (debug)
                e.printStackTrace();
            return ValidationType.PAGE_ERROR;
        }
    }

    //
    // Cryptographic
    //

    private static String xor(String s1, String s2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < (Math.min(s1.length(), s2.length())); i++)
            result.append(Byte.parseByte("" + s1.charAt(i)) ^ Byte.parseByte(s2.charAt(i) + ""));
        return result.toString();
    }

    //
    // Enums
    //

    public enum LogType {
        NORMAL, LOW, NONE;
    }

    public enum ValidationType {
        WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID;
    }

    //
    // Binary methods
    //

    private String toBinary(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    //
    // Console-Log
    //

    private void log(int type, String message) {
        if (logType == LogType.NONE || (logType == LogType.LOW && type == 0))
            return;
        System.out.println(message);
    }
}
