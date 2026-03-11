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
import dev.mukulx.voidplots.managers.NotificationManager;
import dev.mukulx.voidplots.managers.PlotManager;
import dev.mukulx.voidplots.managers.ConfigManager;
import dev.mukulx.voidplots.managers.MessageManager;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerJoinListener implements Listener {
    
    private final VoidPlots plugin;
    private final PlotManager plotManager;
    private final NotificationManager notificationManager;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    
    public PlayerJoinListener(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
        this.notificationManager = plugin.getNotificationManager();
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }
    
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Auto-claim on first join
        if (configManager.isAutoClaimOnFirstJoin() && !player.hasPlayedBefore()) {
            handleAutoClaimFirstJoin(player);
        }
        
        // Notify plot owners that their trusted player is online
        notifyPlotOwners(player);
    }
    
    private void handleAutoClaimFirstJoin(@NotNull Player player) {
        String worldName = configManager.getWorldName();
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            return; // Plot world doesn't exist
        }
        
        // Check if player can claim more plots
        if (!plotManager.canClaimMore(player.getUniqueId(), worldName)) {
            return;
        }
        
        // Find a free plot
        PlotId freePlotId = plotManager.findFreePlot();
        if (freePlotId == null) {
            messageManager.sendMessage(player, "plot.auto.no-free-plots");
            return;
        }
        
        // Claim the plot
        Plot plot = plotManager.getOrCreatePlot(freePlotId);
        plot.setOwner(player.getUniqueId());
        plotManager.savePlot(plot);
        
        // Teleport player to the plot
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.teleport(plotManager.getPlotHome(freePlotId, world));
            messageManager.sendMessage(player, "plot.auto.success",
                java.util.Map.of("plot", freePlotId.toString()));
        });
    }
    
    private void notifyPlotOwners(@NotNull Player player) {
        List<Plot> allPlots = plotManager.getAllPlots();
        
        for (Plot plot : allPlots) {
            if (plot.isTrusted(player.getUniqueId())) {
                notificationManager.notifyTrustedPlayerOnline(plot, player);
            }
        }
    }
}
