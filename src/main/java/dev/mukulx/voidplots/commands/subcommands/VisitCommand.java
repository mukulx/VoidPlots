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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class VisitCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public VisitCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.visit")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(sender, "general.invalid-args",
                Map.of("usage", "/plot visit <player>"));
            return;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        List<Plot> plots = plugin.getPlotManager().getPlayerPlots(target.getUniqueId());
        
        if (plots.isEmpty()) {
            plugin.getMessageManager().sendMessage(sender, "plot.visit.not-found");
            return;
        }
        
        Plot plot = plots.get(0);
        World world = Bukkit.getWorld(plugin.getConfigManager().getWorldName());
        if (world != null) {
            Location home = plugin.getPlotManager().getPlotHome(plot.getId(), world);
            player.teleport(home);
            plugin.getMessageManager().sendMessage(sender, "plot.visit.success",
                Map.of("owner", target.getName() != null ? target.getName() : "Unknown"));
        }
    }
}
