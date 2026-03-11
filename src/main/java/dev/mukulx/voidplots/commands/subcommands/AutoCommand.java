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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AutoCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public AutoCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.auto")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        if (!plugin.getPlotManager().canClaimMore(player.getUniqueId(), player.getWorld().getName())) {
            int maxPlots = plugin.getConfigManager().getMaxPlotsPerPlayer();
            plugin.getMessageManager().sendMessage(sender, "plot.claim.max-plots",
                Map.of("max", String.valueOf(maxPlots)));
            return;
        }
        
        PlotId freePlotId = plugin.getPlotManager().findFreePlot();
        if (freePlotId == null) {
            plugin.getMessageManager().sendMessage(sender, "plot.auto.no-free-plots");
            return;
        }
        
        Plot plot = plugin.getPlotManager().getOrCreatePlot(freePlotId);
        plot.setOwner(player.getUniqueId());
        plugin.getPlotManager().savePlot(plot);
        
        World world = Bukkit.getWorld(plugin.getConfigManager().getWorldName());
        if (world != null) {
            Location home = plugin.getPlotManager().getPlotHome(freePlotId, world);
            player.teleport(home);
        }
        
        plugin.getMessageManager().sendMessage(sender, "plot.auto.success",
            Map.of("plot", freePlotId.toString()));
    }
}
