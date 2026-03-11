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

package dev.mukulx.voidplots.generator;

import dev.mukulx.voidplots.VoidPlots;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class PlotGenerator extends ChunkGenerator {
    
    private final VoidPlots plugin;
    
    public PlotGenerator(@NotNull VoidPlots plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        int plotSize = plugin.getConfigManager().getPlotSize();
        int roadWidth = plugin.getConfigManager().getRoadWidth();
        int wallHeight = plugin.getConfigManager().getWallHeight();
        
        Material fillBlock = plugin.getConfigManager().getFillBlock();
        Material bottomBlock = plugin.getConfigManager().getBottomBlock();
        List<Material> roadBlocks = plugin.getConfigManager().getRoadBlocks();
        List<Material> borderBlocks = plugin.getConfigManager().getBorderBlocks();
        
        int totalSize = plotSize + roadWidth;
        int minHeight = worldInfo.getMinHeight();
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                
                // Calculate position within plot grid
                int modX = Math.floorMod(worldX, totalSize);
                int modZ = Math.floorMod(worldZ, totalSize);
                
                // Set bedrock at bottom
                chunkData.setBlock(x, minHeight, z, bottomBlock);
                
                // Determine if this is a road or plot
                boolean isRoadX = modX >= plotSize;
                boolean isRoadZ = modZ >= plotSize;
                boolean isBorderX = modX == plotSize - 1 || modX == 0;
                boolean isBorderZ = modZ == plotSize - 1 || modZ == 0;
                
                if (isRoadX || isRoadZ) {
                    // Road - fill from bedrock to wall height
                    Material roadBlock = roadBlocks.get(0);
                    for (int y = minHeight + 1; y <= wallHeight; y++) {
                        chunkData.setBlock(x, y, z, roadBlock);
                    }
                } else if (isBorderX || isBorderZ) {
                    // Border wall - stone brick walls
                    Material borderBlock = borderBlocks.get(0);
                    
                    // Fill base with stone
                    for (int y = minHeight + 1; y < wallHeight - 2; y++) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    }
                    
                    // Top layers with border block
                    for (int y = wallHeight - 2; y <= wallHeight + 1; y++) {
                        chunkData.setBlock(x, y, z, borderBlock);
                    }
                } else {
                    // Plot interior - layered terrain
                    // Stone base
                    for (int y = minHeight + 1; y < wallHeight - 4; y++) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    }
                    
                    // Dirt layers
                    for (int y = wallHeight - 4; y < wallHeight - 1; y++) {
                        chunkData.setBlock(x, y, z, Material.DIRT);
                    }
                    
                    // Top layer with fill block (grass)
                    chunkData.setBlock(x, wallHeight - 1, z, fillBlock);
                }
            }
        }
    }
    
    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }
    
    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }
    
    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }
    
    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }
    
    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }
    
    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}
