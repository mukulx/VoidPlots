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

package dev.mukulx.voidplots.commands;

import dev.mukulx.voidplots.VoidPlots;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VoidPlotsCommand implements CommandExecutor, TabCompleter {
    
    private final VoidPlots plugin;
    
    public VoidPlotsCommand(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("voidplots.admin")) {
            plugin.getMessageManager().sendMessage(sender, "general.no-permission");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "createworld":
                handleCreateWorld(sender, args);
                break;
            case "deleteworld":
                handleDeleteWorld(sender, args);
                break;
            case "tp":
            case "teleport":
                handleTeleportWorld(sender, args);
                break;
            case "setowner":
                handleSetOwner(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(@NotNull CommandSender sender) {
        plugin.reload();
        plugin.getMessageManager().sendMessage(sender, "general.reload-success");
    }
    
    private void handleCreateWorld(@NotNull CommandSender sender, @NotNull String[] args) {
        String worldName;
        
        if (args.length >= 2) {
            worldName = args[1];
        } else {
            worldName = plugin.getConfigManager().getWorldName();
        }
        
        if (Bukkit.getWorld(worldName) != null) {
            plugin.getMessageManager().sendMessage(sender, "admin.world-exists");
            return;
        }
        
        WorldCreator creator = new WorldCreator(worldName);
        creator.generator(plugin.getDefaultWorldGenerator(worldName, null));
        World world = creator.createWorld();
        
        if (world != null) {
            // Update config with new world name
            plugin.getConfig().set("world.name", worldName);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            plugin.getMessageManager().sendMessage(sender, "admin.world-created", 
                Map.of("world", worldName));
        }
    }
    
    private void handleDeleteWorld(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /voidplots deleteworld <worldname>");
            return;
        }
        
        String worldName = args[1];
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' does not exist!");
            return;
        }
        
        // Teleport all players out of the world
        World defaultWorld = Bukkit.getWorlds().get(0);
        for (Player player : world.getPlayers()) {
            player.teleport(defaultWorld.getSpawnLocation());
            player.sendMessage("§cYou have been teleported out of " + worldName);
        }
        
        // Unload and delete the world
        boolean unloaded = Bukkit.unloadWorld(world, false);
        
        if (unloaded) {
            // Delete world folder
            java.io.File worldFolder = world.getWorldFolder();
            deleteDirectory(worldFolder);
            
            sender.sendMessage("§aWorld '" + worldName + "' has been deleted!");
            
            // Clear plots from database if it was the plot world
            if (worldName.equals(plugin.getConfigManager().getWorldName())) {
                clearAllPlots();
                sender.sendMessage("§aAll plot data has been cleared from database.");
            }
        } else {
            sender.sendMessage("§cFailed to unload world!");
        }
    }
    
    private void deleteDirectory(java.io.File directory) {
        if (directory.exists()) {
            java.io.File[] files = directory.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    private void clearAllPlots() {
        for (var plot : plugin.getPlotManager().getAllPlots()) {
            plugin.getPlotManager().deletePlot(plot.getId());
        }
    }
    
    private void handleTeleportWorld(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        Player player = (Player) sender;
        String worldName;
        
        if (args.length >= 2) {
            worldName = args[1];
        } else {
            worldName = plugin.getConfigManager().getWorldName();
        }
        
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' does not exist!");
            sender.sendMessage("§7Use §e/voidplots createworld " + worldName + "§7 to create it.");
            return;
        }
        
        player.teleport(world.getSpawnLocation());
        sender.sendMessage("§aTeleported to world: §e" + worldName);
    }
    
    private void handleSetOwner(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "general.player-only");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /voidplots setowner <player>");
            return;
        }
        
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        var plotId = plugin.getPlotManager().getPlotIdAt(player.getLocation());
        if (plotId == null) {
            sender.sendMessage("§cYou must be standing in a plot!");
            return;
        }
        
        var plot = plugin.getPlotManager().getOrCreatePlot(plotId);
        plot.setOwner(target.getUniqueId());
        plugin.getPlotManager().savePlot(plot);
        
        plugin.getMessageManager().sendMessage(sender, "admin.setowner-success",
            Map.of("player", target.getName()));
    }
    
    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage("§8§m----------§r §5VoidPlots Admin §8§m----------");
        sender.sendMessage("§e/voidplots reload §7- Reload configuration");
        sender.sendMessage("§e/voidplots createworld [name] §7- Create plot world");
        sender.sendMessage("§e/voidplots deleteworld <name> §7- Delete a world");
        sender.sendMessage("§e/voidplots tp [world] §7- Teleport to plot world");
        sender.sendMessage("§e/voidplots setowner <player> §7- Set plot owner");
        sender.sendMessage("§8§m--------------------------------");
    }
    
    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "createworld", "deleteworld", "tp", "teleport", "setowner");
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setowner")) {
                return null; // Return null for player name completion
            }
            if (args[0].equalsIgnoreCase("deleteworld") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                // Return list of worlds
                return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        
        return Collections.emptyList();
    }
}
