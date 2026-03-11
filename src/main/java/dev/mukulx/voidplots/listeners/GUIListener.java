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

package dev.mukulx.voidplots.listeners;

import dev.mukulx.voidplots.gui.MaterialSelectorGUI;
import dev.mukulx.voidplots.gui.PlotSettingsGUI;
import dev.mukulx.voidplots.gui.RatingGUI;
import dev.mukulx.voidplots.managers.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof PlotSettingsGUI) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            PlotSettingsGUI gui = (PlotSettingsGUI) holder;
            boolean leftClick = event.getClick() == ClickType.LEFT;
            boolean shift = event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT;
            
            gui.handleClick(player, event.getRawSlot(), leftClick, shift);
        } else if (holder instanceof MaterialSelectorGUI) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            MaterialSelectorGUI gui = (MaterialSelectorGUI) holder;
            gui.handleClick(player, event.getRawSlot());
        } else if (holder instanceof RatingGUI) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            RatingGUI gui = (RatingGUI) holder;
            gui.handleClick(player, event.getRawSlot());
        }
    }
    
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof PlotSettingsGUI || holder instanceof MaterialSelectorGUI || holder instanceof RatingGUI) {
            event.setCancelled(true);
        }
    }
}
