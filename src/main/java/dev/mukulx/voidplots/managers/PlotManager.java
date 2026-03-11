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
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlotManager {
    
    private final VoidPlots plugin;
    private final DatabaseManager databaseManager;
    private final Map<PlotId, Plot> plotCache;
    
    public PlotManager(@NotNull VoidPlots plugin, @NotNull DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.plotCache = new ConcurrentHashMap<>();
    }
    
    public void loadAllPlots() {
        List<Plot> plots = databaseManager.getAllPlots();
        for (Plot plot : plots) {
            plotCache.put(plot.getId(), plot);
        }
        plugin.getLogger().info("Loaded " + plots.size() + " plots from database");
    }
    
    @Nullable
    public Plot getPlot(@NotNull PlotId plotId) {
        Plot plot = plotCache.get(plotId);
        if (plot == null) {
            plot = databaseManager.loadPlot(plotId);
            if (plot != null) {
                plotCache.put(plotId, plot);
            }
        }
        return plot;
    }
    
    @NotNull
    public Plot getOrCreatePlot(@NotNull PlotId plotId) {
        Plot plot = getPlot(plotId);
        if (plot == null) {
            plot = new Plot(plotId, null);
            plotCache.put(plotId, plot);
        }
        return plot;
    }
    
    public void savePlot(@NotNull Plot plot) {
        plotCache.put(plot.getId(), plot);
        databaseManager.savePlot(plot);
    }
    
    public void deletePlot(@NotNull PlotId plotId) {
        plotCache.remove(plotId);
        databaseManager.deletePlot(plotId);
    }
    
    @NotNull
    public List<Plot> getPlayerPlots(@NotNull UUID playerUuid) {
        return databaseManager.getPlayerPlots(playerUuid);
    }
    
    @NotNull
    public List<Plot> getPlayerPlotsInWorld(@NotNull UUID playerUuid, @NotNull String worldName) {
        // Only count plots if they're in the configured plot world
        if (!worldName.equals(plugin.getConfigManager().getWorldName())) {
            return new ArrayList<>();
        }
        return getPlayerPlots(playerUuid);
    }
    
    @NotNull
    public List<Plot> getAllPlots() {
        return new ArrayList<>(plotCache.values());
    }
    
    @Nullable
    public PlotId getPlotIdAt(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }
        
        // Check if this is a plot world (matches configured name)
        if (!world.getName().equals(plugin.getConfigManager().getWorldName())) {
            return null;
        }
        
        int plotSize = plugin.getConfigManager().getPlotSize();
        int roadWidth = plugin.getConfigManager().getRoadWidth();
        int totalSize = plotSize + roadWidth;
        
        int x = location.getBlockX();
        int z = location.getBlockZ();
        
        // Calculate plot coordinates
        int plotX = (x >= 0) ? x / totalSize : (x - totalSize + 1) / totalSize;
        int plotZ = (z >= 0) ? z / totalSize : (z - totalSize + 1) / totalSize;
        
        // Check if location is on road
        int localX = x - (plotX * totalSize);
        int localZ = z - (plotZ * totalSize);
        
        if (localX >= plotSize || localZ >= plotSize) {
            return null; // On road
        }
        
        return new PlotId(plotX, plotZ);
    }
    
    @NotNull
    public Location getPlotHome(@NotNull PlotId plotId, @NotNull World world) {
        int plotSize = plugin.getConfigManager().getPlotSize();
        int roadWidth = plugin.getConfigManager().getRoadWidth();
        int totalSize = plotSize + roadWidth;
        
        int x = plotId.getX() * totalSize + plotSize / 2;
        int z = plotId.getZ() * totalSize + plotSize / 2;
        int y = plugin.getConfigManager().getWallHeight() + 1;
        
        return new Location(world, x + 0.5, y, z + 0.5);
    }
    
    @Nullable
    public PlotId findFreePlot() {
        int searchRadius = 100;
        
        for (int radius = 0; radius < searchRadius; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        PlotId plotId = new PlotId(x, z);
                        Plot plot = getPlot(plotId);
                        if (plot == null || !plot.isClaimed()) {
                            return plotId;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public int getPlayerPlotCount(@NotNull UUID playerUuid) {
        return getPlayerPlots(playerUuid).size();
    }
    
    public int getPlayerPlotCountInWorld(@NotNull UUID playerUuid, @NotNull String worldName) {
        return getPlayerPlotsInWorld(playerUuid, worldName).size();
    }
    
    public boolean canClaimMore(@NotNull UUID playerUuid, @NotNull String worldName) {
        int maxPlots = plugin.getConfigManager().getMaxPlotsPerPlayer();
        return getPlayerPlotCountInWorld(playerUuid, worldName) < maxPlots;
    }
}
