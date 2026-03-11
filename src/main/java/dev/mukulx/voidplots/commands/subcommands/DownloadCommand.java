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

package dev.mukulx.voidplots.commands.subcommands;

import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.commands.SubCommand;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DownloadCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public DownloadCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.download")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        // Check if WorldEdit is available
        if (!plugin.getSchematicManager().isWorldEditAvailable()) {
            sender.sendMessage("§cWorldEdit or FastAsyncWorldEdit is required for this feature!");
            sender.sendMessage("§7Please install WorldEdit to use schematics.");
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(player.getLocation());
        if (plotId == null) {
            plugin.getMessageManager().sendMessage(sender, "plot.claim.not-in-plot");
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.isOwner(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(sender, "plot.schematic.not-owner");
            return;
        }
        
        // Generate filename
        String fileName = args.length > 0 ? args[0] : 
            "plot_" + plotId.toString().replace(";", "_") + "_" + System.currentTimeMillis();
        
        // Sanitize filename
        final String finalFileName = fileName.replaceAll("[^a-zA-Z0-9_-]", "_");
        final PlotId finalPlotId = plotId;
        final String worldName = player.getWorld().getName();
        
        sender.sendMessage("§eDownloading plot schematic...");
        sender.sendMessage("§7This may take a moment for large plots.");
        
        // Run async to avoid blocking
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getSchematicManager().downloadPlot(finalPlotId, worldName, finalFileName);
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getMessageManager().sendMessage(sender, "plot.schematic.download-success",
                        Map.of("file", finalFileName + ".schem"));
                    sender.sendMessage("§7Location: §e" + plugin.getSchematicManager().getSchematicsFolder().getAbsolutePath());
                });
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to download plot schematic: " + e.getMessage());
                e.printStackTrace();
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getMessageManager().sendMessage(sender, "plot.schematic.download-failed",
                        Map.of("error", e.getMessage()));
                });
            }
        });
    }
}
