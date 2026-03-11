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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ListCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public ListCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.list")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        List<Plot> plots = plugin.getPlotManager().getAllPlots();
        int page = 1;
        int pageSize = 10;
        
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int maxPage = (int) Math.ceil((double) plots.size() / pageSize);
        page = Math.max(1, Math.min(page, maxPage));
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, plots.size());
        
        plugin.getMessageManager().sendMessage(sender, "plot.list.header");
        
        for (int i = startIndex; i < endIndex; i++) {
            Plot plot = plots.get(i);
            String ownerName = plot.getOwner() != null ? 
                Bukkit.getOfflinePlayer(plot.getOwner()).getName() : "Unclaimed";
            
            plugin.getMessageManager().sendMessage(sender, "plot.list.entry",
                Map.of("id", plot.getId().toString(), "owner", ownerName != null ? ownerName : "Unknown"));
        }
        
        plugin.getMessageManager().sendMessage(sender, "plot.list.footer",
            Map.of("page", String.valueOf(page), "maxpage", String.valueOf(maxPage)));
    }
}
