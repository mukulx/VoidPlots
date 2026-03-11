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

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PlotProtectionListener implements Listener {
    
    private final VoidPlots plugin;
    
    public PlotProtectionListener(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("voidplots.admin")) {
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getBlock().getLocation());
        if (plotId == null) {
            event.setCancelled(true);
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.hasAccess(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§cYou don't have permission to break blocks here!");
        }
    }
    
    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("voidplots.admin")) {
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getBlock().getLocation());
        if (plotId == null) {
            event.setCancelled(true);
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.hasAccess(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§cYou don't have permission to place blocks here!");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (player.hasPermission("voidplots.admin")) {
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getClickedBlock().getLocation());
        if (plotId == null) {
            event.setCancelled(true);
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.hasAccess(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
