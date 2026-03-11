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
import dev.mukulx.voidplots.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaterialSelectorGUI implements InventoryHolder {
    
    private final VoidPlots plugin;
    private final MessageManager messageManager;
    private final Inventory inventory;
    private final String type;
    
    private static final List<Material> COMMON_BLOCKS = Arrays.asList(
        Material.GRASS_BLOCK, Material.DIRT, Material.STONE, Material.COBBLESTONE,
        Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS, Material.JUNGLE_PLANKS,
        Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS,
        Material.CRACKED_STONE_BRICKS, Material.BRICKS, Material.SANDSTONE, Material.RED_SANDSTONE,
        Material.QUARTZ_BLOCK, Material.SMOOTH_QUARTZ, Material.PRISMARINE, Material.DARK_PRISMARINE,
        Material.TERRACOTTA, Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
        Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA,
        Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA,
        Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA,
        Material.BLACK_TERRACOTTA, Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE,
        Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE,
        Material.PINK_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE,
        Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.GREEN_CONCRETE,
        Material.RED_CONCRETE, Material.BLACK_CONCRETE, Material.DEEPSLATE, Material.POLISHED_DEEPSLATE,
        Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.ANDESITE, Material.POLISHED_ANDESITE,
        Material.DIORITE, Material.POLISHED_DIORITE, Material.GRANITE, Material.POLISHED_GRANITE
    );
    
    public MaterialSelectorGUI(@NotNull VoidPlots plugin, @NotNull MessageManager messageManager, @NotNull String type) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.type = type;
        
        String title = "§eSelect " + type.substring(0, 1).toUpperCase() + type.substring(1) + " Block";
        this.inventory = Bukkit.createInventory(this, 54, title);
        
        setupItems();
    }
    
    private void setupItems() {
        int slot = 0;
        for (Material material : COMMON_BLOCKS) {
            if (slot >= 45) break;
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + formatName(material.name()));
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to select this block");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(slot++, item);
        }
        
        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§cBack to Settings");
            back.setItemMeta(backMeta);
        }
        inventory.setItem(49, back);
    }
    
    private String formatName(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return formatted.toString();
    }
    
    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
    
    public void handleClick(@NotNull Player player, int slot) {
        if (slot == 49) {
            // Back button
            PlotSettingsGUI settingsGUI = new PlotSettingsGUI(plugin, messageManager);
            player.openInventory(settingsGUI.getInventory());
            return;
        }
        
        ItemStack clicked = inventory.getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        Material selected = clicked.getType();
        
        // Update config based on type
        switch (type) {
            case "border":
                plugin.getConfig().set("plot.border-blocks", Arrays.asList(selected.name()));
                player.sendMessage("§aBorder block set to: §e" + formatName(selected.name()));
                break;
            case "road":
                plugin.getConfig().set("plot.road-blocks", Arrays.asList(selected.name()));
                player.sendMessage("§aRoad block set to: §e" + formatName(selected.name()));
                break;
            case "fill":
                plugin.getConfig().set("plot.fill-block", selected.name());
                player.sendMessage("§aFill block set to: §e" + formatName(selected.name()));
                break;
        }
        
        // Go back to settings
        PlotSettingsGUI settingsGUI = new PlotSettingsGUI(plugin, messageManager);
        player.openInventory(settingsGUI.getInventory());
    }
    
    @NotNull
    public String getType() {
        return type;
    }
}
