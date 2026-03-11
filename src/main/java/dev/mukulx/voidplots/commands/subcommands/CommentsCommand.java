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
import dev.mukulx.voidplots.models.PlotComment;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentsCommand implements SubCommand {
    
    private final VoidPlots plugin;
    private static final int COMMENTS_PER_PAGE = 5;
    
    public CommentsCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.comments")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(player.getLocation());
        if (plotId == null) {
            plugin.getMessageManager().sendMessage(sender, "plot.claim.not-in-plot");
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.isClaimed()) {
            sender.sendMessage("§cThis plot is not claimed!");
            return;
        }
        
        List<PlotComment> comments = plot.getComments();
        
        if (comments.isEmpty()) {
            plugin.getMessageManager().sendMessage(sender, "plot.comment.no-comments");
            return;
        }
        
        // Parse page number
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int totalPages = (int) Math.ceil((double) comments.size() / COMMENTS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));
        
        int startIndex = (page - 1) * COMMENTS_PER_PAGE;
        int endIndex = Math.min(startIndex + COMMENTS_PER_PAGE, comments.size());
        
        // Display header
        sender.sendMessage("§8§m----------§r §bPlot Comments §8§m----------");
        sender.sendMessage("§7Page §e" + page + "§7/§e" + totalPages);
        sender.sendMessage("");
        
        // Display comments
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        for (int i = startIndex; i < endIndex; i++) {
            PlotComment comment = comments.get(i);
            String date = dateFormat.format(new Date(comment.getTimestamp()));
            
            sender.sendMessage("§e" + comment.getAuthorName() + " §8- §7" + date);
            sender.sendMessage("§f" + comment.getMessage());
            sender.sendMessage("");
        }
        
        sender.sendMessage("§8§m----------------------------------");
        
        if (page < totalPages) {
            sender.sendMessage("§7Use §e/plot comments " + (page + 1) + "§7 for next page");
        }
    }
}
