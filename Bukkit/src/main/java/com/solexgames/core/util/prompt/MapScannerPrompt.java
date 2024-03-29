package com.solexgames.core.util.prompt;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.Constants;
import com.solexgames.core.util.LockedState;
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

public class MapScannerPrompt extends StringPrompt {

    private int failures = 0;

    @Override
    @SuppressWarnings("deprecation")
    public String getPromptText(ConversationContext context) {
        final Player player = (Player) context.getForWhom();

        if (this.failures == 0) {
            final String secret = generateSecret();
            final BufferedImage image = generateImage(player, secret);

            if (image != null) {
                final MapView mapView = Bukkit.getServer().createMap(player.getWorld());

                mapView.getRenderers().forEach(mapView::removeRenderer);
                mapView.addRenderer(new QrCodeMap(image, player.getUniqueId()));

                final ItemStack mapItem = new ItemStack(Material.MAP, 1, mapView.getId());
                final ItemMeta mapMeta = mapItem.getItemMeta();

                mapMeta.setDisplayName(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "2FA Code");
                mapMeta.setLore(Collections.singletonList("QR Code Map"));
                mapItem.setItemMeta(mapMeta);

                context.setSessionData("secret", secret);
                context.setSessionData("map", mapItem);

                player.sendMap(mapView);
                player.getInventory().addItem(mapItem);
                player.updateInventory();
            }
        }

        return Constants.STAFF_PREFIX + ChatColor.GREEN + "Great! " + Color.SECONDARY_COLOR + "Scan the QR Code on the map which was provided to you and type the code you receive from your 2FA application in chat!";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        final ItemStack mapItem = (ItemStack) context.getSessionData("map");
        final String secret = (String) context.getSessionData("secret");

        final Player player = (Player) context.getForWhom();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        player.getInventory().remove(mapItem);

        int code;

        try {
            code = Integer.parseInt(input.replace(" ", ""));
        } catch (NumberFormatException e) {
            return incrementAttempts(context);
        }

        if (!potPlayer.isAuthValid(secret, code)) {
            return incrementAttempts(context);
        }

        LockedState.release(player);

        potPlayer.setHasSetup2FA(true);
        potPlayer.setAuthSecret(secret);
        potPlayer.setRequiredToAuth(false);
        potPlayer.setLastAuth(System.currentTimeMillis());
        potPlayer.saveWithoutRemove();

        context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.YELLOW + "You've " + ChatColor.GREEN + "verified" + ChatColor.YELLOW + " your identity!");
        context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.YELLOW + "Thanks for helping us keep our server safe! " + ChatColor.RED + "❤");

        return Prompt.END_OF_CONVERSATION;
    }

    private Prompt incrementAttempts(ConversationContext context) {
        final int attempts = this.failures++;

        if (attempts >= 3) {
            context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You've exceeded the amount of attempts that you had!");
            context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.RED + "The 2FA setup has been cancelled.");

            return Prompt.END_OF_CONVERSATION;
        }

        context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.RED + "I'm sorry, but the code you provided us is not valid. Please try again and enter a new code in chat.");
        context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You currently have " + ChatColor.YELLOW + (3 - attempts) + ChatColor.RED + " attempts left.");

        return this;
    }

    @SuppressWarnings("deprecation")
    private BufferedImage generateImage(Player player, String secret) {
        final Escaper urlEscape = UrlEscapers.urlFragmentEscaper();

        final String issuer = CorePlugin.getInstance().getServerManager().getNetwork().getServerName();
        final String url = "otpauth://totp/" + urlEscape.escape(player.getName()) + "?secret=" + secret + "&issuer=" + urlEscape.escape(issuer);
        final String imageUrl = String.format(IMAGE_URL_FORMAT, URLEncoder.encode(url));

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
            throw new RuntimeException("Algorithm does not exist.");
        }
    }

    private static String generateSecret() {
        byte[] secretKey = new byte[10];
        SECURE_RANDOM.nextBytes(secretKey);
        return BASE_32_ENCODER.encodeToString(secretKey);
    }
}
