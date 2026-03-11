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
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlotFlagListener implements Listener {
    
    private final VoidPlots plugin;
    
    public PlotFlagListener(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPvP(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Player victim = (Player) event.getEntity();
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(victim.getLocation());
        
        if (plotId == null) return;
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null) return;
        
        // Check PvP flag
        if (!plot.getFlag("pvp")) {
            event.setCancelled(true);
            Player damager = (Player) event.getDamager();
            damager.sendMessage("§cPvP is disabled in this plot!");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMobSpawn(@NotNull CreatureSpawnEvent event) {
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getLocation());
        if (plotId == null) return;
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null) return;
        
        // Check mob spawning flag
        if (event.getEntity() instanceof Monster) {
            if (!plot.getFlag("mob-spawning")) {
                event.setCancelled(true);
            }
        }
        
        // Check animal spawning flag
        if (event.getEntity() instanceof Animals) {
            if (!plot.getFlag("animal-spawning")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplosion(@NotNull EntityExplodeEvent event) {
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getLocation());
        if (plotId == null) return;
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null) return;
        
        // Check explosions flag
        if (!plot.getFlag("explosions")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        // Only check if player moved to a different block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(event.getTo());
        
        if (plotId == null) return;
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null) return;
        
        // Apply weather flag
        if (plot.getFlag("weather")) {
            player.setPlayerWeather(org.bukkit.WeatherType.CLEAR);
        } else {
            player.resetPlayerWeather();
        }
        
        // Apply time flag
        if (plot.getFlag("time")) {
            player.setPlayerTime(6000, false); // Noon
        } else {
            player.resetPlayerTime();
        }
    }
}
