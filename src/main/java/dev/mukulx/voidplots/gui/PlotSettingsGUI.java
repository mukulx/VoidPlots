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
import java.util.List;
import java.util.Map;

public class PlotSettingsGUI implements InventoryHolder {
    
    private final VoidPlots plugin;
    private final MessageManager messageManager;
    private final Inventory inventory;
    
    public PlotSettingsGUI(@NotNull VoidPlots plugin, @NotNull MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        
        String title = messageManager.getMessage("gui.settings.title");
        this.inventory = Bukkit.createInventory(this, 54, title != null ? title : "Plot Settings");
        
        setupItems();
    }
    
    private void setupItems() {
        // Border block selector
        ItemStack borderBlock = new ItemStack(plugin.getConfigManager().getBorderBlocks().get(0));
        ItemMeta borderMeta = borderBlock.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName("§e§lBorder Block");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §f" + borderBlock.getType().name());
            lore.add("");
            lore.add("§7Click to change border block");
            lore.add("§7This is the wall around each plot");
            borderMeta.setLore(lore);
            borderBlock.setItemMeta(borderMeta);
        }
        inventory.setItem(10, borderBlock);
        
        // Road block selector
        ItemStack roadBlock = new ItemStack(plugin.getConfigManager().getRoadBlocks().get(0));
        ItemMeta roadMeta = roadBlock.getItemMeta();
        if (roadMeta != null) {
            roadMeta.setDisplayName("§e§lRoad Block");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §f" + roadBlock.getType().name());
            lore.add("");
            lore.add("§7Click to change road block");
            lore.add("§7This is the path between plots");
            roadMeta.setLore(lore);
            roadBlock.setItemMeta(roadMeta);
        }
        inventory.setItem(12, roadBlock);
        
        // Fill block selector
        ItemStack fillBlock = new ItemStack(plugin.getConfigManager().getFillBlock());
        ItemMeta fillMeta = fillBlock.getItemMeta();
        if (fillMeta != null) {
            fillMeta.setDisplayName("§e§lFill Block");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §f" + fillBlock.getType().name());
            lore.add("");
            lore.add("§7Click to change fill block");
            lore.add("§7This is the top layer of plots");
            fillMeta.setLore(lore);
            fillBlock.setItemMeta(fillMeta);
        }
        inventory.setItem(14, fillBlock);
        
        // Plot size info
        ItemStack sizeInfo = new ItemStack(Material.PAPER);
        ItemMeta sizeMeta = sizeInfo.getItemMeta();
        if (sizeMeta != null) {
            sizeMeta.setDisplayName("§e§lPlot Size");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §f" + plugin.getConfigManager().getPlotSize() + "x" + plugin.getConfigManager().getPlotSize());
            lore.add("");
            lore.add("§7Left-click: §a+5");
            lore.add("§7Right-click: §c-5");
            lore.add("§7Shift-click: §e±1");
            sizeMeta.setLore(lore);
            sizeInfo.setItemMeta(sizeMeta);
        }
        inventory.setItem(28, sizeInfo);
        
        // Road width info
        ItemStack roadInfo = new ItemStack(Material.COMPASS);
        ItemMeta roadInfoMeta = roadInfo.getItemMeta();
        if (roadInfoMeta != null) {
            roadInfoMeta.setDisplayName("§e§lRoad Width");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §f" + plugin.getConfigManager().getRoadWidth() + " blocks");
            lore.add("");
            lore.add("§7Left-click: §a+1");
            lore.add("§7Right-click: §c-1");
            roadInfoMeta.setLore(lore);
            roadInfo.setItemMeta(roadInfoMeta);
        }
        inventory.setItem(30, roadInfo);
        
        // Wall height info
        ItemStack heightInfo = new ItemStack(Material.LADDER);
        ItemMeta heightMeta = heightInfo.getItemMeta();
        if (heightMeta != null) {
            heightMeta.setDisplayName("§e§lWall Height");
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §fY=" + plugin.getConfigManager().getWallHeight());
            lore.add("");
            lore.add("§7Left-click: §a+5");
            lore.add("§7Right-click: §c-5");
            lore.add("§7Shift-click: §e±1");
            heightMeta.setLore(lore);
            heightInfo.setItemMeta(heightMeta);
        }
        inventory.setItem(32, heightInfo);
        
