package com.solexgames.core.util.external.pagination.extend;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.pagination.PaginatedMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class StaffViewPaginatedMenu extends PaginatedMenu {

    private final Player player;

    public StaffViewPaginatedMenu(Player player) {
        super(9);
        this.player = player;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Online Staff";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        AtomicInteger i = new AtomicInteger(0);
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> buttons.put(i.getAndIncrement(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(XMaterial.SKELETON_SKULL.parseMaterial())
                                .setOwner(potPlayer.getName())
                                .setDisplayName(Color.translate(potPlayer.getActiveGrant().getRank().getPrefix() + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName()))
                                .addLore(
                                        network.getMainColor() + "&m------------------------",
                                        network.getSecondaryColor() + "Mod-Mode: " + (potPlayer.isStaffMode() ? "&aEnabled" : "&cDisabled"),
                                        network.getSecondaryColor() + "Vanish: " + (potPlayer.isVanished() ? "&aEnabled" : "&cDisabled"),
                                        network.getSecondaryColor() + "Discord: " + (potPlayer.getSyncDiscord() != null ? network.getMainColor() + potPlayer.getSyncDiscord() : "&cNot Synced"),
                                        "",
                                        "&aClick to teleport to " + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName() + "&a!",
                                        network.getMainColor() + "&m------------------------"
                                )
                                .create();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        String display = ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName());

                        if (Bukkit.getPlayer(display) != null) {
                            Player clickedUser = Bukkit.getPlayer(display);
                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(clickedUser);

                            player.teleport(clickedUser.getLocation());
                            player.sendMessage(Color.translate("&aYou have been teleported to " + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName() + "&a!"));
                        }
                    }
                }));

        return buttons;
    }
}
