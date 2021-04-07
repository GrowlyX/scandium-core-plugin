package com.solexgames.core.util.prompt;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.LockedState;
import com.solexgames.core.util.TotpUtil;
import com.solexgames.core.util.map.QrCodeMap;
import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class MapScannerPrompt extends StringPrompt {

    private int failures = 0;

    @Override
    public String getPromptText(ConversationContext context) {
        Player player = (Player) context.getForWhom();

        if (this.failures == 0) {
            CompletableFuture.runAsync(() -> {
                String secret = generateSecret();
                BufferedImage image = generateImage(player, secret);

                if (image != null) {
                    MapView mapView = Bukkit.getServer().createMap(player.getWorld());
                    mapView.getRenderers().forEach(mapView::removeRenderer);
                    mapView.addRenderer(new QrCodeMap(image, player.getUniqueId()));

                    ItemStack mapItem = new ItemStack(Material.MAP, 1, mapView.getId());
                    ItemMeta mapMeta = mapItem.getItemMeta();

                    mapMeta.setLore(Collections.singletonList("QR Code Map"));
                    mapItem.setItemMeta(mapMeta);

                    context.setSessionData("secret", secret);
                    context.setSessionData("map", mapItem);

                    player.sendMap(mapView);
                    player.getInventory().addItem(mapItem);
                    player.updateInventory();
                }
            });
        }

        return ChatColor.GREEN + "Scan the QR Code on the map which was provided to you and type the code you receive from your 2FA application in chat!";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        ItemStack mapItem = (ItemStack) context.getSessionData("map");
        String secret = (String) context.getSessionData("secret");

        Player player = (Player) context.getForWhom();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        player.getInventory().remove(mapItem);

        int code;

        try {
            code = Integer.parseInt(input.replace(" ", ""));
        } catch (NumberFormatException e) {
            int attempts = this.failures++;

            if (attempts >= 3) {
                player.sendMessage(ChatColor.RED + "You've exceeded the amount of attempts that you had!");
                player.sendMessage(ChatColor.RED + "The 2FA setup has been cancelled.");

                return Prompt.END_OF_CONVERSATION;
            }

            player.sendMessage("  ");
            player.sendMessage(ChatColor.RED + "I'm sorry, but the code you provided us is not valid.");
            player.sendMessage(ChatColor.RED + "Please try again and enter a new code in chat.");
            player.sendMessage(ChatColor.RED + "You currently have " + ChatColor.WHITE.toString() + (3 - attempts) + ChatColor.RED + " attempts left.");
            player.sendMessage("  ");

            return this;
        }

        if (!potPlayer.isAuthValid(secret, code)) {
            int attempts = this.failures++;

            if (attempts >= 3) {
                player.sendMessage(ChatColor.RED + "You've exceeded the amount of attempts that you had!");
                player.sendMessage(ChatColor.RED + "The 2FA setup has been cancelled.");

                return Prompt.END_OF_CONVERSATION;
            }

            player.sendMessage("  ");
            player.sendMessage(ChatColor.RED + "I'm sorry, but the code you provided us is not valid.");
            player.sendMessage(ChatColor.RED + "Please try again and enter a new code in chat.");
            player.sendMessage(ChatColor.RED + "You currently have " + ChatColor.WHITE.toString() + (3 - attempts) + ChatColor.RED + " attempts left.");
            player.sendMessage("  ");

            return this;
        }

        LockedState.release(player);

        potPlayer.setAuthSecret(secret);

        player.sendMessage(ChatColor.GREEN + "You've finished the 2FA Setup!");
        player.sendMessage(ChatColor.GREEN + "Thank you for keeping your account and our server safe!");

        return Prompt.END_OF_CONVERSATION;
    }

    private BufferedImage generateImage(Player player, String secret) {
        Escaper urlEscaper = UrlEscapers.urlFragmentEscaper();

        String issuer = CorePlugin.getInstance().getServerManager().getNetwork().getServerName();
        String url = "otpauth://totp/" + urlEscaper.escape(player.getName()) + "?secret=" + secret + "&issuer=" + urlEscaper.escape(issuer);
        String imageUrl = String.format(IMAGE_URL_FORMAT, URLEncoder.encode(url));

        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final SecureRandom SECURE_RANDOM;
    private static final Base32 BASE_32_ENCODER = new Base32();
    private static final String IMAGE_URL_FORMAT = "https://www.google.com/chart?chs=130x130&chld=M%%7C0&cht=qr&chl=%s";

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("This should never happen");
        }
    }

    private static String generateSecret() {
        byte[] secretKey = new byte[10];
        SECURE_RANDOM.nextBytes(secretKey);
        return BASE_32_ENCODER.encodeToString(secretKey);
    }
}