        // Save button
        ItemStack saveButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta saveMeta = saveButton.getItemMeta();
        if (saveMeta != null) {
            saveMeta.setDisplayName("§a§lSave & Apply Settings");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to save changes to config.yml");
            lore.add("");
            lore.add("§c§lWARNING:");
            lore.add("§7New chunks will use new settings");
            lore.add("§7Existing chunks won't change");
            saveMeta.setLore(lore);
            saveButton.setItemMeta(saveMeta);
        }
        inventory.setItem(49, saveButton);
        
        // Info item
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§b§lPlot Settings");
            List<String> lore = new ArrayList<>();
            lore.add("§7Configure your plot world");
            lore.add("§7generation settings here.");
            lore.add("");
            lore.add("§7Changes only affect new chunks!");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(4, info);
        
        // Fill glass panes
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
        }
        
        for (int i = 0; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, pane);
            }
        }
    }
    
    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
    
    public void handleClick(@NotNull Player player, int slot, boolean leftClick, boolean shift) {
        switch (slot) {
            case 10: // Border block
                openMaterialSelector(player, "border");
                break;
            case 12: // Road block
                openMaterialSelector(player, "road");
                break;
            case 14: // Fill block
                openMaterialSelector(player, "fill");
                break;
            case 28: // Plot size
                adjustPlotSize(player, leftClick, shift);
                break;
            case 30: // Road width
                adjustRoadWidth(player, leftClick, shift);
                break;
            case 32: // Wall height
                adjustWallHeight(player, leftClick, shift);
                break;
            case 49: // Save
                saveSettings(player);
                break;
        }
    }
    
    private void openMaterialSelector(@NotNull Player player, @NotNull String type) {
        MaterialSelectorGUI selector = new MaterialSelectorGUI(plugin, messageManager, type);
        player.openInventory(selector.getInventory());
    }
    
    private void adjustPlotSize(@NotNull Player player, boolean increase, boolean shift) {
        int current = plugin.getConfigManager().getPlotSize();
        int change = shift ? 1 : 5;
        int newSize = increase ? current + change : current - change;
        
        if (newSize < 10) newSize = 10;
        if (newSize > 200) newSize = 200;
        
        plugin.getConfig().set("plot.size", newSize);
        player.sendMessage("§aPlot size set to: §e" + newSize + "x" + newSize);
        
        // Refresh GUI
        setupItems();
    }
    
    private void adjustRoadWidth(@NotNull Player player, boolean increase, boolean shift) {
        int current = plugin.getConfigManager().getRoadWidth();
        int newWidth = increase ? current + 1 : current - 1;
        
        if (newWidth < 3) newWidth = 3;
        if (newWidth > 20) newWidth = 20;
        
        plugin.getConfig().set("plot.road-width", newWidth);
        player.sendMessage("§aRoad width set to: §e" + newWidth + " blocks");
        
        // Refresh GUI
        setupItems();
    }
    
    private void adjustWallHeight(@NotNull Player player, boolean increase, boolean shift) {
        int current = plugin.getConfigManager().getWallHeight();
        int change = shift ? 1 : 5;
        int newHeight = increase ? current + change : current - change;
        
        if (newHeight < 50) newHeight = 50;
        if (newHeight > 250) newHeight = 250;
        
        plugin.getConfig().set("plot.wall-height", newHeight);
        player.sendMessage("§aWall height set to: §eY=" + newHeight);
        
        // Refresh GUI
        setupItems();
    }
    
    private void saveSettings(@NotNull Player player) {
        plugin.saveConfig();
        plugin.getConfigManager().loadConfig();
        player.sendMessage("§a§lSettings saved to config.yml!");
        player.sendMessage("§7New chunks will use these settings.");
        player.closeInventory();
    }
}
