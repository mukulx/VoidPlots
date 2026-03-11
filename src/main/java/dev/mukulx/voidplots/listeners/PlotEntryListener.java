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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class PlotEntryListener implements Listener {

    private final VoidPlots plugin;

    public PlotEntryListener(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        // Only check if player moved to a different plot
        PlotId fromPlot = plugin.getPlotManager().getPlotIdAt(event.getFrom());
        PlotId toPlot = plugin.getPlotManager().getPlotIdAt(event.getTo());

        if (fromPlot != null && fromPlot.equals(toPlot)) {
            return; // Still in same plot
        }

        if (toPlot == null) {
            return; // Not entering a plot
        }

        Player player = event.getPlayer();

        // Bypass for admins
        if (player.hasPermission("voidplots.admin.bypass")) {
            return;
        }

        Plot plot = plugin.getPlotManager().getPlot(toPlot);
        if (plot == null) return;

        // Check if player is banned
        if (plot.isDenied(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(player, "plot.ban.cannot-enter");
            return;
        }

        // Notify plot owner of visit
        if (plot.isClaimed() && !plot.isOwner(player.getUniqueId())) {
            plugin.getNotificationManager().notifyPlotVisit(plot, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        PlotId plotId = plugin.getPlotManager().getPlotIdAt(to);
        if (plotId == null) return;

        Player player = event.getPlayer();

        // Bypass for admins
        if (player.hasPermission("voidplots.admin.bypass")) {
            return;
        }

        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null) return;

        // Check if player is banned
        if (plot.isDenied(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getMessageManager().sendMessage(player, "plot.ban.cannot-enter");
            return;
        }

        // Notify plot owner of visit
        if (plot.isClaimed() && !plot.isOwner(player.getUniqueId())) {
            plugin.getNotificationManager().notifyPlotVisit(plot, player);
        }
    }
}

