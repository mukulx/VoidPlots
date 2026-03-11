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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FlagCommand implements SubCommand {
    
    private final VoidPlots plugin;
    private static final List<String> VALID_FLAGS = Arrays.asList(
        "pvp", "mob-spawning", "animal-spawning", "explosions", "weather", "time"
    );
    
    public FlagCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("voidplots.flag")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        PlotId plotId = plugin.getPlotManager().getPlotIdAt(player.getLocation());
        if (plotId == null) {
            plugin.getMessageManager().sendMessage(sender, "plot.claim.not-in-plot");
            return;
        }
        
        Plot plot = plugin.getPlotManager().getPlot(plotId);
        if (plot == null || !plot.isOwner(player.getUniqueId())) {
            plugin.getMessageManager().sendMessage(sender, "plot.flag.not-owner");
            return;
        }
        
        if (args.length == 0) {
            // Show all flags
            sender.sendMessage("§8§m----------§r §bPlot Flags §8§m----------");
            for (String flag : VALID_FLAGS) {
                boolean value = plot.getFlag(flag);
                String status = value ? "§a✓ Enabled" : "§c✗ Disabled";
                sender.sendMessage("§e" + formatFlagName(flag) + "§7: " + status);
            }
            sender.sendMessage("§7Use §e/plot flag <flag> <true/false>§7 to change");
            sender.sendMessage("§8§m--------------------------------");
            return;
        }
        
        if (args.length < 2) {
            // Show specific flag
            String flag = args[0].toLowerCase();
            if (!VALID_FLAGS.contains(flag)) {
                plugin.getMessageManager().sendMessage(sender, "plot.flag.invalid");
                return;
            }
            
            boolean value = plot.getFlag(flag);
            sender.sendMessage("§e" + formatFlagName(flag) + "§7: " + (value ? "§aEnabled" : "§cDisabled"));
            return;
        }
        
        // Set flag
        String flag = args[0].toLowerCase();
        if (!VALID_FLAGS.contains(flag)) {
            plugin.getMessageManager().sendMessage(sender, "plot.flag.invalid");
            return;
        }
        
        // Check specific flag permission
        if (!player.hasPermission("voidplots.flag." + flag) && !player.hasPermission("voidplots.admin")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return;
        }
        
        boolean value;
        String valueStr = args[1].toLowerCase();
        if (valueStr.equals("true") || valueStr.equals("on") || valueStr.equals("yes")) {
            value = true;
        } else if (valueStr.equals("false") || valueStr.equals("off") || valueStr.equals("no")) {
            value = false;
        } else {
            sender.sendMessage("§cInvalid value! Use: true/false, on/off, yes/no");
            return;
        }
        
        plot.setFlag(flag, value);
        plugin.getPlotManager().savePlot(plot);
        
        plugin.getMessageManager().sendMessage(sender, "plot.flag.set",
            Map.of("flag", formatFlagName(flag), "value", value ? "enabled" : "disabled"));
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return VALID_FLAGS;
        }
        if (args.length == 2) {
            return Arrays.asList("true", "false", "on", "off");
        }
        return List.of();
    }
    
    private String formatFlagName(String flag) {
        String[] parts = flag.split("-");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return formatted.toString();
    }
}
