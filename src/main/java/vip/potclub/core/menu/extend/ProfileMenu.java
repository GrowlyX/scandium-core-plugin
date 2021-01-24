package vip.potclub.core.menu.extend;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.menu.AbstractInventoryMenu;
import vip.potclub.core.menu.InventoryMenuItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProfileMenu extends AbstractInventoryMenu<CorePlugin> {

    private Player player;

    public ProfileMenu(Player player) {
        super("Profile", 9);
        this.player = player;
        this.update();
    }

    private void update() {
        /*
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        float practice_total_unranked_played = (float)(data.getUnrankedLosses() + data.getUnrankedWins());
        float practice_total_unranked_wl_ratio = (float)data.getUnrankedWins() / practice_total_unranked_played * 100.0F;
        float practice_total_ranked_played = (float)(data.getRankedLosses() + data.getRankedWins());
        float practice_total_ranked_wl_ratio = (float)data.getRankedWins() / practice_total_ranked_played * 100.0F;
        float practice_total_wins = (float)(data.getUnrankedWins() + data.getRankedWins());
        float practice_total_losses = (float)(data.getUnrankedLosses() + data.getRankedLosses());
        float practice_total_played = practice_total_wins + practice_total_losses;
        float practice_w_l_ratio = practice_total_wins / practice_total_played * 100.0F;
        float practice_nodebuff_played = (float)(data.getNoDebuffWins() + data.getNoDebuffLosses());
        float practice_debuff_played = (float)(data.getDebuffWins() + data.getDebuffLosses());
        float practice_build_played = (float)(data.getBuildWins() + data.getBuildLosses());
        float practice_gapple_played = (float)(data.getGappleWins() + data.getGappleLosses());
        float practice_axe_played = (float)(data.getAxeWins() + data.getAxeLosses());

        InventoryMenuItem unranked = new InventoryMenuItem(Material.IRON_SWORD);
        unranked.setDisplayName(serverType.getMainColor() + "Unranked Statistics");
        List<String> unranked_lore = new ArrayList<>();

        if (practice_total_unranked_played >= 1.0F) {
            unranked_lore.add(ChatColor.GRAY + "Unranked Wins: " + serverType.getSecondaryColor() + data.getUnrankedWins());
            unranked_lore.add(ChatColor.GRAY + "Unranked Losses: " + serverType.getSecondaryColor() + data.getUnrankedLosses());
            unranked_lore.add(ChatColor.GRAY + "");
            unranked_lore.add(ChatColor.GRAY + "Total Unranked Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_unranked_played));
            unranked_lore.add(ChatColor.GRAY + "Unranked W/L Ratio: " + serverType.getSecondaryColor() + data.getUnrankedWins() + "/" + data.getUnrankedLosses() + ChatColor.GRAY + " (" + String.format("%.0f", practice_total_unranked_wl_ratio) + "%)");
        } else {
            unranked_lore.add(ChatColor.GRAY + "");
            unranked_lore.add(ChatColor.RED + "You must play at least 1 Unranked game before");
            unranked_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        unranked.addLore(unranked_lore);

        InventoryMenuItem ranked = new InventoryMenuItem(Material.DIAMOND_SWORD);
        ranked.setDisplayName(serverType.getMainColor() + "Ranked Statistics");
        List<String> ranked_lore = new ArrayList<>();

        if (practice_total_ranked_played >= 1.0F) {
            ranked_lore.add(ChatColor.GRAY + "Ranked Wins: " + serverType.getSecondaryColor() + data.getRankedWins());
            ranked_lore.add(ChatColor.GRAY + "Ranked Losses: " + serverType.getSecondaryColor() + data.getRankedLosses());
            ranked_lore.add(ChatColor.GRAY + "");
            ranked_lore.add(ChatColor.GRAY + "Total Ranked Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_ranked_played));
            ranked_lore.add(ChatColor.GRAY + "Ranked W/L Ratio: " + serverType.getSecondaryColor() + data.getRankedWins() + "/" + data.getUnrankedLosses() + ChatColor.GRAY + " (" + String.format("%.0f", practice_total_ranked_wl_ratio) + "%)");
        } else {
            ranked_lore.add(ChatColor.GRAY + "");
            ranked_lore.add(ChatColor.RED + "You must play at least 1 Ranked game before");
            ranked_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        ranked.addLore(ranked_lore);

        InventoryMenuItem statistics = new InventoryMenuItem(Material.EMERALD);
        statistics.setDisplayName(serverType.getMainColor() + "Global Statistics");
        List<String> statistics_lore = new ArrayList<>();

        statistics_lore.add(ChatColor.GRAY + "Global ELO: " + serverType.getSecondaryColor() + data.getPracElo());
        if (practice_total_played >= 1.0F) {
            statistics_lore.add(ChatColor.GRAY + " ");
            statistics_lore.add(ChatColor.GRAY + "Unranked Wins: " + serverType.getSecondaryColor() + data.getUnrankedWins());
            statistics_lore.add(ChatColor.GRAY + "Ranked Wins: " + serverType.getSecondaryColor() + data.getRankedWins());
            statistics_lore.add(ChatColor.GRAY + "Unranked Losses: " + serverType.getSecondaryColor() + data.getUnrankedLosses());
            statistics_lore.add(ChatColor.GRAY + "Ranked Losses: " + serverType.getSecondaryColor() + data.getRankedLosses());
            statistics_lore.add(ChatColor.GRAY + "");
            statistics_lore.add(ChatColor.GRAY + "Total Unranked Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_unranked_played));
            statistics_lore.add(ChatColor.GRAY + "Total Ranked Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_ranked_played));
            statistics_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_played));
            statistics_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_wins));
            statistics_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_losses));
            statistics_lore.add(ChatColor.GRAY + " ");
            statistics_lore.add(ChatColor.GRAY + "Global W/L Ratio: " + serverType.getSecondaryColor() + String.format("%.0f", practice_total_wins) + "/" + String.format("%.0f", practice_total_losses) + ChatColor.GRAY + " (" + String.format("%.0f", practice_w_l_ratio) + "%)");
        } else {
            statistics_lore.add(ChatColor.GRAY + "");
            statistics_lore.add(ChatColor.RED + "You must play at least 1 Practice game before");
            statistics_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        statistics.addLore(statistics_lore);

        InventoryMenuItem nodebuff = new InventoryMenuItem(Material.POTION);
        nodebuff.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        nodebuff.setDurability(16421);
        nodebuff.setDisplayName(serverType.getMainColor() + "Ranked NoDebuff Statistics");
        List<String> nodebuff_lore = new ArrayList<>();

        if (practice_nodebuff_played >= 1.0F) {
            nodebuff_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_nodebuff_played));
            nodebuff_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + data.getNoDebuffWins());
            nodebuff_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + data.getNoDebuffLosses());
            nodebuff_lore.add(ChatColor.GRAY + " ");
            nodebuff_lore.add(ChatColor.GRAY + "NoDebuff Elo: " + serverType.getSecondaryColor() + data.getNoDebuffElo());
        } else {
            nodebuff_lore.add(ChatColor.GRAY + "");
            nodebuff_lore.add(ChatColor.RED + "You must play at least 1 Ranked NoDebuff game before");
            nodebuff_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        nodebuff.addLore(nodebuff_lore);

        InventoryMenuItem debuff = new InventoryMenuItem(Material.POTION);
        debuff.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        debuff.setDurability(16420);
        debuff.setDisplayName(serverType.getSecondaryColor() + "Ranked Debuff Statistics");
        List<String> debuff_lore = new ArrayList<>();

        if (practice_debuff_played >= 1.0F) {
            debuff_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_debuff_played));
            debuff_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + data.getDebuffWins());
            debuff_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + data.getDebuffLosses());
            debuff_lore.add(ChatColor.GRAY + " ");
            debuff_lore.add(ChatColor.GRAY + "Debuff Elo: " + serverType.getSecondaryColor() + data.getDebuffElo());
        } else {
            debuff_lore.add(ChatColor.GRAY + "");
            debuff_lore.add(ChatColor.RED + "You must play at least 1 Ranked Debuff game before");
            debuff_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        debuff.addLore(debuff_lore);

        InventoryMenuItem build = new InventoryMenuItem(Material.LAVA_BUCKET);
        build.setDisplayName(serverType.getMainColor() + "Ranked BuildUHC Statistics");
        List<String> build_lore = new ArrayList<>();

        if (practice_build_played >= 1.0F) {
            build_lore.add(ChatColor.GRAY + " ");
            build_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_build_played));
            build_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + data.getBuildWins());
            build_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + data.getBuildLosses());
            build_lore.add(ChatColor.GRAY + " ");
            build_lore.add(ChatColor.GRAY + "BuildUHC Elo: " + serverType.getSecondaryColor() + data.getBuildElo());
        } else {
            build_lore.add(ChatColor.GRAY + "");
            build_lore.add(ChatColor.RED + "You must play at least 1 Ranked BuildUHC game before");
            build_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        build.addLore(build_lore);

        InventoryMenuItem gapple = new InventoryMenuItem(Material.GOLDEN_APPLE);
        gapple.setDurability(1);
        gapple.setDisplayName(serverType.getMainColor() + "Ranked GApple Statistics");
        List<String> gapple_lore = new ArrayList<>();

        if (practice_build_played >= 1.0F) {
            gapple_lore.add(ChatColor.GRAY + " ");
            gapple_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_gapple_played));
            gapple_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + data.getGappleWins());
            gapple_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + data.getGappleLosses());
            gapple_lore.add(ChatColor.GRAY + " ");
            gapple_lore.add(ChatColor.GRAY + "GApple Elo: " + serverType.getSecondaryColor() + data.getGappleElo());
        } else {
            gapple_lore.add(ChatColor.GRAY + "");
            gapple_lore.add(ChatColor.RED + "You must play at least 1 Ranked GApple game before");
            gapple_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        gapple.addLore(gapple_lore);

        InventoryMenuItem axe = new InventoryMenuItem(Material.IRON_AXE);
        axe.setDisplayName(serverType.getMainColor() + "Ranked Axe Statistics");
        List<String> axe_lore = new ArrayList<>();

        if (practice_axe_played >= 1.0F) {
            axe_lore.add(ChatColor.GRAY + " ");
            axe_lore.add(ChatColor.GRAY + "Total Played: " + serverType.getSecondaryColor() + String.format("%.0f", practice_axe_played));
            axe_lore.add(ChatColor.GRAY + "Total Wins: " + serverType.getSecondaryColor() + data.getAxeElo());
            axe_lore.add(ChatColor.GRAY + "Total Losses: " + serverType.getSecondaryColor() + data.getAxeLosses());
        } else {
            axe_lore.add(ChatColor.GRAY + "");
            axe_lore.add(ChatColor.RED + "You must play at least 1 Ranked Axe game before");
            axe_lore.add(ChatColor.RED + "detailed game statistics can be displayed.");
        }

        this.inventory.setItem(13, mainItem);
        this.inventory.setItem(20, unranked.create());
        this.inventory.setItem(22, ranked.create());
        this.inventory.setItem(24, statistics.create());
        this.inventory.setItem(38, nodebuff.create());
        this.inventory.setItem(39, debuff.create());
        this.inventory.setItem(40, build.create());
        this.inventory.setItem(41, gapple.create());
        this.inventory.setItem(42, axe.create());*/
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (!topInventory.equals(this.inventory)) return;
        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);
        }
    }
}
