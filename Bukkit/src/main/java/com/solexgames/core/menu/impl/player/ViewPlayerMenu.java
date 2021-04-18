package com.solexgames.core.menu.impl.player;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.InventoryUtil;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.core.util.external.pagination.Button;
import com.solexgames.core.util.external.pagination.Menu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * @author Joelioli
 * @since 2018
 */

@RequiredArgsConstructor
public class ViewPlayerMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return Color.MAIN_COLOR + this.target.getName() + ChatColor.DARK_GRAY + "'s Inventory";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        if (player == null) {
            return buttons;
        }

        final ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(this.target.getInventory().getContents());

        for (int i = 0; i < fixedContents.length; i++) {
            final ItemStack itemStack = fixedContents[i];

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            buttons.put(i, new DisplayButton(itemStack, true));
        }

        for (int i = 0; i < this.target.getInventory().getArmorContents().length; i++) {
            ItemStack itemStack = this.target.getInventory().getArmorContents()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(39 - i, new DisplayButton(itemStack, true));
            }
        }

        int pos = 45;

        buttons.put(
                pos++,
                new HealthButton(this.target.getHealth() == 0 ? 0 : (int) Math.round(this.target.getHealth() / 2))
        );
        buttons.put(pos++, new HungerButton(this.target.getFoodLevel()));
        buttons.put(pos, new EffectsButton(this.target.getActivePotionEffects()));

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @AllArgsConstructor
    private static class HealthButton extends Button {

        private final int health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MELON)
                    .setDisplayName(Color.SECONDARY_COLOR + "Health: " + Color.MAIN_COLOR + this.health + "/10 ")
                    .setAmount(this.health == 0 ? 1 : this.health)
                    .create();
        }
    }

    @AllArgsConstructor
    private static class HungerButton extends Button {

        private final int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .setDisplayName(Color.SECONDARY_COLOR + "Hunger: " + Color.MAIN_COLOR + this.hunger + "/10 ")
                    .setAmount(this.hunger == 0 ? 1 : this.hunger)
                    .create();
        }

    }

    @AllArgsConstructor
    private static class EffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            final ItemBuilder builder = new ItemBuilder(Material.POTION).setDisplayName(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Potion Effects");

            if (this.effects.isEmpty()) {
                builder.addLore(ChatColor.GRAY + "No effects!");
            } else {
                final List<String> lore = new ArrayList<>();

                this.effects.forEach(effect -> {
                    final String name = getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
                    final String duration = " (" + millisToTimer((effect.getDuration() / 20) * 1000) + ")";

                    lore.add(Color.SECONDARY_COLOR + name + Color.MAIN_COLOR + duration);
                });

                builder.addLore(lore);
            }

            return builder.create();
        }
    }

    public static String getName(PotionEffectType potionEffectType) {
        if (potionEffectType.getName().equalsIgnoreCase("fire_resistance")) {
            return "Fire Resistance";
        } else if (potionEffectType.getName().equalsIgnoreCase("speed")) {
            return "Speed";
        } else if (potionEffectType.getName().equalsIgnoreCase("weakness")) {
            return "Weakness";
        } else if (potionEffectType.getName().equalsIgnoreCase("slowness")) {
            return "Slowness";
        } else {
            return "Unknown";
        }
    }

    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        } else {
            return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
        }
    }

    @AllArgsConstructor
    private static class DisplayButton extends Button {

        private final ItemStack itemStack;
        private final boolean cancel;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (this.itemStack == null) {
                return new ItemBuilder(XMaterial.AIR.parseMaterial()).create();
            } else {
                return this.itemStack;
            }
        }

        @Override
        public boolean shouldCancel(Player player, ClickType clickType) {
            return this.cancel;
        }
    }
}
