/**
 * VoidPlots - https://github.com/mukulx/VoidPlots
 *
 * Copyright (C) 2026 mukulx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package dev.mukulx.voidplots.gui;

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RatingGUI implements InventoryHolder {
    
    private final VoidPlots plugin;
    private final Plot plot;
    private final Inventory inventory;
    
    public RatingGUI(@NotNull VoidPlots plugin, @NotNull Plot plot) {
        this.plugin = plugin;
        this.plot = plot;
        this.inventory = Bukkit.createInventory(this, 27, "§6⭐ Rate This Plot ⭐");
        
        setupItems();
    }
    
    private void setupItems() {
        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§e§lPlot Rating");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click a star to rate this plot!");
            lore.add("");
            
            if (plot.getRatingCount() > 0) {
                double avg = plot.getAverageRating();
                lore.add(String.format("§7Current Rating: §e%.1f§7/5", avg));
                lore.add("§7Total Ratings: §e" + plot.getRatingCount());
            } else {
                lore.add("§7No ratings yet!");
                lore.add("§7Be the first to rate!");
            }
            
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(4, info);
        
        // Rating stars (1-5)
        for (int i = 1; i <= 5; i++) {
            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta starMeta = star.getItemMeta();
            if (starMeta != null) {
                String stars = "★".repeat(i) + "☆".repeat(5 - i);
                starMeta.setDisplayName("§6" + stars);
                
                List<String> lore = new ArrayList<>();
                lore.add("§7Rate: §e" + i + "§7/5");
                lore.add("");
                lore.add(getRatingDescription(i));
                lore.add("");
                lore.add("§eClick to rate!");
                
                starMeta.setLore(lore);
                star.setItemMeta(starMeta);
            }
            inventory.setItem(10 + i, star);
        }
        
        // Fill with glass panes
        ItemStack pane = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
        }
        
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, pane);
            }
        }
    }
    
    private String getRatingDescription(int rating) {
        return switch (rating) {
            case 1 -> "§c⚠ Poor";
            case 2 -> "§6⚠ Below Average";
            case 3 -> "§e✓ Average";
            case 4 -> "§a✓ Good";
            case 5 -> "§2★ Excellent";
            default -> "§7Unknown";
        };
    }
    
    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
    
    public void handleClick(@NotNull Player player, int slot) {
        // Check if clicked on a star (slots 11-15)
        if (slot < 11 || slot > 15) {
            return;
        }
        
        int rating = slot - 10; // Convert slot to rating (1-5)
        
        // Check if already rated
        if (plot.hasRated(player.getUniqueId())) {
            player.closeInventory();
            plugin.getMessageManager().sendMessage(player, "plot.rate.already-rated");
            return;
        }
        
        // Add rating
        plot.addRating(player.getUniqueId(), rating);
        plugin.getPlotManager().savePlot(plot);
        
        player.closeInventory();
        
        String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
        plugin.getMessageManager().sendMessage(player, "plot.rate.success",
            java.util.Map.of("rating", stars + " (" + rating + "/5)"));
        
        // Show new average
        double avg = plot.getAverageRating();
        player.sendMessage(String.format("§7Plot average: §e%.1f§7/5 §8(§7%d ratings§8)", 
            avg, plot.getRatingCount()));
    }
    
    @NotNull
    public Plot getPlot() {
        return plot;
    }
}
