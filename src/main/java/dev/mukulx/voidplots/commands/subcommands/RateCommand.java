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
import dev.mukulx.voidplots.gui.RatingGUI;
import dev.mukulx.voidplots.models.Plot;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RateCommand implements SubCommand {
    
    private final VoidPlots plugin;
    
    public RateCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
        public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
            if (!(sender instanceof Player)) {
                plugin.getMessageManager().sendMessage(sender, "general.player-only");
                return;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("voidplots.rate")) {
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

            // Cannot rate own plot
            if (plot.isOwner(player.getUniqueId())) {
                plugin.getMessageManager().sendMessage(sender, "plot.rate.own-plot");
                return;
            }

            // If no args, open GUI
            if (args.length == 0) {
                RatingGUI gui = new RatingGUI(plugin, plot);
                player.openInventory(gui.getInventory());
                return;
            }

            // Parse rating
            int rating;
            try {
                rating = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid rating! Use 1-5 or open GUI with /plot rate");
                return;
            }

            if (rating < 1 || rating > 5) {
                sender.sendMessage("§cRating must be between 1 and 5!");
                return;
            }

            // Check if already rated
            if (plot.hasRated(player.getUniqueId())) {
                plugin.getMessageManager().sendMessage(sender, "plot.rate.already-rated");
                return;
            }

            // Add rating
            plot.addRating(player.getUniqueId(), rating);
            plugin.getPlotManager().savePlot(plot);

            String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
            plugin.getMessageManager().sendMessage(sender, "plot.rate.success",
                Map.of("rating", stars + " (" + rating + "/5)"));

            // Notify plot owner
            plugin.getNotificationManager().notifyPlotRated(plot, player, rating);

            // Show new average
            double avg = plot.getAverageRating();
            sender.sendMessage(String.format("§7Plot average: §e%.1f§7/5 §8(§7%d ratings§8)", 
                avg, plot.getRatingCount()));
        }

    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("1", "2", "3", "4", "5");
        }
        return List.of();
    }
}
