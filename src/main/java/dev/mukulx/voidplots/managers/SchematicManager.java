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

package dev.mukulx.voidplots.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.mukulx.voidplots.VoidPlots;
import dev.mukulx.voidplots.models.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class SchematicManager {
    
    private final VoidPlots plugin;
    private final File schematicsFolder;
    
    public SchematicManager(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
        this.schematicsFolder = new File(plugin.getDataFolder(), "schematics");
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
        }
    }
    
    public boolean isWorldEditAvailable() {
        return Bukkit.getPluginManager().getPlugin("WorldEdit") != null ||
               Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
    }
    
    public void downloadPlot(@NotNull PlotId plotId, @NotNull String worldName, @NotNull String fileName) throws IOException, WorldEditException {
        if (!isWorldEditAvailable()) {
            throw new IllegalStateException("WorldEdit is not installed!");
        }
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldName);
        }
        
        // Calculate plot boundaries
        int plotSize = plugin.getConfigManager().getPlotSize();
        int roadWidth = plugin.getConfigManager().getRoadWidth();
        int totalSize = plotSize + roadWidth;
        int wallHeight = plugin.getConfigManager().getWallHeight();
        
        int startX = plotId.getX() * totalSize;
        int startZ = plotId.getZ() * totalSize;
        int endX = startX + plotSize - 1;
        int endZ = startZ + plotSize - 1;
        
        // Create WorldEdit region
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        BlockVector3 min = BlockVector3.at(startX, world.getMinHeight(), startZ);
        BlockVector3 max = BlockVector3.at(endX, wallHeight + 10, endZ);
        CuboidRegion region = new CuboidRegion(weWorld, min, max);
        
        // Create clipboard
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(min);
        
        // Copy blocks to clipboard
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(
                editSession, region, clipboard, region.getMinimumPoint()
            );
            copy.setCopyingEntities(true);
            copy.setCopyingBiomes(false);
            Operations.complete(copy);
        }
        
        // Save to file
        File file = new File(schematicsFolder, fileName + ".schem");
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        }
    }
    
    public void uploadPlot(@NotNull PlotId plotId, @NotNull String worldName, @NotNull String fileName) throws IOException, WorldEditException {
        if (!isWorldEditAvailable()) {
            throw new IllegalStateException("WorldEdit is not installed!");
        }
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + worldName);
        }
        
        // Load schematic
        File file = new File(schematicsFolder, fileName + ".schem");
        if (!file.exists()) {
            // Try .schematic extension
            file = new File(schematicsFolder, fileName + ".schematic");
            if (!file.exists()) {
                throw new IOException("Schematic not found: " + fileName);
            }
        }
        
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IOException("Unknown schematic format!");
        }
        
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }
        
        // Calculate plot position
        int plotSize = plugin.getConfigManager().getPlotSize();
        int roadWidth = plugin.getConfigManager().getRoadWidth();
        int totalSize = plotSize + roadWidth;
        int wallHeight = plugin.getConfigManager().getWallHeight();
        
        int startX = plotId.getX() * totalSize;
        int startZ = plotId.getZ() * totalSize;
        
        // Paste schematic
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation operation = new ClipboardHolder(clipboard)
                .createPaste(editSession)
                .to(BlockVector3.at(startX, world.getMinHeight(), startZ))
                .ignoreAirBlocks(false)
                .build();
            Operations.complete(operation);
        }
    }
    
    @NotNull
    public File getSchematicsFolder() {
        return schematicsFolder;
    }
    
    public boolean schematicExists(@NotNull String fileName) {
        File file = new File(schematicsFolder, fileName + ".schem");
        if (file.exists()) return true;
        
        file = new File(schematicsFolder, fileName + ".schematic");
        return file.exists();
    }
    
    @NotNull
    public String[] listSchematics() {
        File[] files = schematicsFolder.listFiles((dir, name) -> 
            name.endsWith(".schem") || name.endsWith(".schematic"));
        
        if (files == null || files.length == 0) {
            return new String[0];
        }
        
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            // Remove extension
            names[i] = name.substring(0, name.lastIndexOf('.'));
        }
        
        return names;
    }
}
