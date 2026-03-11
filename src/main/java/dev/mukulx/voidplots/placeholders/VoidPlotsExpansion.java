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

package dev.mukulx.voidplots.placeholders;

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.managers.PlotManager;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VoidPlotsExpansion extends PlaceholderExpansion {
    
    private final VoidPlots plugin;
    private final PlotManager plotManager;
    
    public VoidPlotsExpansion(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }
    
    @Override
    @NotNull
    public String getIdentifier() {
        return "voidplots";
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "MukulX";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    @Nullable
    public String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        // %voidplots_plot_count%
        if (params.equalsIgnoreCase("plot_count")) {
            List<Plot> plots = plotManager.getPlayerPlots(player.getUniqueId());
            return String.valueOf(plots.size());
        }
        
        // %voidplots_has_plot%
        if (params.equalsIgnoreCase("has_plot")) {
            List<Plot> plots = plotManager.getPlayerPlots(player.getUniqueId());
            return plots.isEmpty() ? "false" : "true";
        }
        
        // %voidplots_max_plots%
        if (params.equalsIgnoreCase("max_plots")) {
            return String.valueOf(plugin.getConfigManager().getMaxPlotsPerPlayer());
        }
        
        // Placeholders that require the player to be online
        if (!player.isOnline()) {
            return null;
        }
        
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return null;
        }
        
        PlotId currentPlotId = plotManager.getPlotIdAt(onlinePlayer.getLocation());
        Plot currentPlot = currentPlotId != null ? plotManager.getPlot(currentPlotId) : null;
        
        // %voidplots_current_plot%
        if (params.equalsIgnoreCase("current_plot")) {
            return currentPlot != null ? currentPlot.getId().toString() : "none";
        }
        
        // %voidplots_plot_owner%
        if (params.equalsIgnoreCase("plot_owner")) {
            if (currentPlot == null || currentPlot.getOwner() == null) {
                return "none";
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(currentPlot.getOwner());
            return owner.getName() != null ? owner.getName() : "Unknown";
        }
        
        // %voidplots_plot_rating%
        if (params.equalsIgnoreCase("plot_rating")) {
            if (currentPlot == null) {
                return "0.0";
            }
            return String.format("%.1f", currentPlot.getAverageRating());
        }
        
        // %voidplots_plot_rating_count%
        if (params.equalsIgnoreCase("plot_rating_count")) {
            if (currentPlot == null) {
                return "0";
            }
            return String.valueOf(currentPlot.getRatingCount());
        }
        
        // %voidplots_plot_trusted_count%
        if (params.equalsIgnoreCase("plot_trusted_count")) {
            if (currentPlot == null) {
                return "0";
            }
            return String.valueOf(currentPlot.getTrusted().size());
        }
        
        // %voidplots_is_plot_owner%
        if (params.equalsIgnoreCase("is_plot_owner")) {
            if (currentPlot == null) {
                return "false";
            }
            return currentPlot.isOwner(onlinePlayer.getUniqueId()) ? "true" : "false";
        }
        
        // %voidplots_is_trusted%
        if (params.equalsIgnoreCase("is_trusted")) {
            if (currentPlot == null) {
                return "false";
            }
            return currentPlot.isTrusted(onlinePlayer.getUniqueId()) ? "true" : "false";
        }
        
        // %voidplots_has_access%
        if (params.equalsIgnoreCase("has_access")) {
            if (currentPlot == null) {
                return "false";
            }
            return currentPlot.hasAccess(onlinePlayer.getUniqueId()) ? "true" : "false";
        }
        
        // %voidplots_plot_id%
        if (params.equalsIgnoreCase("plot_id")) {
            if (currentPlot == null) {
                return "none";
            }
            PlotId id = currentPlot.getId();
            return id.getX() + ";" + id.getZ();
        }
        
        // %voidplots_plot_world%
        if (params.equalsIgnoreCase("plot_world")) {
            if (currentPlot == null) {
                return "none";
            }
            return onlinePlayer.getWorld().getName();
        }
        
        return null;
    }
}
