package com.solexgames.core.command.impl.auth;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.AuthUtil;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.map.QrCodeMap;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class AuthCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.2fa")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        CompletableFuture.runAsync(() -> {
            if (!potPlayer.isHasSetup2FA()) {
                GoogleAuthenticator authenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key = authenticator.createCredentials();

                try {
                    URL url = new URL(AuthUtil.getQrImageURL(key.getKey()));
                    BufferedImage image = ImageIO.read(url);

                    ItemBuilder item = new ItemBuilder(Material.MAP)
                            .setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "QR Code");

                    MapView view = Bukkit.createMap(player.getWorld());
                    view.getRenderers().clear();
                    view.addRenderer(new QrCodeMap(image));

                    item.setDurability(view.getId());

                    potPlayer.setLastItem(player.getItemInHand());
                    potPlayer.setLastItemSlot(AuthUtil.getHotbarSlotOfItem(player.getItemInHand(), player));
                    player.setItemInHand(item.create());

                    potPlayer.setVerify(true);
                    potPlayer.setHasSetup2FA(true);
                    potPlayer.setKey(key.getKey());
                    potPlayer.saveWithoutRemove();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            if (!potPlayer.isVerify()) {
                player.sendMessage(ChatColor.RED + "You've already verified yourself.");
                return;
            }

            if (args.length == 0) {
                player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <code>.");
                return;
            }

            int code;

            try {
                code = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "That's not a valid integer.");
                return;
            }

            if (!AuthUtil.checkCode(potPlayer.getKey(), code)) {
                player.sendMessage(ChatColor.RED + "That's not a valid auth code.");
                return;
            }

            if (potPlayer.isSetupSecurity()) {
                if (potPlayer.getLastItemSlot() != -1) {
                    player.getInventory().setItem(potPlayer.getLastItemSlot(), potPlayer.getLastItem());
                }
            }

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> AuthUtil.removeQrMapFromInventory(player));

            potPlayer.setSetupSecurity(false);
            potPlayer.setVerify(false);

            player.sendMessage(ChatColor.GREEN + "You've successfully verified yourself.");
            player.sendMessage(ChatColor.GRAY + "Thanks for keeping your account secure!");

            long next = AuthUtil.parseTime("5h");

            potPlayer.setNextAuth(next + System.currentTimeMillis());
            potPlayer.saveWithoutRemove();
        });

        return false;
    }
}
