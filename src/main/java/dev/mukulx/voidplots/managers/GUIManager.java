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

package dev.mukulx.voidplots.managers;

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.gui.MaterialSelectorGUI;
import dev.mukulx.voidplots.gui.PlotSettingsGUI;
import dev.mukulx.voidplots.gui.RatingGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GUIManager {
    
    private final VoidPlots plugin;
    private final MessageManager messageManager;
    
    public GUIManager(@NotNull VoidPlots plugin, @NotNull MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
    }
    
    public void openPlotSettings(@NotNull Player player) {
        PlotSettingsGUI gui = new PlotSettingsGUI(plugin, messageManager);
        player.openInventory(gui.getInventory());
    }
    
    public static boolean isCustomGUI(@NotNull Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof PlotSettingsGUI || holder instanceof MaterialSelectorGUI || holder instanceof RatingGUI;
    }
}
