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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public TopCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.top")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        // Get all claimed plots with ratings
        List<Plot> plots = plugin.getPlotManager().getAllPlots().stream()
            .filter(Plot::isClaimed)
            .filter(p -> p.getRatingCount() > 0)
            .sorted(Comparator.comparingDouble(Plot::getAverageRating).reversed())
            .collect(Collectors.toList());
        
        if (plots.isEmpty()) {
            sender.sendMessage("§cNo rated plots found!");
            return;
        }
        
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
        
        sender.sendMessage("§8§m----------§r §6⭐ Top Rated Plots ⭐ §8§m----------");
        
        for (int i = startIndex; i < endIndex; i++) {
            Plot plot = plots.get(i);
            int rank = i + 1;
            
            String ownerName = plot.getOwner() != null ? 
                Bukkit.getOfflinePlayer(plot.getOwner()).getName() : "Unknown";
            
            double rating = plot.getAverageRating();
            int ratingCount = plot.getRatingCount();
            
            String stars = getStarDisplay(rating);
            String medal = getMedal(rank);
            
            sender.sendMessage(String.format("§e#%d %s §7%s §8- §f%s §7(%d ratings)",
                rank, medal, ownerName, stars, ratingCount));
        }
        
        sender.sendMessage(String.format("§7Page §e%d§7/§e%d §8| §7Use §e/plot top <page>", 
            page, maxPage));
        sender.sendMessage("§8§m----------------------------------------");
    }
    
    private String getStarDisplay(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;
        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
        
        StringBuilder stars = new StringBuilder("§6");
        stars.append("★".repeat(fullStars));
        if (halfStar) stars.append("§e⯨");
        stars.append("§8").append("☆".repeat(emptyStars));
        stars.append(String.format(" §e%.1f§7/5", rating));
        
        return stars.toString();
    }
    
    private String getMedal(int rank) {
        return switch (rank) {
            case 1 -> "§6🥇";
            case 2 -> "§7🥈";
            case 3 -> "§c🥉";
            default -> "§8▪";
        };
    }
}
